package com.example.chenpiyang.testcamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    final static int CAMERA_RESULT = 0;
    private static final int WRITE_PERMISSION = 0x01;
    ImageView imageView;
    String imageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWritePermission();
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }


        imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myfavoriatepicture.jpg";
        File imageFile = new File(imageFilePath);
        Uri imageFileUri = Uri.fromFile(imageFile);
        Intent intent = new Intent(androi.);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //imageFileUri = Uri.parse("file:///sdcard/myfavoriatepicture.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent, CAMERA_RESULT);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
//        if(resultCode == RESULT_OK){
//            Bundle extras = intent.getExtras();
//            Bitmap bitmap = (Bitmap) extras.get("data");
//            imageView =  (ImageView)findViewById(R.id.ReturnedImageView);
//            imageView.setImageBitmap(bitmap);
//        }

        if (resultCode == RESULT_OK) {
            imageView = (ImageView) findViewById(R.id.ReturnedImageView);
            Display currentDisplay = getWindowManager().getDefaultDisplay();
            int displayWidth = currentDisplay.getWidth();
            int displayHeigth = currentDisplay.getHeight();
            //System.out: 屏幕高度：2340屏幕宽度:1080
            System.out.println("屏幕高度：" + displayHeigth + "屏幕宽度:" + displayWidth);

            //加载图像的尺寸而不是图像本身
            //bitmapFactoryOptions.outHeight  原图的高
            //bitmapFactoryOptions.outWidth 原图的宽
            BitmapFactory.Options options = new BitmapFactory.Options();
            //只读取图片，不加载到内存中
            options.inJustDecodeBounds = true;
            // 通过这个bitmap获取图片的宽和高&nbsp
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, options);
            if (bitmap == null) {
                System.out.println("bitmap为空");
            }
            float realWidth = options.outWidth;
            float realHeight = options.outHeight;
            //System.out: 真实图片高度：5632.0真实图片宽度:4224.0
            System.out.println("真实图片高度：" + realHeight + "真实图片宽度:" + realWidth);

            int heightRatio = (int) Math.ceil(realHeight / (float) displayHeigth);
            int widthRatio = (int) Math.ceil(realWidth / (float) displayWidth);
            Log.v("height radio ", "" + heightRatio); // V/height radio:: 3
            Log.v("width radio", "" + widthRatio);    // V/width radio: 4

            //如果两个比率都大于1，那么图像的一条边将大于屏幕
            if (heightRatio > 1 && widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    //若高度比率更大，则根据高度缩放
                    options.inSampleSize = heightRatio;  //图片压缩比例.
                } else {
                    options.inSampleSize = widthRatio;
                }
            }
            System.out.println("缩放比：" + options.inSampleSize);
            //options.inSampleSize=5; //如果强行指定缩放比，显示的图片确实不好，但不知道为什么
            //对它进行真正的解码
            options.inJustDecodeBounds = false;  //加载到内存中
            bitmap = BitmapFactory.decodeFile(imageFilePath, options);
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            //System.out: 缩略图高度：1877宽度:1408
            System.out.println("缩略图高度：" + h + "宽度:" + w);
            //display image
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v("permission", "Write Permission Failed");
                Toast.makeText(this, "You have the permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "You must allow permission write external storage to your mobile device.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void requestWritePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
        }
    }
}
