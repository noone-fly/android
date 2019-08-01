package com.example.chenpiyang.backgroudaudiobind;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class BackgroudAudioBind extends AppCompatActivity implements View.OnClickListener{
    Button startPlaybackButton, stopPlaybackButton, haveFunButton;
    Intent playbackServiceIntent;
    private BackgroundAudioBindService baService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backgroud_audio_bind);
        startPlaybackButton = (Button)this.findViewById(R.id.StartPlaybackButton);
        stopPlaybackButton = (Button)this.findViewById(R.id.StopPlaybackButton);
        haveFunButton = (Button)this.findViewById(R.id.HaveFunButton);
        startPlaybackButton.setOnClickListener(this);
        stopPlaybackButton.setOnClickListener(this);
        haveFunButton.setOnClickListener(this);
        playbackServiceIntent = new Intent(this, BackgroundAudioBindService.class);

    }

    @Override
    public void onClick(View view) {
        if (view == startPlaybackButton){
            Log.v("background activity","start service");
            startService(playbackServiceIntent);
            bindService(playbackServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else if(view == stopPlaybackButton){
            Log.v("background activity","start service");
            unbindService(serviceConnection);
            stopService(playbackServiceIntent);
        }else if (view == haveFunButton){
            baService.haveFun();
        }
    }

    //ServiceConnection 用于监控所绑定服务的状态
    //bindService(playbackServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    //通过 bindService 与服务建立连接，并调用 onServiceconnected
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //返回绑定的服务
            baService = ((BackgroundAudioBindService.BackgroundAudioServiceBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            baService = null;
        }
    };
}
