package com.example.chenpiyang.customvideoplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

public class CustomVideoPlayer extends AppCompatActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnVideoSizeChangedListener,
        SurfaceHolder.Callback{

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 0x01;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION = WRITE_EXTERNAL_STORAGE_PERMISSION + 1;

    Display currentDisplay;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    MediaPlayer mediaPlayer;

    int videoWidth = 0;
    int videoHeight = 0;

    boolean readyToPlay = false;

    public final static String LOGTAG = "CUSTOM_VIDEO_PLAYER";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_video_player);
        checkSelfPermissions();
        //设置内容视图之后，可以获得在布局定义的SurfaceView的引用以及SurfaceHolder引用，从而能监控在底层表面上发生的事
        surfaceView = (SurfaceView)this.findViewById(R.id.SurfaceView);
        //通过SurfaceView获取 SurfaceHolder
        surfaceHolder = surfaceView.getHolder();

        //activity 实现了 SurfaceHolder.Callback，因此把它指定为回调监听器
        surfaceHolder.addCallback(this);
        //需要确保底层表面是一个推送缓冲区表面，需要将它用于视频播放和摄像头预览
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //构造一个MediaPlayer对象, 并指定各种事件监听器
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);

        //在完成onCreate方法之前， 将通知MediaPlayer对象所需的播放内容，
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/1554607766517793.mp4";
        try {
            //MediaPlayer的setDataSource方法可能抛出多个异常，这里是结束activity
            //也可以向用户提供一个机会来选择不同文件或者解释发生错误的具体情况
            mediaPlayer.setDataSource(filePath);
        } catch (IOException e) {
            Log.v(LOGTAG, e.getMessage());
            finish();
        }catch (IllegalStateException e) {
            Log.v(LOGTAG, e.getMessage());
            finish();
        }catch (IllegalArgumentException e) {
            Log.v(LOGTAG, e.getMessage());
            finish();
        }

        currentDisplay = getWindowManager().getDefaultDisplay();
    }

    //当创建Surfaceview中的底层表面时，将会调用surfaceCreated方法
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.v(LOGTAG, "surfaceCreated called");
        //当创建表面时，通过调用MediaPlayer的setDisplay方法并传入SurfaceHolder对象，可以指定MediaPlayer将该表面用于播放
        mediaPlayer.setDisplay(surfaceHolder);
        //最后，指定表面之后可以调用prepare方法，prepare方法会阻塞而不是在后台工作，因此为了在后台完成它所做的工作，从而不至于
        //绑定应用程序， 可以使用prepareAsync方法，不管采用哪种方法，由于已经实现了OnPreparedListener, 并把活动设置为监听器
        //因此当完成操作时将调用onPrepared方法
        //prepare可能抛出多个异常，这里是结束activity
        //也可以向用户提供一个机会来选择不同文件或者解释发生错误的具体情况
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.v(LOGTAG,e.getMessage());
            finish();
        }catch (IllegalStateException e) {
            Log.v(LOGTAG,e.getMessage());
            finish();
        }
    }

    //当SurfaceView的底层表面的宽度，高度或其他参数发送变化时，将调用surfaceChanged方法，
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.v(LOGTAG, "surfaceChanged called");
    }

    //当SurfaceView被销毁时
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.v(LOGTAG, "surfaceDestroyed called");
    }

    //由于实现了OnCompletionListener，并把活动自身注册为监听器，因此当MediaPlayer完成播放文件时，将调用onCompletion方法
    //可以使用这个方法来加载另外一个视频，或者执行其他诸如加载另外一个屏幕的动作，这里只退出活动
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.v(LOGTAG, "onCompletion called");
        finish();
    }

    //由于实现了OnErrorListener，并把活动自身注册为监听器，当发生错误时
    //错误类型只有两个常量
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int whatError, int extra) {
        Log.v(LOGTAG, "onError called");
        if (whatError == MediaPlayer.MEDIA_ERROR_SERVER_DIED){
            Log.v(LOGTAG, "Media Error, Server Died " + extra);
        }else if (whatError == MediaPlayer.MEDIA_ERROR_UNKNOWN){
            Log.v(LOGTAG, "Media Error, Error Unkown " + extra);
        }
        return false;
    }

    //当出现关于播放媒体的特定信息或需要发出警告时， 将调用此方法
    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int whatInfo, int extra) {
        if (whatInfo == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING){
            //当文件中音频和视频数据不正确地交错时触发，在正确交错的媒体文件中，音频和视频样本将依次排列，从而使播放能够有效和平稳地进行
            Log.v(LOGTAG, "Media Info, Media Info Bad Interleaving " + extra);
        }else if (whatInfo == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE){
            //当媒体不能正确地定位时，这意味着它可能时一个在线流
            Log.v(LOGTAG, "Media Info, Media Info not seekable " + extra);
        }else if (whatInfo == MediaPlayer.MEDIA_INFO_UNKNOWN){
            //信息未指定或未知错误
            Log.v(LOGTAG, "Media Info, Media Info Unknown  " + extra);
        }else if (whatInfo == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING){
            //设备无法播放视频时，可能时将要播放视频，但该视频太复杂或码率过高。
            Log.v(LOGTAG, "Media Info, Media Info video track lagging " + extra);
        }else if (whatInfo == MediaPlayer.MEDIA_INFO_METADATA_UPDATE){
            //android2.0以及更高版本可用，当新的元数据可用时触发
            Log.v(LOGTAG, "Media Info, Media Info metadata update " + extra);
        }
        return false;
    }

    //当MediaPlayer成功地准备开始播放之后， 触发onPrepared, 一旦触发此方法，MediaPlayer就进入准备就绪状态，准备开始播放
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.v(LOGTAG, "onPrepared called");

        //播放之前，应该设置SurfaceView的大小以匹配视频或显示器的大小，
        //首先使用MediaPlayer对象的getVideoWidth和getVideoHeight方法来获取视频的尺寸
        videoHeight = mediaPlayer.getVideoHeight();
        videoWidth = mediaPlayer.getVideoWidth();

        //如果视频的宽度或高度大于显示器大小，就需要找出应该使用的比率
        if (videoWidth > currentDisplay.getWidth() || videoHeight > currentDisplay.getHeight()){
            float heightRatio = (float)videoHeight/(float)currentDisplay.getHeight();
            float widthRatio = (float)videoWidth/(float)currentDisplay.getWidth();
            if (heightRatio > 1 || widthRatio > 1){
                //使用较大的比率，通过将视频大小除以较大的比率来设置videoHeight和videoWidth
                if (heightRatio > widthRatio){
                    videoHeight = (int)Math.ceil((float)videoHeight/(float)heightRatio);
                    videoWidth = (int)Math.ceil((float)videoWidth/(float)heightRatio);
                }else {
                    videoHeight = (int)Math.ceil((float)videoHeight/(float)widthRatio);
                    videoWidth = (int)Math.ceil((float)videoWidth/(float)widthRatio);
                }
            }
        }
        //设置用量显示视频的surfaceView的大小，可以是视频实际的尺寸，或者如果视频大于显示器，则是调整之后的尺寸
        surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoWidth,videoHeight));

        mediaPlayer.start();
    }

    //完成seek命令时调用该方法
    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        Log.v(LOGTAG, "onSeekComplete called");
    }

    //视频大小发生变化，以及指定数据源和读取视频的元数据之后至少调用一次
    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i1) {
        Log.v(LOGTAG, "onVideoSizeChanged called");
    }


    // 动态申请摄像头，麦克风，外部存储
    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_PERMISSION) &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_PERMISSION);
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode,  String permissions[],  int[] grantResults) {
        switch (requestCode) {

            case WRITE_EXTERNAL_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You have the permission storage to your mobile device.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
                break;
            }
            case READ_EXTERNAL_STORAGE_PERMISSION: {
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