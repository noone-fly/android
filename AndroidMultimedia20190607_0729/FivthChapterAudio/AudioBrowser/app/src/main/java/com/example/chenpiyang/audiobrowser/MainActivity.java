package com.example.chenpiyang.audiobrowser;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends ListActivity {


    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 0x01;
    private static final int CAMERA_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION + 1;
    private static final int RECORD_AUDIO_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION +2;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION + 3;

    /*
    * 创建几个常量，用于跟踪用户应用程序的位置
    * currentState 变量用于跟踪用户，初始值设为 STATE_SELECT_ALBUM
     */
    public static int STATE_SELECT_ALBUM = 0;
    public static int STATE_SELECT_SONG = 1;
    int currentState = STATE_SELECT_ALBUM;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSelfPermissions();

        //取消严格模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build() );
        }

        String[] columns = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM
        };

        //返回一个所有可用唱片集的列表
        cursor = managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, columns, null, null, null);
        System.out.println("============cursor total=========="+cursor);
        System.out.println("======================"+MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI);
//System.out: ======================content://media/external/audio/albums
        /*
        * 使用ListActivity 自动管理数据列表，可用使用setListAdapter 方法将 Cursor 对象绑定到 ListView 对象。
        * 先创建一个字符串数组，表示将要显示的 Cursor 对象中的列名， 当前只想列出唱片集名称，即 MediaStore.Audio.Albums.ALBUM。
        * 接下来，列出将用来显示来自于这些列的数据的View对象，目前只有一列，故只需要一个View对象，即 android.R.id.text1。
        * android.R.id.text1 是View 对象，即 android.R.layout.simple_list_item_1 布局的一部分。
        * 最后调用 setListAdapter 方法，传入一个内部创建的 SimpleCursorAdapter
        * SimpleCursorAdapter 是一个简单的适配器，它将 Cursor 对象包含的数据转换给 ListActivity
        * 创建 SimpleCursorAdapter 时，传入以下5个参数
        * 1，传入上下文的活动（this）,
        * 2，一个已经定义的标准 ListView 布局 （android.R.layout.simple_list_item_1）
        * 3，包含数据的Cursor对象
        * 4，两个数组 String[] displayFiels， int[] displayViews
         */
        String[] displayFiels = new String[]{MediaStore.Audio.Albums.ALBUM};
        int[] displayViews = new int[]{android.R.id.text1};
        setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, displayFiels, displayViews));
    }

    /*
    * onListItemClick 方法，cursor.moveToPosition(position)获知用户选择哪个唱片集
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //
        if (currentState == STATE_SELECT_ALBUM){
            //
            if (cursor.moveToPosition(position)){
                //想要返回的列
                String[] columns ={
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.MIME_TYPE,
                };
                //
                String where = MediaStore.Audio.Media.ALBUM + "=?";
                String whereVal[] = {cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))};
                //System.out: =========cursor============遥远的你
                System.out.println("=========cursor============"+cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                String orderby = MediaStore.Audio.Media.TITLE;
                //运行managedQuery方法，传入Uri,列，where字句变量，where子句数据，order by子句变量
                cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, where, whereVal,orderby);

                //使用ListActivity的方法管理Cursor对象和展示列表
                String[] displayFields = new String[]{MediaStore.Audio.Media.DISPLAY_NAME};
                int[] displayViews = new int[]{android.R.id.text1};
                setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, displayFields, displayViews));
                //下一次调用该方法时，跳过所有步骤，用户将选定一首歌而不是唱片集
                //点击ListView的项时，触发onListItemClick回调
                currentState = STATE_SELECT_SONG;
            }
        } else if (currentState == STATE_SELECT_SONG){
            //当用户选择唱片集并从列表中选择一首歌时，进入这个分支
            if (cursor.moveToPosition(position)){
                //通过moveToPosition获得实际选择的歌，获取包含文件路径的列以及MIME_type，转换为file对象，并启动内置播放器
                int fileColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int mimeTypeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
                System.out.println("==========fileColumn============"+fileColumn);//System.out: ==========fileColumn============0
                System.out.println("==========mimeTypeColumn============"+mimeTypeColumn);//System.out: ==========mimeTypeColumn============4
                String audioFilePath = cursor.getString(fileColumn);
                String mimeType = cursor.getString(mimeTypeColumn);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File newFile = new File(audioFilePath);
                intent.setDataAndType(Uri.fromFile(newFile), mimeType);
                startActivity(intent);
            }
        }
    }

    // 动态申请摄像头，麦克风，外部存储
    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION) &&
                checkSelfPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION) &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_PERMISSION)
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_PERMISSION);
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果动态申请摄像机权限，顺便再申请外部存储权限
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_PERMISSION);
                    Toast.makeText(this, "You have the permission camera to your mobile device.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            }
            case WRITE_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You have the permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            }
            case RECORD_AUDIO_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You have the permission Audio to your mobile device.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            }
            case READ_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You have the permission read external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            }
        }
    }
}

