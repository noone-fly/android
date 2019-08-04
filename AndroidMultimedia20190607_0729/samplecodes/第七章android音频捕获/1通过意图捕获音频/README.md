三种捕获音频的方式
1，使用意图， 最简单但不灵活
2，使用MediaRecorder  难于使用，但提供了更多灵活性
3，使用AudioRecord， 



7.1  使用意图捕获音频
MediaStore.Audio.Media 类中的常量 RECORD_SOUND_ACTION  触发内置录音机

Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
startActivity(intent)

为了获取用户创建的录音， 我们使用 startActivityForResult 方法

Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
startActivityForResult(intent, RECORD_REQUEST);


