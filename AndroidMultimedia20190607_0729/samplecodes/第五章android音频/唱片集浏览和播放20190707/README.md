使用MediaStore唱片集Uri查询设备上的所有唱片集



String[] columns = {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM};
Cursor cursor1 = managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, columns, null, null, null);
if (cursor1 != null){
    while (cursor1.moveToNext()){
        Log.v("output",cursor1.getString(cursor1.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
    }
}
