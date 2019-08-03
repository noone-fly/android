

Android框架层包含了对多种相机和相机特性的支持，可以让你在你的应用中拍照或录像。本文档主要讨论如何快速、简单的进行拍照和录像，同时也对如何开发复杂一些的相机应用做了简要介绍。


1 基础
Android框架层支持通过android.hardware.camera2API 或 camera Intent 来拍照和录像，以下是相关的类：
android.hardware.camera2
这个包提供了控制相机设备的主要API。

Camera
已过时的控制相机设备的API。

SurfaceView
这个类用来向用户展示实时的相机预览（live camera preview）。

MediaRecorder
这个类用来录像。

Intent
不需要直接操作相机设备，通过 MediaStore.ACTION_IMAGE_CAPTURE 和 MediaStore.ACTION_VIDEO_CAPTURE 这两个Intent就可以快速地进行拍照和录像。


2 清单文件声明
在开始使用相机API进行开发之前，首先要确保你的清单文件中已经声明了相应的权限和特性。

相机权限 —— 如果你的app要直接使用相机设备，则必须声明此权限。
<uses-permission android:name="android.permission.CAMERA" />


注意：如果是通过Intent来间接使用相机，则不需要声明此权限。

相机特性（Camera Features）
<uses-feature android:name="android.hardware.camera" />


如果在清单文件中声明了相机特性，那么Google Play会阻止你的app被安装在不支持相机的设备上。

存储权限 —— 如果你的app将相片和视频存储在外部存储设备（SD卡），则必须声明此权限。
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />


录音权限 —— 如果需要在录像的同时录音，则必须声明录音权限。
<uses-permission android:name="android.permission.RECORD_AUDIO" />


获取位置信息权限 —— 如果想要给拍摄的照片加上位置信息，则必须声明此权限。
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />





3 通过Intent使用已有的相机app来拍照和录像

在你的app中进行拍照和录像的一个简单方法是：通过Intent来调起一个已有的相机应用。有如下几步：
创建Intent
MediaStore.ACTION_IMAGE_CAPTURE：通过已有的相机应用拍照。
MediaStore.ACTION_VIDEO_CAPTURE：通过已有的相机应用录像。
发送Intent：调用startActivityForResult()。
接收拍照或录像的结果：在onActivityResult()方法中接收拍照或录像的结果。


（1） 拍照intent

拍照Intent可以包含如下额外信息：
MediaStore.EXTRA_OUTPUT：设置照片保存的路径（包含文件名），值为一个Uri对象。此项设置是可选的，但是强烈建议进行设置。如果不设置，则照片会以默认的名字保存在默认路径，通过调用onActivityResult()方法中参数intent的getData()方法，可以获得照片的保存路径(uri)。
 imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myfavoriatepicture.jpg";
        File imageFile = new File(imageFilePath);
        Uri imageFileUri = Uri.fromFile(imageFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //imageFileUri = Uri.parse("file:///sdcard/myfavoriatepicture.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent, CAMERA_RESULT);

以下为示例代码，其中getOutputMediaFileUri(...)方法的实现见后面的存储媒体文件部分：

private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
private Uri fileUri;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // create Intent to take a picture and return control to the calling application
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

    // start the image capture Intent
    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
}


（2） 录像intent

录像Intent可以包含如下额外信息：
MediaStore.EXTRA_OUTPUT：同上，设置视频保存的路径（包含文件名），不再赘述。
MediaStore.EXTRA_VIDEO_QUALITY：设置视频的质量。取值从0到1，1代表最高的视频质量（和最大的文件尺寸）
MediaStore.EXTRA_DURATION_LIMIT：限制视频的时长，以秒为单位。
MediaStore.EXTRA_SIZE_LIMIT：限制视频文件的大小，以字节为单位。


以下为示例代码，其中getOutputMediaFileUri(...)方法的实现见后面的存储媒体文件部分：
private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
private Uri fileUri;

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    //create new Intent
    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

    fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video
    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high

    // start the Video Capture Intent
    startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
}


（3） 接收拍照或录像的结果

为了接收拍照或录像的结果，你需要覆写Acitivity的onActivityResult(...)方法，如下例所示：
private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            // Image captured and saved to fileUri specified in the Intent
            Toast.makeText(this, "Image saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture
        } else {
            // Image capture failed, advise user
        }
    }

    if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
        if (resultCode == RESULT_OK) {
            // Video captured and saved to fileUri specified in the Intent
            Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the video capture
        } else {
            // Video capture failed, advise user
        }
    }
}


