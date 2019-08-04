
可以将录制的音频放入MediaStore 内容提供器， 从而可以用于其他应用， 这非常类似于之前的将图像添加到MediaStore
创建一个 ContentValues 对象保存数据，同时将他们插入到 MediaStore，ContentValues 由一系列的键值对组成， 其中可以使用的键定义为 MediaStore.Audio.Media 类的常量
MediaStore.Audio.Media.DATA 常量表示录制文件的路径， 
为了插入到MediaStore  可以使用 ContentResolver 对象的 insert方法，同时以指向SDcard上的音频文件表的Uri，以及包含数据的 ContentValues 对象作为参数。 Uri 定义为 MediaStore.Audio.Media 上的常量 EXTERNAL_CONTENT_URL

在MediaRecorder  release之后执行插入到MediaStore 



//停止录制并释放mediarecording
mediaRecorder.stop();
mediaRecorder.release();

//把录制的音频存入MediaStore
ContentValues contentValues = new ContentValues();
contentValues.put(MediaStore.MediaColumns.TITLE, "this isnot music");
contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
contentValues.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());
Uri newUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);



else if (view == stopRecording){
    //完成录制时，设置isRecording 布尔值为false，同时调 RecordAmplitude的cancel方法
    isRecording = false;
    recordAmplitude.cancel(true);

    //停止录制并释放mediarecording
    mediaRecorder.stop();
    mediaRecorder.release();

    //把录制的音频存入MediaStore
    ContentValues contentValues = new ContentValues();
    contentValues.put(MediaStore.MediaColumns.TITLE, "this isnot music");
    contentValues.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis());
    contentValues.put(MediaStore.Audio.Media.DATA, audioFile.getAbsolutePath());
    Uri newUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);

    //然后构造一个MediaPlayer，使他准备好播放刚刚录制的音频
    mediaPlayer = new MediaPlayer();
    mediaPlayer.setOnCompletionListener(this);
    try {
        mediaPlayer.setDataSource(audioFile.getAbsolutePath());
        mediaPlayer.prepare();
    } catch (IOException e) {
        e.printStackTrace();
    }
    //设置statusTextView 提醒用户已准备好播放音频文件
    statusTextView.setText("Ready to play");
    playRecording.setEnabled(true);
    stopRecording.setEnabled(false);
    startRecording.setEnabled(true);
