https://blog.csdn.net/lanjingling09/article/details/76954131

（1）使用HttpURLConnection
Android6.0之前发送HTTP请求一般有两种方式：HttpURLConnection和HttpClient（API数量过多、扩展困难等缺点）。Android6.0中HttpClient功能被完全移除。


HttpURLConnection用法： 
a、获取HttpURLConnection实例。new一个URL对象，传入目标网络地址，调用openConnection()方法：

URL url = new URL("http://www.baidu.com");
HttpURLConnection connection = (HttpURLConnection) url.openConnection();

b、设置HTTP请求所使用的方法。常用两种：GET（希望从服务器获取数据）和POST（希望提交数据给服务器）。写法：

connection.setRequestMethod("GET");

c、进行自由定制，如设置连接超时、读取超市的毫秒数，及服务器希望得到的一些消息头等。如：

connection.setConnectTimeout(8000);
connection.setReadTimeout(8000);

d、调用getInputStream()获取服务器返回的输入流，读取输入流：

InputStream in = connection.getInputStream();

e、调用disconnect()方法关闭HTTP连接。如：

connection.disconnect();

