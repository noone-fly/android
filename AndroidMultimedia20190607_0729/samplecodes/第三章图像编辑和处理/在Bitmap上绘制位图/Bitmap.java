package com.example.chenpiyang.choosepicture;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 0x01;
    private static final int CAMERA_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION + 1;
    private static final int RECORD_AUDIO_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION +2;
    ImageView chosenImageView;
    ImageView alteredImageView;
    Button choosePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chosenImageView = (ImageView)this.findViewById(R.id.ChosenImageView);
        alteredImageView = (ImageView)this.findViewById(R.id.AlteredImageView);
        choosePicture = (Button)this.findViewById(R.id.ChoosePictureButton);
        choosePicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Uri imageFileUri = data.getData();
            Display display = getWindowManager().getDefaultDisplay();
            int dw = display.getWidth();
            int dh = display.getHeight()/2 - 100;
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

                Bitmap alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),bitmap.getConfig());
                Canvas canvas = new Canvas(alteredBitmap);
                Paint paint = new Paint();
                canvas.drawBitmap(bitmap, 0, 0, paint);


                chosenImageView.setImageBitmap(bitmap);
                alteredImageView.setImageBitmap(alteredBitmap);
            } catch (FileNotFoundException e) {
                Log.v("error", e.toString());
            }
        }
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

    public void onRequestPermissionsResult(int requestCode,   String permissions[], int[] grantResults) {
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