 Intent intent = new Intent(Intent.ACTION_VIEW);
        File sdcard = Environment.getExternalStorageDirectory();
        System.out.println("===++++==== "+sdcard+" ===========================");
//System.out: ===++++==== /storage/emulated/0 ===========================
        //创建File对象， 引用内部存储的音频文件
        File audioFile = new File("/sdcard/youyuandeni.mp3");
        //File audioFile = new File(sdcard.getPath()+"/netease/cloudmusic/Music/徐薇\\ -\\ 云烟成雨\\ -\\ 徐薇（Cover\\ 房东的猫）.mp3");
        //将意图的数据设置为源自音频文件的Uri，类型设置为其MIME类型：audio/mp3，
        intent.setDataAndType(Uri.fromFile(audioFile),"audio/mp3");
        startActivity(intent);


/storage/emulated/0  即内部存储
HWSNE:/storage/emulated/0/netease/cloudmusic/Music $ pwd
/storage/emulated/0/netease/cloudmusic/Music
HWSNE:/storage/emulated/0/netease/cloudmusic/Music $ ls
卫仲乐\ -\ 醉渔唱晚\ 据《李子昭传谱》.mp3          成公亮\ -\ 潇湘水云.mp3     
吴文光\ -\ 酒狂.mp3                      管平湖\ -\ 乌夜啼.mp3      
周璇\ -\ 月圆花好.mp3                     管平湖\ -\ 碣石调幽兰.mp3    
尧十三\ -\ 北方的女王(demo).mp3             许嵩\ -\ 清明雨上.mp3      
尧十三\ -\ 南方的女王.mp3                   谢导秀\ -\ 碧涧流泉.mp3     
尧十三\ -\ 瞎子(贵州话).mp3                 郑云飞\ -\ 鸥鹭忘机.mp3     
山阳琴人\ 李家祥\ -\ 山水情.mp3               马杰\ -\ 山水情(金复载曲).mp3 
徐君跃\ -\ 欸乃.mp3                      马杰\ -\ 平沙落雁.mp3      
徐君跃\ -\ 醉渔唱晚.mp3                    马杰\ -\ 醉渔唱晚.mp3      
徐薇\ -\ 云烟成雨\ -\ 徐薇（Cover\ 房东的猫）.mp3 龚一\ -\ 欸乃.mp3        
成公亮\ -\ 普庵咒.mp3 