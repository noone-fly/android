package com.example.chenpiyang.androidmusic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 0x01;
    private static final int CAMERA_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION + 1;
    private static final int RECORD_AUDIO_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION +2;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION + 3;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSelfPermissions();

        //取消严格模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build() );
        }
        button = (Button)findViewById(R.id.PlayMusic);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File sdcard = Environment.getExternalStorageDirectory();
        System.out.println("===++++==== "+sdcard+" ===========================");//System.out: ===++++==== /storage/emulated/0 ===========================
        //创建File对象， 引用内部存储的音频文件
        File audioFile = new File("/sdcard/youyuandeni.mp3");

        //File audioFile = new File(sdcard.getPath()+"/netease/cloudmusic/Music/徐薇\\ -\\ 云烟成雨\\ -\\ 徐薇（Cover\\ 房东的猫）.mp3");
        //将意图的数据设置为源自音频文件的Uri，类型设置为其MIME类型：audio/mp3，
        intent.setDataAndType(Uri.fromFile(audioFile),"audio/mp3");
        startActivity(intent);
    }

    // 动态申请摄像头，麦克风，外部存储
    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION) &&
                checkSelfPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION) &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_PERMISSION)
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_PERMISSION);
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
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
                    Toast.makeText(this, "You have the permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
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
            case READ_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You have the permission read external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            }
        }
    }

}
