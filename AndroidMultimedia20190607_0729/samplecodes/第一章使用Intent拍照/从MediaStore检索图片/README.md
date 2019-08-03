


https://gy890725.iteye.com/blog/782467
MediaScannerService(多媒体扫描服务)
MediaStore（多媒体存储）
MediaProvider（多媒体内容提供者）





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




1. Android的多媒体如何存储的？

Android的多媒体文件主要存储在 /data/data/com.android.providers.media/databases 目录下，该目录下有两个db文件，一个是内部存储数据库文件(internal.db)，一个是存储卡数据库(external-XXXX.db)。媒体文件的操作主要是围绕着这两个数据库来进行。这两个数据库的结构是完全一模一样的。

internal.db存放的是系统分区的文件信息，开发者是没法通过接口获得其中的信息的，而external.db存放的则是我们用户能看到的存储区的文件信息，即包含了手机内置存储，还包含了SD卡。
如果你的手机root了，可以使用SQLite Expert 这个软件查看其中的内容

这两个数据库包含了哪些表。
album_art， audio， search，album_info， audio_genres， searchhelpertitle，albums， audio_genres_map， thumbnails，android_metadata， audio_meta， video，artist_info， audio_playlists， videothumbnails，artists， audio_playlists_map，artists_albums_map， images，

先从基本的分析：
Images表：主要存储images信息。可以看一下这个表的结构：
CREATE TABLE images (
     _id                     INTEGER  PRIMARY KEY, 
     _data                   TEXT,
     _size                   INTEGER,
     _display_name           TEXT,
     mime_type               TEXT,
     title                   TEXT,
     date_added              INTEGER,
     date_modified           INTEGER,
     description             TEXT,
     picasa_id               TEXT,
     isprivate               INTEGER,
     latitude                DOUBLE,
     longitude               DOUBLE,
     datetaken               INTEGER,
     orientation             INTEGER,
     mini_thumb_magic        INTEGER,
     bucket_id               TEXT,
     bucket_display_name     TEXT
);

包含了一些基本信息，其中大家一看就明白了。


Thumbnails表：这个表和images表是有直接关系的。主要存储图片的缩略图，Android为每一张保存进系统的图片文件都会自动生成一张缩略图文件。关于这一点还有一些特殊的技巧后面再讲。我们可以看一下这个表的结构：
CREATE TABLE thumbnails (
     _id            INTEGER PRIMARY KEY,
     _data          TEXT,
     image_id       INTEGER,
     kind           INTEGER,
     width          INTEGER,
     height         INTEGER
);
每一张image对应一条thumbnail记录。


Video表：主要存储视频信息了。和images表类似。表结构如下：
CREATE TABLE video (
_id                  INTEGER PRIMARY KEY,
_data                TEXT NOT NULL,
_display_name        TEXT,
_size                INTEGER,
mime_type            TEXT,
date_added           INTEGER,
date_modified        INTEGER,
title                TEXT,
duration             INTEGER,
artist               TEXT,
album                TEXT,
resolution           TEXT,
description          TEXT,
isprivate            INTEGER,
tags                 TEXT,
category             TEXT,
language             TEXT,
mini_thumb_data      TEXT,
latitude             DOUBLE,
longitude            DOUBLE,
datetaken            INTEGER,
mini_thumb_magic     INTEGER, 
bucket_id            TEXT, 
bucket_display_name  TEXT,
bookmark             INTEGER
);

Videothumbnails表：存储视频的缩略图信息。这个和thumbnails表类似。

Audio表：音频信息比视频信息和图片信息要稍微复杂一些，主要是存储了一些专辑(album)、歌手(artists)信息，而专辑和歌手信息是单独的表格存储的，audio其实是一个视图，真正的音频数据信息存储在audio_meta表格中。我们可以看一下audio视图的定义：
CREATE VIEW audio as SELECT * FROM audio_meta LEFT OUTER JOIN artists ON audio_meta.artist_id=artists.artist_id LEFT OUTER JOIN albums ON audio_meta.album_id=albums.album_id;

Albums表：主要存储专辑信息。

Artists表：主要存储歌手信息。不多赘述。

其他的一些表格我们平时可能用的比较少，就不做描述了，有兴趣可以自行研究一下。




1. 2. Android的多媒体如何获取？
Android提供了媒体获取与存储的相关API，主要包含在android.provider.MediaStorepackage中。