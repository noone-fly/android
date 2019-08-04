7.2 定制音频捕获
控制录音的时长

MediaRecorder 可以用于音频和视频的捕获
setAudioEncoder
setAudioSource 
setOutputFormat   录制所用的文件格式
setOutputFile     录制到目标文件



7.2.1 MediaRecorder 音频源

实例化MediaRecorder 之后， 调用第一个方法是 setAudioSource
该方法采用一个AudioSource 内部类中定义的常量作为参数， MediaRecorder.AudioSource.MIC
其他常量是 。 VOICE_CALL,  VOICE_DOWNLINK,  VOICE_UPLINK，但是android手机没有能从电话中录音的
android 2.2 之后， 还包含两个常量  CAMCORDER 和 VOICE_RECOGNITION
如果设备包含一个以上的麦克风，可以使用它们
MediaRecorder recorder = new MediaRecorder()
recorder.setAudioSource(MediaRecorder.AudioSource.MIC)



7.2.2 MediaRecorder 输出格式
根据顺序，下一个调用的方法是 setOutputFormat
MediaRecorder.OutputFormat.MPEG_4      这个常量指定输出的文件将是一个 MPEG-4文件，它可能同时包含音频和视频轨。
MediaRecorder.OutputFormat.RAW_AMR   输出没有任何容器类型的原始文件，只有在捕获没有视频的音频且音频编码器是AMR_NB
MediaRecorder.OutputFormat.THREE_GPP 输出的文件将是一个 3GPP文件，扩展名是 .3gp  可能同时包含音频轨视频轨

recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)



7.2.3 MediaEncorder 音频编码器

在设置输出格式之后， 可以调用 setAudioEncoder 方法来设置应该使用的编解码器，
可能的值指定为 MediaRecorder.AudioEncoder 类中的常量， 除了 DEFAULT 之外， 
只存在一个其他的值 MediaRecorder.AudioEncoder.AMR_NB  这是自适应多速率窄带编解码器
默认情况下，采样率是 8kHz，码率在 4.75 ～ 12.2 kbps   这两个值是当前可用于 MediaRecorder 的唯一选择

recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)


7.2.4 MediaRecorder 输出和录制

setOutputFile

File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.apress.proandroidmedia.ch07.customrecorder/files/")
path.mkdirs();
audioFile = File.createTempFile("recording", ".3gp", path);
recorder.setOutputFile(audioFile.getAbsolutePath());
调用prepare方法，其表明配置阶段的结束， 同时通知MediaRecorder准备开始录制
recorder.prepare();
recorder.start()
recorder.stop()


7.2.5  MediaRecorder 状态机

类似于 MediaPlayer
MediaRecorder 可作为一个状态机进行操作


7.2.7  其他MediaRecorder方法

MediaRecording 有各种用于音频捕获的其他方法
getMaxAmplitude 
允许请求由 MediaPlayer 录制的音频的最大振幅， 每次调用此方法时都会重置该值， 因此每次调用都将返回自从上一次调研以来的最大振幅，可以通过调用该方法实现音量表

setMaxDuration
允许以毫秒为单位指定最大录制持续时间， 必须在setOutputFormat方法之后和prepare方法之前调用该方法

setMaxFileSize
允许以字节为单位指定录制的最大文件大小，与setMaxDuration 一样， 必须在 OutputFormat方法之后和prepare之前调用