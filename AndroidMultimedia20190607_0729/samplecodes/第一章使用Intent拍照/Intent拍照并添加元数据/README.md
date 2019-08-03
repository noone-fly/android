

1，通过Intent 启动 Camera应用程序
2，BitmapFactory.Options  缩放图片，获取inSampleSize 缩放比例
3，对图片解码
BitmapFactory.decodeFile(imageFilePah, options)
      BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options); 

4，MediaStore  图像的内容提供器
保存图片到设备的标准位置
getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());


查询它来快速构建使用已经捕获的图像
//指定想要返回的列的字符串数组，并传给managedQuery方法
String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.TITLE, MediaStore.Images.Media.DISPLAY_NAME};
//返回所有图片
cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);





元数据（Metadata），又称中介数据、中继数据，为描述数据的数据（data about data），主要是描述数据属性（property）的信息，用来支持如指示存储位置、历史数据、资源查找、文件记录等功能


android 有一套应用程序之间共享数据的标准方法，负责这些功能的类称为内容提供器，内容提供器为不同类型数据的存储和检索提供了一个标准接口。

图像（以及音视频）的标准内容提供器是 MediaStore,  MediaStore 是设备上的一个标准位置存放文件的设置，并且为存储和检索该文件的元数据提供便利，

元数据是关于数据的数据，它可以包含文件本身的数据信息， 如文件的大小和名称，
但MediaStore 还允许设置各种其他数据，如标题，描述，经度，维度。


1.2.1 获取图像的Uri
为了获取存储图像的标准位置，先需要获取MediaStore的引用，为此需要使用一个内容解析器
内容解析器是用于访问内容提供器（例如MediaStore）的方法

provider.MediaStore.Images.Media 
EXTERNAL_CONTENT_URI  将图像存在设备的主要外部存储器（sdcard）
INTERNAL_CONTENT_URI   将图像存在设备的内存中


预填充关联元数据
//更新MediaStore中记录的标题和描述
//预填充关联元数据，使用put方法，key-value，
descriptionTextView = (TextView) findViewById(R.id.DescriptionTextView);
titleEditText = (EditText) findViewById(R.id.TitleEditView);

ContentValues contentValues = new ContentValues(3);
contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, titleEditText.getText().toString());
contentValues.put(MediaStore.Images.Media.DESCRIPTION, descriptionEditText.getText().toString());
contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");


//添加一条不带位图的新记录
                //返回新记录的Uri
                //Uri insert(Uri url, ContentValues values) 将一组数据插入到Uri 指定的地方，返回新inserted的URI。
                //ContentValues 和HashTable类似都是一种存储的机制 但是两者最大的区别就在于，contenvalues只能存储基本类型的数据，像string，int之类的
                //I/System.out: MediaStore.Images.Media.EXTERNAL_CONTENT_URI==content://media/external/images/media
                //android里，当Uri以content开头，它将由内容提供器使用（如MediaStore）
                //insert方法返回新记录的Uri
                imageFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
                //启动Camera应用程序
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //传递你要保存的图片的路径
                //MediaStore.EXTRA_OUTPUT 将拍摄的照片存储在SDcard
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
//如果想在Activity中得到新打开Activity 关闭后返回的数据，需要使用系统提供的startActivityForResult(Intent intent, int requestCode)方法打开新的Activity，新的Activity 关闭后会向前面的Activity传回数据，为了得到传回的数据，必须在前面的Activity中重写onActivityResult(int requestCode, int resultCode, Intent data)方法
                //CAMERA_RESULT
                startActivityForResult(intent, CAMERA_RESULT);


检索保存的图像
对于之前所获得的用来保存图像的相同uri，同样也可以用于访问该图像，无须将该文件的完整路径传递给BitmapFactory, 相反，我们可以通过内容解析器为图像打开一个InputStream,  并将之传给 BitmapFactory
//加载图像的尺寸，而非图像本身
BitmapFactory.Options options = new BitmapFactory.Options();
options.inJustDecodeBounds = true;
Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, options);


后期添加元数据
将图像捕获到MediaStore之后，如果希望将图像与更多元数据关联，可以使用内容解析器的update方法，
ContentValues contentValues = new ContentValues(3);
contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, titleEditText.getText().toString());
contentValues.put(MediaStore.Images.Media.DESCRIPTION, descriptionEditText.getText().toString());
getContentResolver().update(imageFileUri, contentValues, null, null);








