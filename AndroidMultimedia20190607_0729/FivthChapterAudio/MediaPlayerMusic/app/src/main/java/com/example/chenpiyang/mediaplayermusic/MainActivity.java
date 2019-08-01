package com.example.chenpiyang.mediaplayermusic;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,View.OnTouchListener,View.OnClickListener {

    MediaPlayer mediaPlayer;
    View theView;
    Button stopButton, startButton;
    //声明一个变量，包含音频文件中的保存位置，后续需要使用这个位置确定从哪里开始播放音频文件
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button)this.findViewById(R.id.StartButton);
        stopButton = (Button)this.findViewById(R.id.StopButton);
        theView = this.findViewById(R.id.theview);
        //响应触摸事件
        theView.setOnTouchListener(this);

        mediaPlayer = MediaPlayer.create(this, R.raw.youyuandeni);
        //MediaPlayer实现OnCompletionListener 并通过 setOnCompletionListener方法注册的类调用 onCompletion 方法，
        //该操作将在音频文件政治播放时执行
        mediaPlayer.setOnCompletionListener(this);
        //mediaPlayer.start(); //直接开始播放

        startButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mediaPlayer = MediaPlayer.create(this, R.raw.youyuandeni);
//        //MediaPlayer实现OnCompletionListener 并通过 setOnCompletionListener方法注册的类调用 onCompletion 方法，
//        //该操作将在音频文件政治播放时执行
//        mediaPlayer.setOnCompletionListener(this);
//        mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    //每当MediaPlayer完成播放时都会调用它，
    //目前先start播放音频， 然后调seekTo 方法定位到保存的位置，
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        mediaPlayer.seekTo(position);
    }

    /**
     * @param view
     * @param motionEvent
     * @return
     * 当用户触发一个触摸事件时，将会调用onTouch方法，该方法只关注 ACTION_MOVE 触摸事件，
     * 当用户在 View 对象表面移动手指时触发该事件， 此时要确保MediaPlayer正在播放， 然后根据触摸事件在屏幕上发送的位置计算出应定位到哪里
     *
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
            position = (int)(motionEvent.getX() * mediaPlayer.getDuration()/theView.getWidth());
            Log.v("seek",""+position);
            mediaPlayer.seekTo(position);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view==stopButton){
            mediaPlayer.pause();
        }else if (view==startButton){
            mediaPlayer.start();
        }
    }
}
