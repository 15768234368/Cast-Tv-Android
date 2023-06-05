package com.example.casttvandroiddemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.casttvandroiddemo.adapter.HistoryConnectedDeviceAdapter;
import com.example.casttvandroiddemo.bean.DeviceBean;
import com.example.casttvandroiddemo.helper.DeviceManageHelper;
import com.example.casttvandroiddemo.utils.OnlineDeviceUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class DeviceManage extends AppCompatActivity {
    private static final String TAG = "DeviceManage";
    private List<DeviceBean> mData;
    private HistoryConnectedDeviceAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView iv_addDevice;
    private static final String SSDP_MSEARCH = "M-SEARCH * HTTP/1.1\r\n" +
            "Host: 239.255.255.250:1900\r\n" +
            "Man: \"ssdp:discover\"\r\n" +
            "ST: roku:ecp\r\n\r\n";
    private static final int BUFFER_SIZE = 4096;
    private String RokuLocation = null;
    private Dialog dialog;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if(adapter != null)
                adapter.notifyDataSetChanged();
            dialog.cancel();
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manage);
        initView();
    }

    private void loadData() {
        try {
            DeviceManageHelper helper = new DeviceManageHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(DeviceManageHelper.TABLE_HISTORY, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String deviceName = cursor.getString(1);
                String deviceLocation = cursor.getString(2);
                String deviceIpAddress = cursor.getString(0);
                int isOnline = 0;//默认不在线
                for(String item: OnlineDeviceUtils.mRokuLocation_onLine){
                    if (item.contains(deviceIpAddress)) {
                        isOnline = 2;//在线
                        if(deviceIpAddress.equals(FragmentRemoteControl.RokuLocation))
                            isOnline = 1;//在线且已连接
                        break;
                    }
                }
                mData.add(new DeviceBean(deviceName, deviceLocation, deviceIpAddress, isOnline));
            }
            cursor.close();
            helper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void initView() {
        mData = new ArrayList<DeviceBean>();
        adapter = new HistoryConnectedDeviceAdapter(mData, this);
        recyclerView = (RecyclerView) findViewById(R.id.rv_historyConnectedDevice);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        iv_addDevice = (ImageView) findViewById(R.id.iv_addDevice_device_manage);
        iv_addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DeviceManage.this, DeviceAdd.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        mData.clear();
        loadData();
        if(adapter != null)
        adapter.notifyDataSetChanged();
        super.onResume();
    }
    public void refresh(View view){
        mData.clear();
        showRefreshDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadData();
                handler.sendMessageDelayed(new Message(), 1000);
            }
        }).start();
    }
    public void showRefreshDialog(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_refresh);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.findViewById(R.id.iv_closeDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}