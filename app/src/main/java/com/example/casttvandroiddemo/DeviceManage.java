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
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.casttvandroiddemo.adapter.HistoryConnectedDeviceAdapter;
import com.example.casttvandroiddemo.bean.DeviceBean;
import com.example.casttvandroiddemo.helper.DeviceManageHelper;
import com.example.casttvandroiddemo.utils.OnlineDeviceUtils;
import com.example.casttvandroiddemo.utils.RemoteUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class DeviceManage extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DeviceManage";
    private List<DeviceBean> mData;
    private HistoryConnectedDeviceAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView iv_addDevice, iv_titleImage, iv_back, iv_refresh;
    private TextView tv_titleText, tv_updateDevice, tv_allSelectDelete, tv_topTitle;
    private Button btn_addDevice, btn_deleteDevice;
    private static final String SSDP_MSEARCH = "M-SEARCH * HTTP/1.1\r\n" +
            "Host: 239.255.255.250:1900\r\n" +
            "Man: \"ssdp:discover\"\r\n" +
            "ST: roku:ecp\r\n\r\n";
    private static final int BUFFER_SIZE = 4096;
    private Dialog dialog;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            iv_refresh.setVisibility(View.VISIBLE);
            if (adapter != null)
                adapter.notifyDataSetChanged();
            dialog.cancel();
            return false;
        }
    });
    private boolean isAllSelect = false;
    private boolean isDeleteSelect = false;
    private int delete_cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_manage);
        initView();
        loadData();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void loadData() {
        mData.clear();
        try {
            DeviceManageHelper helper = new DeviceManageHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(DeviceManageHelper.TABLE_HISTORY, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                String deviceUDN = cursor.getString(0);
                String deviceIpAddress = cursor.getString(1);
                String deviceName = cursor.getString(2);
                String deviceLocation = cursor.getString(3);
                int isOnline = 0;//默认不在线
                for (String item : OnlineDeviceUtils.mRokuLocation_onLine) {
                    if (item.contains(deviceIpAddress)) {
                        isOnline = 2;//在线
                        if (deviceIpAddress.equals(FragmentRemoteControl.RokuLocation))
                            isOnline = 1;//在线且已连接
                        break;
                    }
                }
                mData.add(new DeviceBean(deviceUDN, deviceName, deviceLocation, deviceIpAddress, isOnline));
            }
            cursor.close();
            helper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentViewBaseToData();
    }

    private void setContentViewBaseToData() {
        if (mData.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            iv_addDevice.setVisibility(View.VISIBLE);
            iv_titleImage.setVisibility(View.INVISIBLE);
            btn_addDevice.setVisibility(View.INVISIBLE);
            tv_titleText.setVisibility(View.INVISIBLE);
            tv_updateDevice.setVisibility(View.VISIBLE);
            iv_refresh.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            iv_addDevice.setVisibility(View.INVISIBLE);
            iv_titleImage.setVisibility(View.VISIBLE);
            btn_addDevice.setVisibility(View.VISIBLE);
            tv_titleText.setVisibility(View.VISIBLE);
            tv_updateDevice.setVisibility(View.INVISIBLE);
            iv_refresh.setVisibility(View.INVISIBLE);
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
        iv_titleImage = (ImageView) findViewById(R.id.iv_commonDevice_image);
        iv_refresh = (ImageView) findViewById(R.id.iv_refresh_deviceManage);

        btn_addDevice = (Button) findViewById(R.id.btn_goToAdd_device_manage);
        btn_deleteDevice = (Button) findViewById(R.id.btn_deleteDevice);

        tv_titleText = (TextView) findViewById(R.id.tv_common_device_title2);
        tv_updateDevice = (TextView) findViewById(R.id.tv_update_commonDevice);
        iv_back = (ImageView) findViewById(R.id.iv_back_deviceManage);
        tv_allSelectDelete = (TextView) findViewById(R.id.tv_allSelect_delete);
        tv_topTitle = (TextView) findViewById(R.id.tv_topTitle);

        //添加设备按钮，类型为ImageView
        iv_addDevice.setOnClickListener(this);
        //添加设备按钮，类型为Button
        btn_addDevice.setOnClickListener(this);
        //添加删除按钮，类型为Button
        btn_deleteDevice.setOnClickListener(this);
        //编辑按钮，类型为TextView
        tv_updateDevice.setOnClickListener(this);
        //返回按钮，类型为ImageView
        iv_back.setOnClickListener(this);
        //添加刷新按钮，类型为ImageView
        iv_refresh.setOnClickListener(this);
        //全选设备
        tv_allSelectDelete.setOnClickListener(this);
        //为RecycleView中的每一项设置监听器
        adapter.setOnItemClickListener(new HistoryConnectedDeviceAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                //连接操作
                if (!adapter.isDelete()) {
                    //如果设备不在线，不产生任何操作
                    if (mData.get(position).getIsOnline() == 0) return;
                    for (int i = 0; i < mData.size(); ++i) {
                        if (position == i && mData.get(i).getIsOnline() != 0) {
                            mData.get(i).setIsOnline(1);
                            FragmentRemoteControl.RokuLocation = mData.get(i).getUserDeviceIpAddress();
                            FragmentRemoteControl.RokuLocationUrl = RemoteUtils.getRokuLocationUrl(FragmentRemoteControl.RokuLocation);
                            try {
                                FragmentRemoteControl.ConnectingDevice = (DeviceBean) mData.get(i).clone();
                            } catch (CloneNotSupportedException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (position != i && mData.get(i).getIsOnline() == 1)
                            mData.get(i).setIsOnline(2);
                    }
                } else {
                    //删除操作
                    if (mData.get(position).getIsDelete() == 1) {
                        mData.get(position).setIsDelete(2);
                        delete_cnt++;
                    } else if (mData.get(position).getIsDelete() == 2) {
                        mData.get(position).setIsDelete(1);
                        delete_cnt--;
                    }
                    setDeleteStatus();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setDeleteStatus() {
        Log.d(TAG, "setDeleteStatus: " + delete_cnt);
        if (delete_cnt > 0) {
            btn_deleteDevice.setVisibility(View.VISIBLE);
            btn_deleteDevice.setBackgroundResource(R.drawable.shape_device_delete_100alpha);
            btn_deleteDevice.setEnabled(true);
        } else {
            btn_deleteDevice.setVisibility(View.VISIBLE);
            btn_deleteDevice.setBackgroundResource(R.drawable.shape_device_delete_50alpha);
            btn_deleteDevice.setEnabled(false);
        }

        if (delete_cnt == mData.size()) {
            tv_allSelectDelete.setText(R.string.Deselect);
        } else {
            tv_allSelectDelete.setText(R.string.Select_all);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void refresh() {
        mData.clear();
        loadData();
        iv_refresh.setVisibility(View.INVISIBLE);
        showRefreshDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendMessageDelayed(new Message(), 1000);
            }
        }).start();
    }


    public void editingDeviceStatus() {


        adapter.setDelete(true);
        isDeleteSelect = true;
        //UI设计
        tv_topTitle.setText(R.string.Select_device);
        tv_updateDevice.setText(R.string.Cancel);
        tv_allSelectDelete.setVisibility(View.VISIBLE);
        btn_deleteDevice.setVisibility(View.VISIBLE);
        btn_deleteDevice.setBackgroundResource(R.drawable.shape_device_delete_50alpha);
        btn_deleteDevice.setEnabled(false);
        iv_back.setVisibility(View.INVISIBLE);
        iv_refresh.setVisibility(View.INVISIBLE);
        iv_addDevice.setVisibility(View.INVISIBLE);
        btn_addDevice.setVisibility(View.INVISIBLE);

        //逻辑设计
        for (int i = 0; i < mData.size(); ++i) {
            mData.get(i).setIsDelete(1);
        }
        adapter.notifyDataSetChanged();
    }

    private void allSelect() {
        delete_cnt = mData.size();
        btn_deleteDevice.setBackgroundResource(R.drawable.shape_device_delete_100alpha);
        btn_deleteDevice.setEnabled(true);
        isAllSelect = true;
        tv_allSelectDelete.setText(R.string.Deselect);
        for (int i = 0; i < mData.size(); ++i) {
            mData.get(i).setIsDelete(2);
        }
        adapter.notifyDataSetChanged();
    }

    private void allUnselect() {
        delete_cnt = 0;
        btn_deleteDevice.setBackgroundResource(R.drawable.shape_device_delete_50alpha);
        btn_deleteDevice.setEnabled(false);
        isAllSelect = false;
        tv_allSelectDelete.setText(R.string.Select_all);
        for (int i = 0; i < mData.size(); ++i) {
            mData.get(i).setIsDelete(1);
        }
        adapter.notifyDataSetChanged();
    }

    private void cancelDelete() {
        delete_cnt = 0;
        adapter.setDelete(false);
        isDeleteSelect = false;
        isAllSelect = false;
        //UI设计
        tv_allSelectDelete.setText(R.string.Select_all);
        tv_topTitle.setText(R.string.Device_Management);
        tv_updateDevice.setText(R.string.Edit);
        tv_allSelectDelete.setVisibility(View.INVISIBLE);
        btn_deleteDevice.setVisibility(View.INVISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        iv_refresh.setVisibility(View.VISIBLE);
        setContentViewBaseToData();

        //逻辑设计
        for (int i = 0; i < mData.size(); ++i) {
            mData.get(i).setIsDelete(0);
        }
        adapter.notifyDataSetChanged();
    }

    private void deleteDevice() {
        DeviceManageHelper helper = new DeviceManageHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        for (int i = 0; i < mData.size(); ++i) {
            if (mData.get(i).getIsDelete() == 2) {
                db.delete(DeviceManageHelper.TABLE_HISTORY, DeviceManageHelper.USER_DEVICE_IPADDRESS + "=?", new String[]{mData.get(i).getUserDeviceIpAddress()});
                if (mData.get(i).getUserDeviceIpAddress().equals(FragmentRemoteControl.RokuLocation))
                    FragmentRemoteControl.RokuLocation = null;
            }
        }
        db.close();
        mData.clear();
        loadData();
        adapter.notifyDataSetChanged();
        cancelDelete();

    }

    public void showRefreshDialog() {
        dialog = new Dialog(this);
        dialog.setCancelable(false);
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

    private void showDeleteDialog() {
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_delete);
        dialog.findViewById(R.id.tv_cancelDelete_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.findViewById(R.id.tv_confirmDelete_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDevice();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_addDevice_device_manage:
            case R.id.btn_goToAdd_device_manage:
                Intent intent = new Intent(DeviceManage.this, DeviceAdd.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_update_commonDevice:
                if (!isDeleteSelect)
                    editingDeviceStatus();
                else
                    cancelDelete();
                break;
            case R.id.iv_back_deviceManage:
                finish();
                break;
            case R.id.iv_refresh_deviceManage:
                refresh();
                break;
            case R.id.tv_allSelect_delete:
                if (!isAllSelect)
                    allSelect();
                else
                    allUnselect();
                break;
            case R.id.btn_deleteDevice:
                showDeleteDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            loadData();
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    }
}