package com.example.chenpiyang.simpleservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class SimpleService extends Service {
    public SimpleService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("simpleservice","onCreate");

        Toast.makeText(this, "simple service onCreate.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("simpleservice","onStartCommand");
        Toast.makeText(this, "simple service onStartCommand.", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.v("simpleservice","onStart");
        Toast.makeText(this, "simple service onStart.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("simpleservice","onDestroy");
        Toast.makeText(this, "simple service onDestroy.", Toast.LENGTH_SHORT).show();
    }
}
