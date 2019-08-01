package com.example.chenpiyang.videocapture;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

public class VideoCaptureActivity extends AppCompatActivity implements View.OnClickListener,SurfaceHolder.Callback{

    private static final int RECORD_AUDIO_PERMISSION = 0x01;
    private static final int CAMERA_PERMISSION = RECORD_AUDIO_PERMISSION + 1;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = RECORD_AUDIO_PERMISSION +2;
    MediaRecorder mediaRecorder;
    SurfaceHolder surfaceHolder;
    boolean recording = false;
    public static final String TAG = "VIDEOCAPTURE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkSelfPermissions();

        //此活动以全屏和横向模式运行
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mediaRecorder = new MediaRecorder();

        initRecorder();

        setContentView(R.layout.activity_video_capture);

        SurfaceView cameraView = (SurfaceView)findViewById(R.id.CameraView);
        //获取指向SurfaceView和SurfaceHolder的引用，同时将活动注册为SurfaceHolder.Callback
        surfaceHolder = cameraView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //设置SurfacView可单击，
        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (recording){
            mediaRecorder.stop();
            recording = false;
            Log.v(TAG, "Recording Stopped");
            initRecorder(); // 重新录制
            prepareRecorder();
        } else {
            recording = true;
            mediaRecorder.start();
            Log.v(TAG, "Recording started");
        }
    }

    //
    private void prepareRecorder(){
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRecorder(){
        //使用默认的音频和视频源
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        //
        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mediaRecorder.setProfile(camcorderProfile);

        mediaRecorder.setOutputFile("/sdcard/videocapture_example.mp4");
        mediaRecorder.setMaxDuration(50000); //最长持续时间 50秒
        mediaRecorder.setMaxFileSize(5000000); //最大文件大小，50兆

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.v(TAG, "surfaceCreated");
        prepareRecorder();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.v(TAG, "surfaceDestroyed");
        if (recording){
            mediaRecorder.stop();
            recording = false;
        }
        mediaRecorder.release();
        finish();
    }

    // 动态申请摄像头，麦克风，外部存储
    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION) &&
                checkSelfPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION) &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_PERMISSION);
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
            case CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //如果动态申请摄像机权限，顺便再申请外部存储权限
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_PERMISSION);
                    Toast.makeText(this, "You have the permission camera to your mobile device.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            }
            case WRITE_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You have the permission storage to your mobile device.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            }
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
