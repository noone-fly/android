


第三种方法录制音频， AudioRecord 是最灵活的方法，因为它允许访问原始音频流， 但它拥有较少的内置功能， 例如，不会自动压缩音频。

第一个需要指定的参数是音频源，在MediaRecorder 指定
int audioSource = MediaRecorder.AudioSource.MIC;

第二个需要指的是录制的采样率， 单位 赫兹 Hz
我们知道，MediaRecorder里面的采样率是 8kHz，而CD质量的音频通常是44.1 kHz 或 44100 Hz (赫兹是每秒的样本数量)

int sampleRateInHz = 11025;

第三， 指定捕获的音频通道数量， 
AudioFormat.CHANNEL_CONFIGURATION_MONO
AudioFormat.CHANNEL_CONFIGURATION_STEREO
AudioFormat.CHANNEL_CONFIGURATION_INVALID
AudioFormat.CHANNEL_CONFIGURATION_DEFAULT

int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO


第四，指定音频格式
AudioFormat.ENCODING_DEFAULT
AudioFormat.ENCODING_INVALID
AudioFormat.ENCODING_PCM_16BIT
AudioFormat.ENCODING_PCM_8BIT

PCM 代表脉冲编码调制（Pulse code modulation），它实际上是原始的音频样本，
因此可以设置每个样本的分辨率为16位或8位， 16位将占用更多空间和处理能力， 但表示的音频将更接近真实

int audioFormat = AudioFormat.ENCODING_PCM_16BIT


第五， 指定缓冲区大小
查询AudioRecord 类以获取最小缓冲区大小

int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);


第六，构造实际的AudioRecord 对象

AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);


第七，把音频存在文件
AudioRecord 实际不保存捕获的音频， 因此需要手动保存捕获的音频

创建文件
File recordingFile;
File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.chenpiyang/files");
path.mkdirs();

recordingFile = File.createTempFile("recording", ".pcm", path);


创建对应该文件的OutputStream  
出于性能和便利考虑，可以将它包装在 BufferedOutputStream 和 DataOutputStream 中

DataOutputStream dataOS = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(recordingFile)));


第八， 现在可以开始捕获音频，并写入文件

使用short数组来保存从AudioRecord对象读取的音频，同时采用比AudioRecord对象的缓冲区更小的数组向AudioRecord缓冲区填数据，这样可以确保在缓冲区音频读出之前，缓冲区不会溢出。

数组的大小推荐为缓冲区的四分之一。因为缓冲区是以字节为单位，而每个 short类型的数据占用2个字节， 所以除以2，正好和缓冲期大小相同， 除以4，则变为缓冲期的一半大小。

short[]   buffer = new short[bufferSize/4];

开始录制
audioRecord.startRecording();


录制开始之后可以构造一个循环， 不断从AudioRecord对象读取音频并放入short数组， 同时写入对应文件的 DataOutputStream
while(true){
   int bufferReadResult = audioRecord.read(buffer, 0, bufferSize/4);
   for(int i = 0; i < bufferReadResult; i++){
     dataOS.writeShort(buffer[i]);
   }
}
audioRecord.stop();
dataOS.close();

我们可能还想在某个线程中运行它， 从而，它不会绑定用户界面以及我们希望应用在录制时做其他事情
























