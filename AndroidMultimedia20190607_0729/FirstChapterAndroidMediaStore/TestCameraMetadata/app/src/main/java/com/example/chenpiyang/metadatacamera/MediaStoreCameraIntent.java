package com.example.chenpiyang.metadatacamera;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class MediaStoreCameraIntent extends AppCompatActivity {

    private static final int WRITE_PERMISSION = 0x01;  // 动态申请存储权限
    final static int CAMERA_RESULT = 0;
    Uri imageFileUri;
    ImageView returnedImageView;
    Button takePictureButton;
    Button saveDataButton;
    TextView titleTextView;
    TextView descriptionTextView;
    EditText titleEditText;
    EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_store_camera_intent);

        requestWritePermission();

        System.out.println("MediaStore.Images.Media.EXTERNAL_CONTENT_URI=====================" + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        //获取UI元素的引用
        returnedImageView = (ImageView) findViewById(R.id.ReturnedImageView);
        takePictureButton = (Button) findViewById(R.id.TakePictureButton);
        saveDataButton = (Button) findViewById(R.id.SaveDataButton);
        titleTextView = (TextView) findViewById(R.id.TitleTextView);
        descriptionTextView = (TextView) findViewById(R.id.DescriptionTextView);
        titleEditText = (EditText) findViewById(R.id.TitleEditView);
        descriptionEditText = (EditText) findViewById(R.id.DescriptionEditView);
        //除tackPictureButton, 将其他所有元素都设置为初始时不可见，且不占用布局空间  View.GONE
        //View.GONE 不可见且不占布局空间，View.INVISIBLE 隐藏元素，但占布局空间
        returnedImageView.setVisibility(View.GONE);
        saveDataButton.setVisibility(View.GONE);
        titleTextView.setVisibility(View.GONE);
        descriptionTextView.setVisibility(View.GONE);
        titleEditText.setVisibility(View.GONE);
        descriptionEditText.setVisibility(View.GONE);


        //点击拍照
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加一条不带位图的新记录
                //返回新记录的Uri
                //Uri insert(Uri url, ContentValues values) 将一组数据插入到Uri 指定的地方，返回新inserted的URI。
                //ContentValues 和HashTable类似都是一种存储的机制 但是两者最大的区别就在于，contenvalues只能存储基本类型的数据，像string，int之类的
                //I/System.out: MediaStore.Images.Media.EXTERNAL_CONTENT_URI==content://media/external/images/media
                //android里，当Uri以content开头，它将由内容提供器使用（如MediaStore）
                //insert方法返回新记录的Uri
                imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                //启动Camera应用程序
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //传递你要保存的图片的路径
                //MediaStore.EXTRA_OUTPUT 将拍摄的照片存储在SDcard
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
//如果想在Activity中得到新打开Activity 关闭后返回的数据，需要使用系统提供的startActivityForResult(Intent intent, int requestCode)方法打开新的Activity，新的Activity 关闭后会向前面的Activity传回数据，为了得到传回的数据，必须在前面的Activity中重写onActivityResult(int requestCode, int resultCode, Intent data)方法
                //CAMERA_RESULT
                startActivityForResult(intent, CAMERA_RESULT);
            }
        });

        saveDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //更新MediaStore中记录的标题和描述
                //预填充关联元数据，使用put方法，key-value，
                ContentValues contentValues = new ContentValues(3);
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, titleEditText.getText().toString());
                contentValues.put(MediaStore.Images.Media.DESCRIPTION, descriptionEditText.getText().toString());
                getContentResolver().update(imageFileUri, contentValues, null, null);
                //通知
                Toast bread = Toast.makeText(MediaStoreCameraIntent.this, "Record Updated", Toast.LENGTH_SHORT);
                bread.show();
                //回到初始状态，设置拍照按钮可见， 隐藏其他UI元素
                takePictureButton.setVisibility(View.VISIBLE);
                returnedImageView.setVisibility(View.GONE);
                saveDataButton.setVisibility(View.GONE);
                titleTextView.setVisibility(View.GONE);
                descriptionTextView.setVisibility(View.GONE);
                titleEditText.setVisibility(View.GONE);
                descriptionEditText.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //隐藏拍照按钮，不隐藏也没关系
            takePictureButton.setVisibility(View.VISIBLE);
            //显示其他UI元素
            saveDataButton.setVisibility(View.VISIBLE);
            returnedImageView.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setVisibility(View.VISIBLE);
            titleEditText.setVisibility(View.VISIBLE);
            descriptionEditText.setVisibility(View.VISIBLE);
            //缩放图像
            int dw = 400; // 最宽200像素
            int dh = 400; // 最高200像素
            try {
                //加载图像的尺寸，而非图像本身
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options);
                int heightRadio = (int) Math.ceil(options.outHeight / (float) dh);
                int widthRadio = (int) Math.ceil(options.outWidth / (float) dw);
                Log.v("height radio ", "" + heightRadio);
                Log.v("width radio", "" + widthRadio);

                //如果两个比率都大于1，那么图像的一条边将大于屏幕
                if (heightRadio > 1 && widthRadio > 1) {
                    if (heightRadio > widthRadio) {
                        //若高度比率更大，则根据高度缩放
                        options.inSampleSize = heightRadio;  //图片压缩比例.
                    } else {
                        options.inSampleSize = widthRadio;
                    }
                }

                //对它进行真正的解码
                options.inJustDecodeBounds = false;  //加载到内存中
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options);
                returnedImageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                Log.v("Error", e.toString());
            }
        }
    }

    // 动态申请存储权限
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

    // 动态申请存储权限
    private void requestWritePermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
        }
    }


}
