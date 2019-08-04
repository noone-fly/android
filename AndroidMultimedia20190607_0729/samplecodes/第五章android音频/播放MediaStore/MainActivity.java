package com.example.chenpiyang.mediastoremusic;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] columns ={
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.IS_RINGTONE,
                MediaStore.Audio.Media.IS_ALARM,
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.IS_NOTIFICATION
        };

        /*
        * 通过活动中的 managedQuery 方法来查询 MediaStore，managedQuery 方法接收内容提供器的Uri作为参数
        * MediaStore.Audio.Media.EXTERNAL_CONTENT_URI 存储在SDcard上面的音频
        * MediaStore.Audio.Media.INTERNAL_CONTENT_URI 存储在内存中的音频
        * 除了指向MediaStore的Uri，managedQuery 还接收想返回的列数组， 一条sql where字句，
        */
        Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
        //这个是包含指向实际音频文件的路径的列索引
        int fileColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        //其他索引
        int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int displayColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        int mineTypeColumn = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);
        //通过moveToFirst检索结果，返回第一个结果，为true , 否则false
        if (cursor.moveToFirst()){
            String audioFilePath = cursor.getString(fileColumn);
            String mineType = cursor.getString(mineTypeColumn);
            Log.v("audio player", audioFilePath);
            Log.v("audio player", mineType);

            //通过意图启动内置音频播放器，
            //意图中需要Uri, 所以需要用 Uri.fromFile(File)
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File newFile = new File(audioFilePath);
            intent.setDataAndType(Uri.fromFile(newFile), mineType);
            startActivity(intent);
        }

        String[] columns1 = {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM};
        Cursor cursor1 = managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, columns1, null, null, null);
        if (cursor1 != null){
            while (cursor1.moveToNext()){
                Log.v("output",cursor1.getString(cursor1.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
            }
        }
    }


}
