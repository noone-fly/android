9.1.1 支持的格式

android支持的格式： 3GP（.3gp）和 MPEG-4 （.mp4）

编解码器： H.263   H.264  android以MPEG-4文件格式支持H.264编码的视频

android 3.0 及以上版本可能还支持 WebM   这是一种开放的，免版税的媒体容器，其中包含了VP8编码的视频和Vorbis编码的音频


9.1.2 使用意图播放

代码：用意图播放本地视频.note

Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/1554607766517793.mp4");
        intent.setDataAndType(data, "video/mp4");
        startActivity(intent);



9.1.3 使用VideoView 播放


代码：使用VideoView 播放本地视频.note
VideoView  videoView = (VideoView)this.findViewById(R.id.VideoView);
        File extStore = Environment.getExternalStorageDirectory();
        String mPath = extStore.getAbsolutePath() + "/1554607766517793.mp4";
        Uri data = Uri.parse(mPath);
        // Uri data = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/1554607766517793.mp4");
        videoView.setVideoURI(data);
        videoView.start();



9.1.4 使用MediaController 添加控制

VideoView控制视频播放的功能相对少， 只有start和pause方法， 
为了提供更多的控制， 可以实例化一个 MediaController，并通过 setMediaController方法设置为VidoeView的控制器
默认的 MediController 有后退， 暂停，播放，快进，还有清除，进度组合空间，


videoView = (VideoView)this.findViewById(R.id.VideoView);
videoView.setMediaController(new MediaController(this));
File extStore = Environment.getExternalStorageDirectory();
String mPath = extStore.getAbsolutePath() + "/1554607766517793.mp4";
Uri data = Uri.parse(mPath);
// Uri data = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/1554607766517793.mp4");
videoView.setVideoURI(data);
videoView.start();
