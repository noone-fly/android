package com.example.chenpiyang.videogallery;

import android.Manifest;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoGalleryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final int READ_EXTERNAL_STORAGE_PERMISSION = 0x01;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);
        checkSelfPermissions();

        //取消严格模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build() );
        }

        ListView listView = (ListView)this.findViewById(R.id.ListView);
        //从MediaStore.Video.Thumbnails 获取列的列表
        String[] thumbColums = {MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID};
        //MediaStore.Video.Media
        String[] mediaColumns = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE
        };
        //主查询中获取所有视频
        cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null,null,null);

        //查询返回的每一行都在下面的 ArrayList中创建一个条目，每个条目都是一个 VideoViewInfo 对象, 保存视频信息
        ArrayList<VideoViewInfo> videoRows = new ArrayList<VideoViewInfo>();

        //接下来遍历cursor,  为每行数据创建一个VideoViewInfo， 然后保存在ArrayList
        if (cursor.moveToFirst()){
            do {
                VideoViewInfo videoViewInfo = new VideoViewInfo();
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                Log.v("Videogallary","id " + id);
                //提取缩略图的查询， 存入VideoViewInfo
                Cursor thumbCursor = managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColums , MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id , null, null);
                if (thumbCursor.moveToFirst()){
                    //提取缩略图
                    videoViewInfo.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
                    Log.v("Videogallary","thumbPath " + videoViewInfo.thumbPath);

                }
                videoViewInfo.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                videoViewInfo.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                Log.v("Videogallary","Title " + videoViewInfo.title);
                videoViewInfo.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                Log.v("Videogallary","mime type " + videoViewInfo.mimeType);
                videoRows.add(videoViewInfo);
            }while (cursor.moveToNext());
        }

        //获取所有数据，接下来将ListView对象的适配器设置为 VideoGalleryAdapter 的一个新实例，
        // 同时设置此活动为 ListView 的 OnItemClickListener
        listView.setAdapter(new VideoGalleryAdapter(this, videoRows));
        listView.setOnItemClickListener(this);
    }

    //当单击 ListView 的条目，触发此回调方法，
    //从Cursor获取所有数据， 同时创建一个意图，来启动默认播放器来播放视频，
    //可以使用MediaPlayer 这里使用VideoView
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (cursor.moveToFirst()){
            if (cursor.moveToPosition(position)) {
                int fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE);
                String videoFilePath = cursor.getString(fileColumn);
                String videoMimeType = cursor.getString(mimeColumn);
                Log.v("onItemClick", "fileColumn " + videoFilePath);
                Log.v("onItemClick", "mimeColumn " + videoMimeType);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                File newfile = new File(videoFilePath);
                intent.setDataAndType(Uri.fromFile(newfile), videoMimeType);
                startActivity(intent);
            }
        }
    }

    // 动态申请摄像头，麦克风，外部存储
    private boolean checkSelfPermissions() {
        return  checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_PERMISSION);
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode,  String permissions[],  int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You have the permission write storage to your mobile device.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            }
        }
    }
}

class VideoViewInfo{
    String filePath;
    String mimeType;
    String thumbPath;
    String title;
}


