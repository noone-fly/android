package com.example.chenpiyang.getrequest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;


public class GetRequestActivity extends AppCompatActivity {
    public static final int INTERNAT_PERMISSION = 1;
    public static final String TAG = "GET REQUEST";
    String requestUrl = "https://api.agora.io/dev/v1/project/?id=fd33b96dc23c417fac91579efff42701&name=IOS";
    HttpURLConnection connection = null;
    URL url = null;

    BufferedReader bufferedReader = null;
    StringBuilder stringBuilder = new StringBuilder();
    String responseString = null;
    String encoding = Base64.encodeToString("118f0fc996234f50bd2c8626e17dfd42:a840047a26fb408dbc11f1853f4c393b".getBytes(Charset.forName("utf-8")), Base64.NO_WRAP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_request);
        getRequest();
    }

    private void getRequest() {
        //在子线程执行请求
        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    // 新建一个URL对象
                    url = new URL(requestUrl);
                    // 打开一个HttpURLConnection连接
                    connection = (HttpURLConnection) url.openConnection();
                    // 设置连接主机超时时间
                    connection.setConnectTimeout(5 * 1000);
                    //设置从主机读取数据超时
                    connection.setReadTimeout(5 * 1000);
                    // 设置是否使用缓存  默认是true
                    connection.setUseCaches(true);
                    // 设置为Post请求
                    connection.setRequestMethod("GET");
                    //urlConn设置请求头信息
                    //设置请求中的媒体类型信息。
                    connection.setRequestProperty("Content-Type", "application/json");

                    //设置客户端与服务连接类型
                    connection.addRequestProperty("Connection", "Keep-Alive");
                    connection.addRequestProperty("Authorization", "Basic " + encoding);
                    // 开始连接
                    connection.connect();
                    Log.v(TAG, String.valueOf(connection.getResponseCode()));
                    // 判断请求是否成功
                    if (connection.getResponseCode() == 200) {
                        // 获取返回的数据
                        String result = streamToString(connection.getInputStream());
                        Log.e(TAG, "Get方式请求成功，result--->" + result);
                    } else {
                        Log.e(TAG, "Get方式请求失败");
                    }
                    // 关闭连接
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                HttpClient httpClient = new DefaultHttpClient();
//                HttpGet httpGet = new HttpGet("https://api.agora.io/dev/v1/project/?id=fd33b96dc23c417fac91579efff42701&name=IOS");
//                Log.v("base64 string ", encoding);
//                httpGet.setHeader("Authorization", "Basic " + encoding);
//                StringBuilder stringBuilder = new StringBuilder();
//                try {
//                    HttpResponse httpResponse = httpClient.execute(httpGet);
//                    HttpEntity httpEntity = httpResponse.getEntity();
//
//                    StatusLine statusLine = httpResponse.getStatusLine();// 获取请求对象中的响应行对象
//                    int responseCode = statusLine.getStatusCode();// 从状态行中获取状态码
//                    System.out.println(responseCode);
//
//                    if (httpEntity != null){
//                        InputStream inputStream = httpEntity.getContent();
//                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//                        String currentline = null;
//                        while ((currentline = bufferedReader.readLine()) != null){
//                            Log.v("currentline ", currentline);
//                            stringBuilder.append(currentline + "\n");
//                        }
//                        String result = stringBuilder.toString();
//                        Log.v("http request ", result);
//                        inputStream.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        }.start();
    }

    private String streamToString(InputStream inputStream){
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String currentLine = null;
        try {
            while ((currentLine=bufferedReader.readLine())!=null){
                stringBuilder.append(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseString = stringBuilder.toString();
        return responseString;
    }

}
