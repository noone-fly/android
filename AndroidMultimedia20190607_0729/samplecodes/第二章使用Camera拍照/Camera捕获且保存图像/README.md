第一章了解到android内置的Camera为其他应用程序提供了一个现成的照片捕获组件， 但这套标准接口， 没有提供太多灵活性，例如， 支持时间推移摄影，
本章介绍如何利用底层Camera来构建一个照相应用程序，
一个简单的点击拍照应用程序
一个倒计时风格的计时器
一个时间推移照相应用程序


2.1.1 CAMERA权限
<uses-permission android:name="android.permission.CAMERA" />

2.1.2 预览 Surface
开始使用摄像头之前， 需要创建某种类型的Surface(表面)，使得Camera能在其上绘制取景器（viewfinder）或预览图像， Surface 是android中的一个抽象类， 表示绘制图形或图像的位置， 提供一个绘图Surface的简单方法是使用SurfaceView类， 
SurfaceView是标准视图中提供Surface的具体类

为了在布局中指定SurfaceView，只须要在任何普通布局xml中使用<SurfaceView/>元素，

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent" android:layout_height="fill_parent" android:orientation="vertical">

    <SurfaceView android:id="@+id/CameraView"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent" />
</LinearLayout>


为了在代码中实现通过camera类使用此SurfaceView，需要添加一个 SurfaceHolder类
SurfaceHolder类作为Surface上面的一个监控器，通过回调提供接口，让我们知道什么时候创建，销毁或更改Surface
同时SurfaceView还提供一个 getHolder 方法，用于获取对应其Surface的SurfaceHolder对象

创建SurfaceView, 实现SurfaceHolder.Callback

package com.example.chenpiyang.snapshot;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }
}


2.1.3 实现Camera对象

既然建立了活动及预览Surface，现在开始使用实际的Camera对象
当创建Surface  由于SurfaceHolder.Callback 存在， 它将在代码中触发调用 surfaceCreated 方法， 此时可以通过调用Camera的静态方法 open 获取 camera对象


代码首先检查设备配置（通过 Context.getResources().getConfiguration() ）查看当前方向，如果方向不是横向模式，那么设置 Camera.Parameters的 “orientation” 为 portrait 。此外， 调用 Camera.Parameters 的setRotation方法，并传入 90度的参数， 该方法在 API 5和更高版本上可用，它实际上并不执行任何旋转，相反，他会告知Camera 对象在EXIF数据中制定该图像应该旋转90度显示， 如果没有包含该信息， 那么在其他应用程序中查看该图像时， 它可能会侧面显示
1.2.5 内部元数据exif.note

注意：以上所示的通过Camera.Parameters 修改Camera对象旋转的方法用于Android2.1以及更早版本， 在android2.2中引入了Camera类的一个新方法 setDisplayOrientation(int degrees) .   该方法接受一个整数，表示图像应该旋转的度数， 有效的度数是 0，90，180，270




大多数可以或应该修改的参数都有与他们相关联的特定方法
如同setRotation方法一样， 这些方法遵循Java的获取器和设置器设计模式
例如，
可以使用 setFlashMode(Camera.Parameters.FLASH_MODE_AUTO) 设置Camera的闪光定模式，
同时使用 getFlashMode() 获取它的当前值


修改颜色效果
android2.0开始， 存在一个可用于展示的有趣参数， 该参数可以修改颜色效果，
对应的获取器和设置器方法是 getColorEffect 和 setColorEffect
同时还存在一个 getSupportedColorEffects方法， 它返回一个String 对象列表，对应特定设备上所支持的各种效果

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
            if(currentEffect.equals(Camera.Parameters.EFFECT_SOLARIZE)){
                Log.v("snapshot","Using solarize");
                parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
                break;
            }
        }
        Log.v("snapshot","Using Effect: "+ parameters.getColorEffect());

       
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


07-02 01:46:39.707 11485-11485/com.example.chenpiyang.snapshot V/snapshot: Checking none
07-02 01:46:39.707 11485-11485/com.example.chenpiyang.snapshot V/snapshot: Checking mono
07-02 01:46:39.707 11485-11485/com.example.chenpiyang.snapshot V/snapshot: Checking negative
07-02 01:46:39.707 11485-11485/com.example.chenpiyang.snapshot V/snapshot: Checking sepia
07-02 01:46:39.707 11485-11485/com.example.chenpiyang.snapshot V/snapshot: Using Effect: none

其他可能的效果以常量的形式在Camera.Parameters里面列出
EFFECT_NONE
EFFECT_MONO
EFFECT_NEGATIVE
EFFECT_SOLARIZE
EFFECT_SEPIA
EFFECT_POSTERIZE
EFFECT_WHITEBOARD
EFFECT_BLACKBOARD
EFFECT_AQUA



2，更改摄像头预览大小

另一个在Camera.Parameters  中特别有用的参数是设置预览大小
首先检查参数对象并获取所支持的值，
public static final int Largest_width = 200
public static final int largest_height = 200

