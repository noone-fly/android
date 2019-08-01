package com.example.chenpiyang.sharepreferences;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String SETTING_INFOS = "SETTING_Infos";
    public static final String NAME = "NAME";
    public static final String PASSWORD = "PASSWORD";
    private EditText field_name;  //接收用户名的组件
    private EditText filed_pass;  //接收密码的组件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Find VIew
        field_name = (EditText) findViewById(com.example.chenpiyang.sharepreferences.R.id.name);  //首先获取用来输入用户名的组件
        filed_pass = (EditText) findViewById(com.example.chenpiyang.sharepreferences.R.id.password); //同时也需要获取输入密码的组件

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0); //获取一个SharedPreferences对象
        String name = settings.getString(NAME, "");  //取出保存的NAME
        String password = settings.getString(PASSWORD, ""); //取出保存的PASSWORD

        //Set value
        field_name.setText(name);  //将取出来的用户名赋予field_name
        filed_pass.setText(password);  //将取出来的密码赋予filed_pass
    }

    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0); //首先获取一个SharedPreferences对象
        settings.edit()
                .putString(NAME, field_name.getText().toString())
                .putString(PASSWORD, filed_pass.getText().toString())
                .commit();
    } //将用户名和密码保存进去

}
