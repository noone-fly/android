package com.example.chenpiyang.backgroundaudio;

import android.content.Intent;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class BackgroundAudioActivity extends AppCompatActivity implements View.OnClickListener{
    Button startPlaybackButton, stopPlaybackButton;
    Intent playbackServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background_audio);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//            StrictMode.setVmPolicy(builder.build() );
//        }
        startPlaybackButton = (Button)this.findViewById(R.id.StartPlaybackButton);
        stopPlaybackButton = (Button)this.findViewById(R.id.StopPlaybackButton);
        startPlaybackButton.setOnClickListener(this);
        stopPlaybackButton.setOnClickListener(this);
        playbackServiceIntent = new Intent(this, BackgroundAudioService.class);

    }

    @Override
    public void onClick(View view) {
        if (view == startPlaybackButton){
            Log.v("background activity","start service");
            startService(playbackServiceIntent);
            //结束当前activity，退到播放音乐
            finish();
        }else if(view == stopPlaybackButton){
            Log.v("background activity","start service");
            stopService(playbackServiceIntent);
            //结束当前activity，退到播放音乐
            finish();
        }
    }
}
