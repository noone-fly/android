package com.example.chenpiyang.videoplayerintent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class VideoPlayerIntentActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int READ_EXTERNAL_STORAGE_PERMISSION = 0x01;
    VideoView videoView;
    Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_intent);

        checkSelfPermissions();

        videoView = (VideoView)this.findViewById(R.id.VideoView);
        videoView.setMediaController(new MediaController(this));
        File extStore = Environment.getExternalStorageDirectory();
        String mPath = extStore.getAbsolutePath() + "/1554607766517793.mp4";
        Uri data = Uri.parse(mPath);
        // Uri data = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/1554607766517793.mp4");
        videoView.setVideoURI(data);

        videoView.start();

        playButton = (Button)this.findViewById(R.id.PlayButton);
        playButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/1554607766517793.mp4");
        intent.setDataAndType(data, "video/mp4");
        startActivity(intent);
    }

    // 动态申请摄像头，麦克风，外部存储
    private boolean checkSelfPermissions() {
        return  checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_PERMISSION);
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
            case READ_EXTERNAL_STORAGE_PERMISSION: {
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

