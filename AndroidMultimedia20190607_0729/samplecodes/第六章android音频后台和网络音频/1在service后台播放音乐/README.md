
activity里面
Intent playbackServiceIntent = new Intent(this, BackgroundAudioService.class);
 startService(playbackServiceIntent);

service里面  onCreate 拿到音频文件， onStartCommand 播放音频文件


@Override
public void onCreate() {
    super.onCreate();
    Log.v("player service","onCreate");
    //youyuandeni.mp3放在 res/raw下面
    Uri fileUri = Uri.parse("android.resource://com.example.chenpiyang.backgroundaudio/"+R.raw.youyuandeni);
    mediaPlayer = MediaPlayer.create(this, fileUri);

    //mediaPlayer = MediaPlayer.create(this, R.raw.youyuandeni);
    //将当前服务（即this类）设置为MediaPalyer对象的OnCompletionListener
    mediaPlayer.setOnCompletionListener(this);
}


// 当在该服务上面发出startService命令时，将触发 onStartCommand 方法，
    // 由于可能多次调用该方法，因此这个方法首先将检查MediaPlayer对象是否已经在播放，若没有播放则启动它
    // 由于使用了 onStartCommand方法而非onStart方法，因此该示例仅能在android2.0及以上版本运行
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("player service","onStartCommand");
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
            Log.v("onStartCommand","mediaplayer start");
        }
        return START_STICKY;
    }


// 当销毁服务时，触发onDestroy方法，由于这并不能确保MediaPlayer会停止播放，因此如果它还在播放，则在此调mediaplayer的stop方法
    // 同时还调用其release方法清除内粗使用和资源锁
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        Log.v("player service","onDestroy");
    }


// 由于实现了OnCompletionListener，且服务本身被设置为MediaPlayer对象的OnCompletionListener
    // 因此当MediaPlayer完成播放音频时，将调用onCompletion方法，由于此服务只播放一首歌，因此调用stopSelf方法，
    // 类似于在活动中调用stopService
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopSelf();
    }

