package com.example.chenpiyang.snapshot;


import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private static final int WRITE_PERMISSION = 0x01;
    private static final int CAMERA_PERMISSION = WRITE_PERMISSION + 1;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Camera默认预览画面是逆时针旋转90度，最简单的办法是把activity设置为横向模式显示
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //android6.0运行时申请权限
        requestWritePermission();

        surfaceView = (SurfaceView)this.findViewById(R.id.CameraView);
        surfaceHolder = surfaceView.getHolder();
        //设置该surface是一个"推送"类型的Surface，意味着在Surface本身的外部维持绘图缓冲区
        //在这种情况下，该缓冲区由Camera类管理，推送类型的Surface是Camera预览所需的Surface
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //告诉SurfaceHolder使用该活动作为回调处理程序
        surfaceHolder.addCallback(this);
    }

    //实现 SurfaceHolder.Callback，使得创建，修改以及销毁该Surface时，activity将会获得通知，
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //当创建Surface  由于SurfaceHolder.Callback 存在， 它将在代码中触发调用 surfaceCreated 方法， 此时可以通过调用Camera的静态方法 open 获取 camera对象
        camera = Camera.open();
        try {

            Camera.Parameters parameters = camera.getParameters();

            //过度曝光
            List<String> colorEffects = parameters.getSupportedColorEffects();
            Iterator<String> cei = colorEffects.iterator();
            while (cei.hasNext()){
                String currentEffect = cei.next();
                Log.v("snapshot","Checking " + currentEffect);
                if(currentEffect.equals(Camera.Parameters.EFFECT_SEPIA)){
                    Log.v("snapshot","Using negative");
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
                    break;
                }
            }
            Log.v("snapshot","Using Effect: "+ parameters.getColorEffect());

            /*
            * 使用Parameters请求Camera对象，采用纵向方向而非横向方向
            * 代码首先检查设备配置（通过 Context.getResources().getConfiguration() ）查看当前方向，
            * 如果方向不是横向模式，那么设置 Camera.Parameters的 “orientation” 为 portrait 。
            * 此外，调用 Camera.Parameters 的setRotation方法，并传入 90度的参数，
            * 该方法在 API 5和更高版本上可用，它实际上并不执行任何旋转，
            * 相反，他会告知Camera 对象在EXIF数据中制定该图像应该旋转90度显示，
            * 如果没有包含该信息， 那么在其他应用程序中查看该图像时， 它可能会侧面显示
            * */
            if(this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                parameters.set("orientation", "portrait");
                //android2.2及以上版本
                camera.setDisplayOrientation(90);
                //android2.2及以上版本取消注释
                parameters.setRotation(90);
            }else{
                parameters.set("orientation", "landscape");
                //android2.2及以上版本
                camera.setDisplayOrientation(0);
                //android2.2及以上版本取消注释
                parameters.setRotation(0);
            }
            camera.setParameters(parameters);

            //将预览显示设置为正在使用的SurfaceHolder，它通过回调提供给我们的方法
            // 可能跑出io异常，所以需要释放camera对象，否则将绑定硬件资源，导致其他应用无法使用
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
            camera.release();
        }
        //启动摄像头预览
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //这里也要释放摄像头对象
        //这里也要释放摄像头对象
        camera.stopPreview();
        camera.release();
    }

    // 动态申请摄像头
    private void requestWritePermission(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            //通过ActivityCompat.requestPermissions(activity,permissions,requestCode)第二个参数是一个String数组,第三个参数是请求码便于在onRequestPermissionsResult 方法中根据requestCode进行判断.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v("permission", "Write Permission Failed");
                Toast.makeText(this, "You have the permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(this, "You must allow permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
