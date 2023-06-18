package com.example.casttvandroiddemo.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.example.casttvandroiddemo.utils.OnlineDeviceUtils;

public class VolumeService extends Service {
    private static final String TAG = "VolumeService";
    private final String CONNECTIVITY_ACTION = ConnectivityManager.CONNECTIVITY_ACTION;
    private ConnectivityManager connectivityManager;

    private BroadcastReceiver mReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //这里处理监听到的消息
            String action = intent.getAction();
            if (CONNECTIVITY_ACTION.equals(action)) {
                if (connectivityManager == null) {
                    connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                }
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
                    Log.d(TAG, "onReceive: 当前网络状态：" + activeNetworkInfo.getTypeName());
                }else{
                    Log.d(TAG, "onReceive: 没有网络：");
                }

                OnlineDeviceUtils.findDevice();
            }
        }
    };

    public VolumeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //注册广播 并开启
        registerReceiver(mReciver, new IntentFilter(CONNECTIVITY_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReciver != null) {
            unregisterReceiver(mReciver);
        }
    }
}