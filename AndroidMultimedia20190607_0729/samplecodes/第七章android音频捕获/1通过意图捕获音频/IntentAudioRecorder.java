package com.example.chenpiyang.intentaudiorecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;

public class IntentAudioRecorder extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener {
    private static final int RECORD_AUDIO_PERMISSION = 0x01;
    public static int RECORD_REQUEST = 0;
    Button createRecording, playRecording;
    Uri audioFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_audio_recorder);
        checkSelfPermissions();
        createRecording = (Button)this.findViewById(R.id.RecordButton);
        createRecording.setOnClickListener(this);
        playRecording = (Button)this.findViewById(R.id.PlayButton);
        playRecording.setOnClickListener(this);
        playRecording.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        if (view == createRecording){
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            startActivityForResult(intent, RECORD_REQUEST);
        }else if (view == playRecording){
            MediaPlayer mediaPlayer = MediaPlayer.create(this, audioFileUri);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.start();
            playRecording.setEnabled(false);
        }
    }

    //当MediaPlayer完成播放一个文件时，会调用onCompletion方法，重启play按钮，
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playRecording.setEnabled(true);
    }

    //获取录制的音频文件用于播放
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK && requestCode == RECORD_REQUEST){
            audioFileUri = data.getData();
            Log.v("onActivityResult","recording audio: "+audioFileUri);
            // V/onActivityResult: recording audio: content://media/external/audio/media/60632
            playRecording.setEnabled(true);
        }
    }

    // 动态申请摄像头，麦克风，外部存储
    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION);
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

            case RECORD_AUDIO_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You have the permission Audio to your mobile device.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            }
        }
    }
}
