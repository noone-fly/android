/**
*点击Button 启动一个Intent .  告诉android系统，现在需要选取一块数据
*然后进入onActivityResult ，通过BitmapFactory.decodeStream 打开Uri，获取Bitmap
*然后把bitmap  setImageBitmap到ImageView
*/

package com.example.chenpiyang.previewsize;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener, SurfaceHolder.Callback, Camera.PictureCallback{
    public static final int LARGEST_WIDTH = 200;
    public static final int LARGEST_HEIGTH = 200;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 0x01;
    private static final int CAMERA_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION + 1;
    private static final int RECORD_AUDIO_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION +2;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSelfPermissions();
        surfaceView = (SurfaceView)this.findViewById(R.id.CameraView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

        surfaceView.setFocusable(true);
        surfaceView.setFocusableInTouchMode(true);
        surfaceView.setClickable(true);
        surfaceView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        camera.takePicture(null, null, null, this);
    }

    //Camera.PictureCallback 图片压缩时被调用
    //第一个参数是实际的jpeg图像数据的字节数组，第二个参数是捕获该图像的Camera对象的引用
    //这段代码向MediaStore插入一条新记录，并返回一个URI，然后利用这个URI可以获取一个OutputStream，用于写入jpeg数据，

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {
        //由于给定了实际的jpeg数据， 因此为了保存图像， 需要将其写入磁盘，所以可以使用MediaStore指定图像的位置和元数据
        Uri imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        //更新MediaStore里面的元数据
        ContentValues contentValues = new ContentValues(3);
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME,"this is a test title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"this is a test description");
        getContentResolver().update(imageFileUri, contentValues, null, null);
        try {
            //当执行onPictureTaken方法时，可以调用Camera对象的startPreview
            //当调用takePicture方法时，预览已经自动暂停了，
            OutputStream imageFileOS = getContentResolver().openOutputStream(imageFileUri);
            imageFileOS.write(bytes);
            imageFileOS.flush();
            imageFileOS.close();
        }catch (FileNotFoundException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        camera.stopPreview();
    }

    //SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(surfaceHolder);
            Camera.Parameters parameters = camera.getParameters();
            if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                parameters.set("orientation", "portrait");
                //android2.2及以上版本
                camera.setDisplayOrientation(90);
                //android2.0 以上版本
                parameters.setRotation(90);
            }

            //用于android 2.0以及以上版本的效果
            List<String> colorEffects = parameters.getSupportedColorEffects();
            Iterator<String> cei = colorEffects.iterator();
            while (cei.hasNext()){
                String currentEffect = cei.next();
                if(currentEffect.equals(Camera.Parameters.EFFECT_SOLARIZE)){
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
                }
            }
            //结束android2.0以及更高版本的效果
            camera.setParameters(parameters);


//            int bestWidth = 0;
//            int bestHeight = 0;
//            //获取所支持的所以大小的列表，将返回一个Camera.Size对象的列表
//            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
//            if(previewSizes.size()>1){
//                Iterator<Camera.Size> cei = previewSizes.iterator();
//                while (cei.hasNext()){
//                    Camera.Size aSize = cei.next();
//                    Log.v("snapshot","Checking "+aSize.width+" x " + aSize.height);
//                    if(aSize.width > bestWidth && aSize.width <= LARGEST_WIDTH && aSize.height > bestHeight && aSize.height <= LARGEST_HEIGTH){
//                        //迄今为止，它是最大的大小，且不超过屏幕尺寸
//                        bestHeight = aSize.height;
//                        bestWidth = aSize.width;
//                    }
//                }
//            }

        }catch (IOException e) {
            camera.release();
        }

    }

    //SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        camera.startPreview();
    }

    //SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //这里也要释放摄像头对象
        camera.stopPreview();
        camera.release();
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

    public void onRequestPermissionsResult(int requestCode,  @NonNull String permissions[], @NonNull int[] grantResults) {
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
