3.3 基本的图像缩放和旋转
现在开始探索图像编辑和处理， 学习如何执行空间转换操作，例如改变比例和旋转图像


3.3.1 输入矩阵
android 有一个 Matrix (矩阵)类，当在现有位图对象上进行绘制或从另一个位图对象创建某个图像对象时， 可以使用之，
这个类使得我们能够在一副图像上应用空间转换，这种类型的转换可以是旋转，裁剪，缩放或更改图像的坐标空间

Matrix 类以9个数字的数组表示转换，这些数字由公式生成， 该公司在数学上表示应该发生的旋转
例如， 对应旋转公式，可以通过使用正弦和余弦生成矩阵的数字

Matrix 中的每个数字都将应用于图像上每个点的三个坐标 (x, y, z)
1  0  0
0  1  0
0  0  1

顶点(1,0,0) 指定源图像的x坐标  根据此公式转换： x=1x+0y+0z
第二行(0,1,0) 意味着y坐标被确定为 y=0x+1y+0z
第三行（0，0，1）意味着z坐标确定为 z=0x+0y+1z

Matrix matrix = new Matrix();
matrix.setValues(new float[]{
1,0,0,
0,1,0,
0,0,1
});
当在画布上绘制一张位图时，可以用该Matrix对象
canvas.drawBitmap(bitmap, matrix, paint);

如果将第一个数字从1 改为 .5
那么x 轴上将图像压缩50%

Matrix matrix = new Matrix();
matrix.setValues(new float[]{
.5f, 0, 0,
0, 1, 0,
0, 0, 1
});
canvas.drawBitmap(bitmap, matrix, paint);

代码：Matrix 自定义矩阵 x 轴上将图像压缩50%.note

Bitmap alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),bitmap.getConfig());
Canvas canvas = new Canvas(alteredBitmap);
Paint paint = new Paint();
//canvas.drawBitmap(bitmap, 0, 0, paint);
Matrix matrix = new Matrix();
matrix.setValues(new float[]{
        .5f, 0, 0,
        0, 1, 0,
        0, 0, 1
});
canvas.drawBitmap(bitmap, matrix, paint);



如果改变矩阵，试x坐标也受源图像的y坐标影响， 可以修改第二个数字，
图像向右倾斜， 是因为第一行数字导致的倾斜，操作每个像素的x值，根据每个像素的y值进行改变，随着y值增加，向图像的下部分移动， 所以x值增加，导致图像倾斜， 如果使用一个负值，则向左倾斜，
同时，图像因为坐标变化而被截断，因此需要增加位图的大小
Matrix matrix = new Matrix();
matrix.setValues(new float[]{
1, .5f, 0,
0, 1, 0,
0, 0, 1
});
canvas.drawBitmap(bitmap, matrix, paint);

代码：Matrix 自定义矩阵 图像倾斜.note

Bitmap alteredBitmap = Bitmap.createBitmap(bitmap.getWidth()*2, bitmap.getHeight(),bitmap.getConfig());
Canvas canvas = new Canvas(alteredBitmap);
Paint paint = new Paint();
//canvas.drawBitmap(bitmap, 0, 0, paint);
Matrix matrix = new Matrix();
matrix.setValues(new float[]{
        1, .5f, 0,
        0, 1, 0,
        0, 0, 1
});
canvas.drawBitmap(bitmap, matrix, paint);



3.3.2  Matrix 类的方法
Matrix类的其他方法，帮助我们完成大部分工作，无须重新学习高中大学数学

1，旋转
setRotate 
采用一个浮点数表示旋转的角度，围绕默认点(0,0)  左上角
正数将顺时针旋转图像，
负数将逆时针旋转图像
Matrix matrix = new Matrix();
matrix.setRotate(15); // 按照左上角(0,0)旋转15度
canvas.drwaBitmap(bitmap, matrix, paint);

//按照图像中心旋转15度
matrix.setRotate(15, bitmap.getWidth()/2, bitmap.getHeight()/2) 

Matrix方法 setRotate 图像旋转.note

 Bitmap alteredBitmap = Bitmap.createBitmap(bitmap.getWidth()*2, bitmap.getHeight(),bitmap.getConfig());
                Canvas canvas = new Canvas(alteredBitmap);
                Paint paint = new Paint();
                //canvas.drawBitmap(bitmap, 0, 0, paint);
                Matrix matrix = new Matrix();
                //matrix.setRotate(15); //围绕左上角(0,0)进行顺时针旋转15读
                matrix.setRotate(15, bitmap.getWidth()/2, bitmap.getHeight()/2);
//                matrix.setValues(new float[]{
//                        1, .5f, 0,
//                        0, 1, 0,
//                        0, 0, 1
//                });
                canvas.drawBitmap(bitmap, matrix, paint);



2，缩放
setScale 
matrix.setScale(1.5f, 1)
第一个参数是x轴的缩放比例
第二个参数是y轴的缩放比例


Bitmap alteredBitmap = Bitmap.createBitmap(bitmap.getWidth()*2, bitmap.getHeight(),bitmap.getConfig());
Canvas canvas = new Canvas(alteredBitmap);
Paint paint = new Paint();
//canvas.drawBitmap(bitmap, 0, 0, paint);
Matrix matrix = new Matrix();
matrix.setScale(1.5f, 1);
//matrix.setRotate(15); //围绕左上角(0,0)进行顺时针旋转15读
matrix.setRotate(15, bitmap.getWidth()/2, bitmap.getHeight()/2);
canvas.drawBitmap(bitmap, matrix, paint);



3, 平移
Matrix 最有用的方法之一
setTranslate(1.5f, -10)
第一个参数是x轴的移动，正数向右移动，负数向左移动
第二个参数是y轴的移动，正数向下， 负数向上



4，之前和之后的版本

matrix.setScale(1.5f, 1)
matrix.postRotate(15, bitmap.getWidth()/2, bitmap.getHeight()/2)



5,  镜像