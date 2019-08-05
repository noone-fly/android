9.1.3 使用VideoView 播放


代码：使用VideoView 播放本地视频.note
VideoView  videoView = (VideoView)this.findViewById(R.id.VideoView);
        File extStore = Environment.getExternalStorageDirectory();
        String mPath = extStore.getAbsolutePath() + "/1554607766517793.mp4";
        Uri data = Uri.parse(mPath);
        // Uri data = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/1554607766517793.mp4");
        videoView.setVideoURI(data);
        videoView.start();


代码：使用VideoView播放并用MediaController控制视频
videoView = (VideoView)this.findViewById(R.id.VideoView);
videoView.setMediaController(new MediaController(this));
File extStore = Environment.getExternalStorageDirectory();
String mPath = extStore.getAbsolutePath() + "/1554607766517793.mp4";
Uri data = Uri.parse(mPath);
// Uri data = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/1554607766517793.mp4");
videoView.setVideoURI(data);

videoView.start(); 