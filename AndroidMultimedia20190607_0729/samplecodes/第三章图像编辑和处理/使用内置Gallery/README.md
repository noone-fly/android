


本章介绍如何处理捕获后的图像
缩放，旋转图像
调整图像亮度，对比度
如何合成两幅和多幅图像



3.1 使用内置Gallery应用程序
为了使用android预装的应用， 使用Intent是最快捷方式
本章利用内置的Gallery(图像库)选择图像

将使用的意图
1， Intent.ACTION_PICK   它通知android， 我们想要选择一块数据
并制定 URI   使用MediaStore存储在SDcard上面的图像

Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

当触发这个意图， 它会以用户能够选择一副图像的模式启动Gallery
与通常从意图返回一样，在用户选择图像之后将触发 onActivityResult 方法， 返回的意图数据中， 将返回选择图像的URI

onActivityResult(int requestCode, int resultCode, Intent intent){
     supur.onActivityResult(requestCode, resultCode, intent);
     if(resultCode == RESULT_OK){
          Uri imageFileUri = intent.getData();
     }
}



<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.chenpiyang.previewsize">
    <uses-permission android:name="android.permission.RECORD_AUDIO"></uses-permission>
    <uses-permission android:name="android.permission.CAMERA"></uses-permission>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"></uses-permission>
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>



<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent" android:layout_height="fill_parent" android:orientation="vertical">

    <SurfaceView android:id="@+id/CameraView"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent" />
</LinearLayout>


