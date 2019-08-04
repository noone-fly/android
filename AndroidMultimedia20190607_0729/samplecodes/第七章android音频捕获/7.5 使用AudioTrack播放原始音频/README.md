

AudioTrack 允许播放原始音频样本，利用该类能播放使用AudioRecord捕获的音频，  这些音频是无法用 MediaPlayer 来播放的

构造一个AudioTrack对象
第一个参数是流类型
可能的值是 AudioManager 的常量，
AudioManager.STREAM_MUSIC

第二个参数是采样率
根据采集时的采样率

第三个参数是通道
AudioFormat.CHANNEL_CONFIGURATION_MONO
AudioFormat.CHANNEL_CONFIGURATION_STEREO
AudioFormat.CHANNEL_CONFIGURATION_INVALID
AudioFormat.CHANNEL_CONFIGURATION_DEFAULT

第四个参数是音频格式
AudioFormat.ENCODING_DEFAULT
AudioFormat.ENCODING_INVALID
AudioFormat.ENCODING_PCM_16BIT
AudioFormat.ENCODING_PCM_8BIT

第五个参数是在对象中用于存储音频的缓冲期大小

int frequency = 11025;
int channelConfiguration  = AudioFormat.CHANNEL_CONFIGURATION_MONO
int audioFormat = AudioFormat.ENCODING_PCM_16BIT
int bufferSize = AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioFormat);


第六个参数是模式
AudioTrack.MODE_STATIC       在播放动作发生之前，将所有音频数据转移到 AudioTrack
AudioTrack.MODE_STREAM    在播放动作发生的同时，将音频数据持续地转移到 AudioTrack


构造完整的AudioTrack对象

AudioTrack audioTrack = new AudioTrack(AudioManger.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, bufferSize, AudioTrack.MODE_STREAM)



构造了AudioTrack对象之后， 需要准备原始pcm数据文件

使用 DataInputStream  从文件中读取

DataInputStream dataIS = new DataInputStream(new BufferedInputStream(new FileInputStream(recordingFile)));

调用play方法，并开始从 DataInputStream 写入音频
audioTrack.play();

while(isPlaying && dataIS.available() > 0){
   int i = 0;
   while (dataIS.available > 0 && audiodata.length){
      audiodata[i] = dataIS.readShort();
      i++;
   }
   audioTrack.write(audiodata, 0, audiodata.length);
}
dataIS.close();













