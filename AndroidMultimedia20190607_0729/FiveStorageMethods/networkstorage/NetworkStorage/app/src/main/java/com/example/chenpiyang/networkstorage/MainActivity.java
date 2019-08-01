package com.example.chenpiyang.networkstorage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//https://blog.csdn.net/w88193363/article/details/8617646
        //http://www.webservicex.net/WeatherForecast.asmx/GetWeatherByZipCode
        final String SERVER_URL = "https://blog.csdn.net/w88193363/article/details/8617646"; //定义需要获取的内容来源地址
        HttpPost request = new HttpPost(SERVER_URL); //根据内容来源地址创建一个Http请求
// 添加一个变量
        List<NameValuePair> params = new ArrayList<NameValuePair>();
// 设置一个华盛顿区号
        params.add(new BasicNameValuePair("ZipCode", "200120"));  //添加必须的参数
        try {
            request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); //设置参数的编码
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            HttpResponse httpResponse = new DefaultHttpClient().execute(request); //发送请求并获取反馈
// 解析返回的内容
            if(httpResponse.getStatusLine().getStatusCode() != 404)
            {
                String result = EntityUtils.toString(httpResponse.getEntity());
                System.out.println("==result=========="+result);
                Log.d(LOG_TAG, result);
            }
        } catch (Exception e) {
            //Log.e(LOG_TAG, e.getMessage());
        }
    }
}
