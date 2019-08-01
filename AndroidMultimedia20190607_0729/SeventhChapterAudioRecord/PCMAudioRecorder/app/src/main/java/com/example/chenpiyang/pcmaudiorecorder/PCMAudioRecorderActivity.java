package com.example.chenpiyang.pcmaudiorecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PCMAudioRecorderActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int RECORD_AUDIO_PERMISSION = 0x01;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = RECORD_AUDIO_PERMISSION +1;

    //定义两个内部类，一个用于录制，一个用于播放，都扩展AsyncTack
    RecordAudio recordAudio;
    PlayAudio playAudio;
    Button startRecording, stopRecording, startPlayTrack, stopPlayTrack;
    TextView statusText;
    File recordingFile;

    //boolean值用于跟踪录制和播放
    boolean isRecording = false;
    boolean isPlaying = false;

    //AudioRecord, AudioTrack 配置采样率，通道数，音频编码方式
    // 11025 Hz
    int frequency = 11025;
    int channelConfiguration  = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pcmaudio_recorder);

        checkSelfPermissions();

        statusText = (TextView)this.findViewById(R.id.StatusTextView);


        stopRecording = (Button)this.findViewById(R.id.StopRecording);
        startRecording = (Button)this.findViewById(R.id.StartRecording);
        startPlayTrack = (Button)this.findViewById(R.id.StartPlayTrack);
        stopPlayTrack = (Button)this.findViewById(R.id.StopPlayTrack);

        stopRecording.setOnClickListener(this);
        startRecording.setOnClickListener(this);
        startPlayTrack.setOnClickListener(this);
        stopPlayTrack.setOnClickListener(this);

        //禁用按钮
        stopRecording.setEnabled(false);
        startPlayTrack.setEnabled(false);
        stopPlayTrack.setEnabled(false);

        //创建录制和播放的文件

        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.example.chenpiyang.pcmaudiorecorder/files");
        path.mkdirs();

        try {
            recordingFile = File.createTempFile("recording", ".pcm", path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == startRecording){
            record();
        }else if (view == stopRecording){
            stopRecording();
        }else if (view == startPlayTrack){
            play();
        }else if (view == stopPlayTrack){
            stopPlaying();
        }
    }

    public void play(){
        startPlayTrack.setEnabled(true);
        playAudio = new PlayAudio();
        playAudio.execute();
        stopPlayTrack.setEnabled(true);
    }

    public void stopPlaying(){
        isPlaying = false;
        stopPlayTrack.setEnabled(false);
        startPlayTrack.setEnabled(true);
    }

    public void record(){
        startRecording.setEnabled(false);
        stopRecording.setEnabled(true);
        startPlayTrack.setEnabled(true);
        recordAudio = new RecordAudio();
        recordAudio.execute();
    }

    //停止录制，只需要将isRecording 置为false，这将使RecordAudio对象停止循环
    public void stopRecording(){
        isRecording = false;
    }

    private class PlayAudio extends AsyncTask<Void, Integer, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            isPlaying = true;
            //AudioTrack的缓冲区
            int bufferSize = AudioTrack.getMinBufferSize(frequency,channelConfiguration,audioEncoding);
            //short数组用于从AudioRecorder取数据
            short[] audiodata = new short[bufferSize/4];

            try {
                DataInputStream dataInputStream = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(recordingFile)));
                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency,
                        channelConfiguration, audioEncoding, bufferSize, AudioTrack.MODE_STREAM);
                audioTrack.play();
                while (isPlaying && dataInputStream.available() > 0){
                    int i = 0;
                    while (dataInputStream.available() > 0 && i < audiodata.length){
                        audiodata[i] = dataInputStream.readShort();
                        i++;
                    }
                    audioTrack.write(audiodata, 0, audiodata.length);
                }
                dataInputStream.close();

                //错误：子线程例改变了UI
                //Animators may only be run on Looper threads
                //startPlayTrack.setEnabled(false);
                //stopPlayTrack.setEnabled(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.v("Audio track ===== ", "Playback failed");
            }

            return null;
        }
    }

    private class RecordAudio extends AsyncTask<Void, Integer, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            isRecording = true;
            try {
                Log.v("recording file", recordingFile.toString());
                //V/recording file: /storage/emulated/0/Android/data/com.example.chenpiyang.pcmaudiorecorder/files/recording1595766398163264981.pcm
                DataOutputStream dataOutputStream = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(recordingFile)));
                int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        frequency, channelConfiguration, audioEncoding, bufferSize);
                short[] buffer = new short[bufferSize];
                audioRecord.startRecording();

                int r = 0;
                while (isRecording){
                    int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                    for (int i = 0; i < bufferReadResult; i++){
                        dataOutputStream.writeShort(i);
                    }
                    //调此方法，会触发 onProgressUpdate
                    publishProgress(new Integer(r));
                    r++;
                }

                audioRecord.stop();
                dataOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            statusText.setText(values[0].toString());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startRecording.setEnabled(true);
            stopPlayTrack.setEnabled(false);
            startPlayTrack.setEnabled(true);
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
