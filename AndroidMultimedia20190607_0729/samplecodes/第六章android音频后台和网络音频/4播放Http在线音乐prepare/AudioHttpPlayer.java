package com.example.chenpiyang.audiohttpplayer;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class AudioHttpPlayer extends AppCompatActivity {
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_http_player);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource("http://imp.qumitech.com/ljW9Xby4Q9CkxVyLwE6Wd-NlJpct");
            Log.v("fore prepare","======");
            //07-16 00:07:41.696 30365-30365/? V/fore prepare: ======
            mediaPlayer.prepare();
            Log.v("behind prepare","======");
            //07-16 00:07:46.533 30365-30365/com.example.chenpiyang.audiohttpplayer V/behind prepare: ======
            mediaPlayer.start();
        } catch (IOException e) {
            Log.v("audio http player", e.getMessage());
        }
    }
}
