package com.example.chenpiyang.customrecorderexp;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class CustomRecorder extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener{

    private static final int RECORD_AUDIO_PERMISSION = 0x01;
    private static final int WRITE_EXTERNAL_STORAGE = RECORD_AUDIO_PERMISSION +1;
    //amplitudeTextView 显示音频输入的数字振幅
    TextView statusTextView, amplitudeTextView;
    Button startRecording, stopRecording, playRecording, finishRecording;
    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    File audioFile;

    //需要一个名为RecordAmplitude的新类实例，是一个内部类，将在本源代码程序清淡末尾定义， 它使用一个isRecording的boolean
    //当启动 MediaRecorder 时，即为true
    RecordAmplitude recordAmplitude;
    boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_recorder);
        checkSelfPermissions();

        statusTextView = (TextView)this.findViewById(R.id.StatusTextView);
        statusTextView.setText("Ready");

        //使用Textview显示所捕获的音频的当前振幅
        amplitudeTextView = (TextView)this.findViewById(R.id.AmplitudeTextView);
        amplitudeTextView.setText("0");

        stopRecording = (Button)this.findViewById(R.id.StopRecording);
        startRecording = (Button)this.findViewById(R.id.StartRecording);
        playRecording = (Button)this.findViewById(R.id.PlayRecording);
        finishRecording = (Button)this.findViewById(R.id.FinishRecording);

        stopRecording.setOnClickListener(this);
        startRecording.setOnClickListener(this);
        playRecording.setOnClickListener(this);
        finishRecording.setOnClickListener(this);

        //禁用stopRecording, playRecording
        stopRecording.setEnabled(false);
        playRecording.setEnabled(false);
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playRecording.setEnabled(true);
        stopRecording.setEnabled(false);
        startRecording.setEnabled(true);
        statusTextView.setText("Ready");
    }

    @Override
    public void onClick(View view) {
        if (view == finishRecording){
            //结束活动
            finish();
        }else if (view == stopRecording){
            //完成录制时，设置isRecording 布尔值为false，同时调 RecordAmplitude的cancel方法
            isRecording = false;
            recordAmplitude.cancel(true);

            //停止录制并释放mediarecording
            mediaRecorder.stop();
            mediaRecorder.release();

            //把录制的音频存入MediaStore
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.TITLE, "this isnot music");
            contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
            contentValues.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());
            Uri newUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);

            //然后构造一个MediaPlayer，使他准备好播放刚刚录制的音频
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            try {
                mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //设置statusTextView 提醒用户已准备好播放音频文件
            statusTextView.setText("Ready to play");
            playRecording.setEnabled(true);
            stopRecording.setEnabled(false);
            startRecording.setEnabled(true);

        }else if (view == startRecording){
            mediaRecorder = new MediaRecorder();
            //设置音频来源。一般使用麦克风AudioSource.MIC。
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

            /*设置媒体输出格式。OutputFormat.AMR_NB表示窄带格式，
            /* OutputFormat.AMR_WB表示宽带格式，
            /* AAC_ADTS表示高级的音频传输流格式。
            /* 该方法要在setVideoEncoder之前调用，不然调用setAudioEncoder时会报错“java.lang.IllegalStateException”。
            */
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            /* 设置音频编码器。
            /* AudioEncoder.AMR_NB表示窄带编码，
            /* AudioEncoder.AMR_WB表示宽带编码，
            /* AudioEncoder.AAC表示低复杂度的高级编码，
            /* AudioEncoder.HE_AAC表示高效率的高级编码，
            /* AudioEncoder.AAC_ELD表示增强型低延迟的高级编码。
            /* 注意：setAudioEncoder应在setOutputFormat之后执行，否则会出现“setAudioEncoder called in an invalid state(2)”的异常。
            */
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Android/data/com.example.chenpiyang/files/");

            path.mkdirs();

            try {
                audioFile = File.createTempFile("recording", ".3gp", path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.start();

            //开始录制音频时，设置isRecording=true,并创建RecordAmplitude类的新实例
            //由于RecordAmplitude扩展了AsyncTask，因此执行execute方法来启动 RecordAmplitude 的任务运行
            isRecording = true;
            recordAmplitude = new RecordAmplitude();
            recordAmplitude.execute();


            statusTextView.setText("Recording");
            playRecording.setEnabled(false);
            stopRecording.setEnabled(true);
            startRecording.setEnabled(false);
        }else if (view == playRecording){
            mediaPlayer.start();
            statusTextView.setText("Playing");
            playRecording.setEnabled(false);
            stopRecording.setEnabled(false);
            stopRecording.setEnabled(false);
        }
    }

    /**
     * AsyncTask 是android的一个非常好的工具类，提供一个线程，用于执行长期运行的任务，而不用联系用户界面或使应用程序不响应
     * doIntBackground 在一个单独线程上执行，而且只有在对象上调用execute方法时才开始执行，只有isRecording为true
     * 该方法就一直循环，完成操作只有调用publishProgress
     */
    private class RecordAmplitude extends AsyncTask<Void, Integer, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            while (isRecording){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(mediaRecorder.getMaxAmplitude());
            }
            return null;
        }

        //上面调publishProgress会触发如下回调方法，它在主线程上执行，所以可以与用户界面交互，当前，
        // 它会使用从publishProgress方法调用中传递过来的值对amplitudeTextView进行更新
        @Override
        protected void onProgressUpdate(Integer... values) {
            amplitudeTextView.setText(values[0].toString());
        }
    }

    // 动态申请摄像头，麦克风，外部存储
    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION)
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
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
            case WRITE_EXTERNAL_STORAGE: {
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
