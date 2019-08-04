6.1.3 控制服务中的MediaPlayer

当使用一个服务时，从面向用户的活动向MediaPlayer发出命令变得更为复杂
为了能够控制MediaPlayer，需要把该活动与服务绑定在一起。这样做之后，由于活动和服务在相同进程中运行，所以可以直接调用该服务中的方法

   @Override
    public void onClick(View view) {
        if (view == startPlaybackButton){
            Log.v("background activity","start service");
            startService(playbackServiceIntent);
            bindService(playbackServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }else if(view == stopPlaybackButton){
            Log.v("background activity","start service");
            unbindService(serviceConnection);
            stopService(playbackServiceIntent);
        }else if (view == haveFunButton){
            baService.haveFun();
        }
    }

    //ServiceConnection 用于监控所绑定服务的状态
    //bindService(playbackServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    //通过 bindService 与服务建立连接，并调用 onServiceconnected
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //返回绑定的服务
            baService = ((BackgroundAudioBindService.BackgroundAudioServiceBinder)iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            baService = null;
        }
    };


  //创建一个内部类，并扩展Binder，请求时返回服务本身
    public class BackgroundAudioServiceBinder extends Binder{
        BackgroundAudioBindService getService(){
            return BackgroundAudioBindService.this;
        }
    }
    //将内部类实例化为一个对象，basbinder
    private final IBinder basBinder = new BackgroundAudioServiceBinder();

    //重写onBind 返回该对象
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return basBinder;
    }

    //以上是实现绑定所需的工作，
    //2500毫秒
    public void haveFun(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 2500);
        }
    }

