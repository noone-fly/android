package com.example.chenpiyang.querymediastore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int WRITE_PERMISSION = 0x01;  // 动态申请存储权限
    //没有使用屏幕大小来加载和显示图像，而是使用常量来决定如何显示
    public final static int DISPLAYWIDTH = 200;
    public final static int DISPLAYHIGHT = 200;
    //ImageButton代替ImageView，可以同时具备两个功能，可单击和可显示图片
    TextView titleTextView;
    ImageButton imageButton;
    Cursor cursor;
    Bitmap bitmap;
    String imageFilePath;
    int fileColumn;
    int titleColumn;
    int displayColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWritePermission();

        titleTextView = (TextView)this.findViewById(R.id.TitleTextView);
        imageButton = (ImageButton)this.findViewById(R.id.ImageButton);
        //指定想要返回的列的字符串数组，并传给managedQuery方法
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DISPLAY_NAME};
        //返回所有图片
        cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
        //
        fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        displayColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        //cursor.moveToFirst() 确定游标有内容
        if(cursor.moveToFirst()){
            titleTextView.setText(cursor.getString(titleColumn));
            imageFilePath = cursor.getString(fileColumn);
            bitmap = getBitmap(imageFilePath);
            //显示图像
            imageButton.setImageBitmap(bitmap);
        }
        //然后为imageButton 指定一个新的onClickListener, 其调用Cursor对象上的moveToNext方法，将遍历整个结果集，获取并显示返回的每幅图片
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cursor.moveToNext()){
                    titleTextView.setText(cursor.getString(titleColumn));
                    imageFilePath = cursor.getString(fileColumn);
                    bitmap = getBitmap(imageFilePath);
                    //显示图像
                    imageButton.setImageBitmap(bitmap);
                }
            }
        });
    }

    //该方法封装了图像的缩放和加载功能，为了在显示图像时避免产生前面提到的内存问题
    private Bitmap getBitmap(String imageFilePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, options);
        int heightRadio = (int)Math.ceil(options.outHeight/(float)DISPLAYHIGHT);
        int widthRadio = (int)Math.ceil(options.outWidth/(float)DISPLAYWIDTH);
        Log.v("height radio", " ---- "+heightRadio);
        Log.v("width radio", " ---- "+widthRadio);

        //如果两个比率都大于1，那么图像的一条边将大于屏幕
        if (heightRadio > 1 && widthRadio > 1) {
            if (heightRadio > widthRadio) {
                //若高度比率更大，则根据高度缩放
                options.inSampleSize = heightRadio;  //图片压缩比例.
            } else {
                options.inSampleSize = widthRadio;
            }
        }

        //对图像进行真正的解码
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(imageFilePath, options);
        return bitmap;
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
