第九章，了解了android如何播放设备的sd卡上放置的特定视频文件，本章继续探索如何通过MediaStore访问视频，以及如何访问Internet上的视频


10.1 使用MediaStore检索视频

正如第一章讨论的， android为应用程序之间共享数据提供了一个标准方式， ContentProvider类是实现此功能的基础类，针对媒体扩展了 ContentProvider 概念的类是各种 MediaStore 类，
之前已经学习如何将 MediaStore用于图像和音频以及它们的元数据，用于视频的MediaStore大致相同

MediaStore.Video  专门用于视频文件
MediaStore.Video.Media  其中包含各种常量， 以指定在MediaStore中与视频媒体本身相关的可用列，
其他许多列都是继承自其他类，如 MediaStore.MediaColumns

MediaStore.Video.Thumbnails  的常量指定用于缩略图存储的MediaStore 中与视频文件相关的可用列

为了使用Activity中的 managedQuery 方法， 需要传入想要返回的列数组，
MediaStore.Video.Media._ID  
MediaStore.Video.Media.DATA  指向视频文件本身的路径

String[] mediaColums = {
MediaStore.Video.Media._ID,
MediaStore.Video.Media.DATA,   视频文件的路径
MediaStore.Video.Media.TITLE,   文件的标题
MediaStore.Video.Media.MIME_TYPE  文件的mime类型
};


从MediaStore.Video.Media.EXTERNAL_CONTENT_URL中查询所有数据源
Cursor cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URL, mediaColumns, null,null,null);
返回一个cursor 对象，用于遍历和提取数据
if(cursor.moveToFirst()){
  do{
     cursor.getString( cursor.getColumnIndex(MediaStore.Video.Media.DATA) );
  } while (cursor.moveToNext());
}


10.1.1 来自MediaStore的视频缩略图
android 2.0（API level 5）开始，可以在循环中获取与每个视频文件相关联的缩略图，
为此，需要视频文件的ID，位于 MediaStore.Video.Media._ID
同时在managedQuery 中使用

int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DATA) );
String[] thumbColums = {MediaStore.Video.Thumbnails.DATA,
                                           MediaStore.Video.Thumbnails.VIDEO_ID};
Cursor thumbCursor = managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URL, thumbColums , MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id , null, null);
if(thumbCursor.moveToFirst()){
 Log.v("VideoGallery", thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
}



10.1.2  完整的MediaStore 视频示例
从MediaStore检索所有视频，显示缩略图和标题，