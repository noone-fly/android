 MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.youyuandeni); //创建音频
 mediaPlayer.setOnCompletionListener(this); //注册 onCompletion方法
 public void onCompletion(MediaPlayer mediaPlayer) //完成播放时调用



 