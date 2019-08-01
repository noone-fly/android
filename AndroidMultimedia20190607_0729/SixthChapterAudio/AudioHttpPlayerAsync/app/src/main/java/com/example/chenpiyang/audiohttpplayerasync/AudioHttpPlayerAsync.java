package com.example.chenpiyang.audiohttpplayerasync;

import android.media.MediaPlayer;
import android.media.MediaSync;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class AudioHttpPlayerAsync extends AppCompatActivity implements View.OnClickListener,MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnPreparedListener{

    MediaPlayer mediaPlayer;
    Button stopButton, startButton;
    TextView statusTextView, bufferValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_http_player_async);
        startButton = (Button)this.findViewById(R.id.StartButton);
        stopButton = (Button)this.findViewById(R.id.EndButton);
        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        startButton.setEnabled(false);
        stopButton.setEnabled(false);

        bufferValueTextView = (TextView)this.findViewById(R.id.BufferValueTextView);
        statusTextView = (TextView)this.findViewById(R.id.StatusDisplayTextView);
        statusTextView.setText("onCreate");

        //实例化MediaPlayer对象之后，将把活动注册为OnCompletionListener，OnBufferingUpdateListener，OnPreparedListener
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnPreparedListener(this);

        statusTextView.setText("MediaPlayer created");

        try {
            mediaPlayer.setDataSource("http://imp.qumitech.com/ljW9Xby4Q9CkxVyLwE6Wd-NlJpct");
            statusTextView.setText("setDataSource done");
            statusTextView.setText("calling prepareAsync");
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.v("Audio http player",e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        if (view == stopButton){
            mediaPlayer.pause();
            statusTextView.setText("pause called");
            startButton.setEnabled(true);
        }else if(view == startButton){
            mediaPlayer.start();
            statusTextView.setText("start called");
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
        }
    }

    //MediaPlayer正在缓冲时，调用该方法，并把已经缓冲的百分比显示在TextView
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        bufferValueTextView.setText(""+i+"%");
    }

    //MediaPlayer 准备完之后调用该方法
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        statusTextView.setText("onCompletion called");
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
    }

    //当完成prepareAsync方法时，将调用该方法，表明音频开始播放
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        statusTextView.setText("onPrepared called");
        startButton.setEnabled(true);
    }

    //如果MediaPlayer进入错误状态，
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        statusTextView.setText("onError called");
        switch (i){
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                statusTextView.setText("MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK"+i1);
                Log.v("error","MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK"+i1);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                statusTextView.setText("MEDIA_ERROR_SERVER_DIED"+i1);
                Log.v("error","MEDIA_ERROR_SERVER_DIED"+i1);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                statusTextView.setText("MEDIA_ERROR_UNKNOWN"+i1);
                Log.v("error","MEDIA_ERROR_UNKNOWN"+i1);
                break;
        }
        return false;
    }
}
