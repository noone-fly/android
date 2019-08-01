package com.example.chenpiyang.simpleservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button startServiceButton;
    Button stopServiceButton;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startServiceButton = (Button)this.findViewById(R.id.StartServiceButton);
        stopServiceButton = (Button)this.findViewById(R.id.StopServiceButton);
        startServiceButton.setOnClickListener(this);
        stopServiceButton.setOnClickListener(this);
        //当实例化用量启动和停止服务的意图时，可以传入作为上下文的活动，后跟该服务类
        serviceIntent = new Intent(this, SimpleService.class);
    }


    @Override
    public void onClick(View view) {
        if (view == startServiceButton){
            startService(serviceIntent);
        }else if (view == stopServiceButton){
            stopService(serviceIntent);
        }
    }
}
