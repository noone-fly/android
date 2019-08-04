
MediaStore 是android系统提供的一个多媒体数据库，android中多媒体信息都可以从这里提取，包括音频，视频，图像。

所有数据库不用自己进行创建， 直接调用 ContentResolver 来调用封装好的接口，进行数据库操作。

首先获取 ContentResolver 实例，通过 Activity或Service的Context来获取
-
Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options);

ContentResolver resolver = context.getContentResolver();

Activity.this 就是 Context
ContentResolver 实例获取之后，就可以进行各种查询，下面以音频数据库为例讲解增删改查， 视频和图像都类似

查询

Cursor cursor = resolver.query(Uri, columns,  selections, selectArgs, order)
1，Uri 代表要查询的数据库名称和表的名称， 
      MediaStore.Audio.Media.EXTERNAL_CONTENT_URI  取所有音乐  
      MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI  取专辑信息
2，columns  代表要从表中查询的列，用String数组表示
3，selections  相当于sql中的where条件子句，
4，selectArgs  这个参数是说你的selections里有？问号时，这里可以用实际值代替问号，如果selections没有问号，则这个String数组可以为null

代码：获取本机音乐并播放Audio browser.note


Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);


插入

ContentValues values = new ContentValues();
values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, 0);
resolver.insert(Uri, values);

ContentValues 对应数据库的一行数据，用put设置好， 直接insert


删除
resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, selectionArgs);


更新
ContentResolver resolver = ctx.getContentResolver();
Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
ContentValues values = new ContentValues();
values.put(MediaStore.Audio.Media.DATE_MODIFIED, sid);
resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,values, where, selectionArgs);
上面update方法和查询还有增加里的参数都很类似



***************************************************************************************************



1. Android的多媒体如何存储的？

Android的多媒体文件主要存储在 /data/data/com.android.providers.media/databases 目录下，该目录下有两个db文件，一个是内部存储数据库文件(internal.db)，一个是存储卡数据库(external-XXXX.db)。媒体文件的操作主要是围绕着这两个数据库来进行。这两个数据库的结构是完全一模一样的。

internal.db存放的是系统分区的文件信息，开发者是没法通过接口获得其中的信息的，而external.db存放的则是我们用户能看到的存储区的文件信息，即包含了手机内置存储，还包含了SD卡。
如果你的手机root了，可以使用SQLite Expert 这个软件查看其中的内容

这两个数据库包含了哪些表。
album_art， audio， search，album_info， audio_genres， searchhelpertitle，albums， audio_genres_map， thumbnails，android_metadata， audio_meta， video，artist_info， audio_playlists， videothumbnails，artists， audio_playlists_map，artists_albums_map， images，

先从基本的分析：
Images表：主要存储images信息。可以看一下这个表的结构：
CREATE TABLE images (
     _id                       INTEGER  PRIMARY KEY, 
     _data                  TEXT,
     _size                   INTEGER,
     _display_name   TEXT,
     mime_type         TEXT,
     title                     TEXT,
     date_added        INTEGER,
     date_modified    INTEGER,
     description        TEXT,
     picasa_id            TEXT,
     isprivate             INTEGER,
     latitude               DOUBLE,
     longitude            DOUBLE,
     datetaken           INTEGER,
     orientation          INTEGER,
     mini_thumb_magic INTEGER,
     bucket_id           TEXT,
     bucket_display_name TEXT
);

包含了一些基本信息，其中大家一看就明白了。


Thumbnails表：这个表和images表是有直接关系的。主要存储图片的缩略图，Android为每一张保存进系统的图片文件都会自动生成一张缩略图文件。关于这一点还有一些特殊的技巧后面再讲。我们可以看一下这个表的结构：
CREATE TABLE thumbnails (
     _id            INTEGER PRIMARY KEY,
     _data       TEXT,
     image_id  INTEGER,
     kind          INTEGER,
     width        INTEGER,
     height       INTEGER
);
每一张image对应一条thumbnail记录。


Video表：主要存储视频信息了。和images表类似。表结构如下：
CREATE TABLE video (
_id                     INTEGER PRIMARY KEY,
_data                TEXT NOT NULL,
_display_name    TEXT,
_size                  INTEGER,
mime_type        TEXT,
date_added       INTEGER,
date_modified   INTEGER,
title                    TEXT,
duration             INTEGER,
artist                  TEXT,
album                TEXT,
resolution          TEXT,
description        TEXT,
isprivate             INTEGER,
tags                   TEXT,
category           TEXT,
language           TEXT,
mini_thumb_data TEXT,
latitude DOUBLE,
longitude DOUBLE,
datetaken INTEGER,
mini_thumb_magic INTEGER, 
bucket_id TEXT, 
bucket_display_name TEXT,
bookmark INTEGER
);

Videothumbnails表：存储视频的缩略图信息。这个和thumbnails表类似。

Audio表：音频信息比视频信息和图片信息要稍微复杂一些，主要是存储了一些专辑(album)、歌手(artists)信息，而专辑和歌手信息是单独的表格存储的，audio其实是一个视图，真正的音频数据信息存储在audio_meta表格中。我们可以看一下audio视图的定义：
CREATE VIEW audio as SELECT * FROM audio_meta LEFT OUTER JOIN artists ON audio_meta.artist_id=artists.artist_id LEFT OUTER JOIN albums ON audio_meta.album_id=albums.album_id;

Albums表：主要存储专辑信息。

Artists表：主要存储歌手信息。不多赘述。

其他的一些表格我们平时可能用的比较少，就不做描述了，有兴趣可以自行研究一下。




1. 2. Android的多媒体如何获取？
Android提供了媒体获取与存储的相关API，主要包含在android.provider.MediaStorepackage中。

***************************************************************************************************

简单的观察一下，发现这些类也就是对数据库中的一些表的封装，弄懂了底层的存储结构，对于了解这些类的作用就很容易了。

Android系统中的每一种媒体文件有两种地址描述方式。

第一种模式，大家知道，在Android中，ContentProvider是用来存储和获取公共数据的统一接口，ContentProvider为每一类资源分配了URI地址，比如图片的地址就包括 MediaStore.Images.Media.INTERNAL_CONTENT_URI和MediaStore.Images.Media.EXTERNAL_CONTENT_URI 两个基础地址，其值分别是content://media/internal/images/media和content://media/external/images/media，对应内部库和外部库地址。每一张图片的地址基本上是上面的基础URL地址下加上图片的内部ID。打个比方一张存储卡上的图片ID为2，其对应的Uri地址就是content://media/external/images/media/2。知道了这个地址，基本上就可以操作这张图片的所有信息了。

另外一种描述文件地址标识就是传统的文件路径模式了，比如一张存储卡上的图片地址可能描述为：/mnt/sdcard/images/1.jpg。其实这个路径存储在images表格中的data字段中，有了这点关联，我们可以在这两种模式下进行任意切换。

前一种模式下，主要通过MediaStore.Images.Media、MediaStore.Audio.Media、MediaStore.Video.Media 三个库中的query方法来查询或者获取特定条件的媒体了。

基本用法1：从一个Content Uri地址中生成Bitmap

可以采用android.provider.MediaStore.Images.Media.getBitmap(ContentResolver cr, Uri url)方法，其中ContentResolver是应用与资源之间的衔接人，它的示例通常可以通过在Activity中调用的getContentResolver()方法中获取。Uri地址就是上面描述的content://media/external/images/media/2类似地址，也就是Content Provider定义的地址形式。




基本用法2：从一个传统地址中生成Bitmap

有时候我们只知道一张图片的路径，并不知道图片的内部地址，想去获取该图片，可以采用android.graphics.BitmapFactory中的decodeXXX方法来搞定，比如decodeFile方法就是从文件路径中读取图片，原图片可以支持jpg,png,gif,bmp等各种格式。decodeByteArray就是从字节流中解码了。最后都是转换成Bitmap格式。

3.2 在位图上绘制位图.note
Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options);
                



基本用法3：获取一张图片的缩略图

有时候我们需要显示图片的缩略图，可以采用android.provider.MediaStore.Images.Thumbnails的getThumbnail方法。另外其实也可以采用bitmap的compress的方法对图片进行一些压缩处理。




