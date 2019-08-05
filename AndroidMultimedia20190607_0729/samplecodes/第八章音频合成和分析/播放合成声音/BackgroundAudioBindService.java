package com.example.chenpiyang.audiosynthesis;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class AudioSynthesis extends AppCompatActivity implements View.OnClickListener{
    private static final int RECORD_AUDIO_PERMISSION = 0x01;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = RECORD_AUDIO_PERMISSION +1;

    Button startSound,endSound;
    AudioSynthesisTask audioSynthesis;
    boolean keepGoing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_synthesis);
        checkSelfPermissions();
        startSound = (Button)this.findViewById(R.id.StartSound);
        startSound.setOnClickListener(this);

        endSound = (Button)this.findViewById(R.id.EndSound);
        endSound.setOnClickListener(this);

        endSound.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        keepGoing = false;
        endSound.setEnabled(false);
        startSound.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if (view == startSound){
            keepGoing = true;
            audioSynthesis = new AudioSynthesisTask();
            audioSynthesis.execute();
            endSound.setEnabled(true);
            startSound.setEnabled(false);
        }else if (view == endSound){
            keepGoing = false;
            endSound.setEnabled(false);
            startSound.setEnabled(true);
        }
    }

    private class AudioSynthesisTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            final int SAMPLE_RATE = 11025;
            int minSize = AudioTrack.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,AudioFormat.ENCODING_PCM_16BIT,
                    minSize,AudioTrack.MODE_STREAM);
            audioTrack.play();

            //音频样本，每个数字代表波形上的一个点
            // 样本从8130 振荡到32695， 向下振荡到 -32121， 然后回到 -466
//            short[] buffer = {8130, 15752, 22389, 27625, 31134, 32695, 32210, 29711, 25354, 19410, 12253,
//            4329, -3865, -11818, -19032, -25055, -29511, -32121, -32722, -31276, -27874, -22728, -16160, -8582, -466};

            //这是一个短波形，十个样本，表示一个高频声音，每秒存在许多振荡
            //而低频声音的波形将会以一个固定的采样率覆盖更多的样本
            short[] buffer = {8130,15752,32695,12253,4329,-3865,-19032,-32722,16160,-466};

            while (keepGoing){
                audioTrack.write(buffer, 0, buffer.length);
            }
            return null;
        }
    }

    // 动态申请摄像头，麦克风，外部存储
    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION)
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_PERMISSION);
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
            case WRITE_EXTERNAL_STORAGE_PERMISSION: {
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