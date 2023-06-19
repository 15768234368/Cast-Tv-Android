package com.example.casttvandroiddemo;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.casttvandroiddemo.bean.DeviceBean;
import com.example.casttvandroiddemo.helper.DeviceManageHelper;
import com.example.casttvandroiddemo.utils.OnlineDeviceUtils;
import com.example.casttvandroiddemo.utils.RemoteUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentRemoteControl extends Fragment implements View.OnClickListener {
    private static final String TAG = "FragmentRemoteControl";
    private View view;
    private ImageButton iv_up, iv_down, iv_left, iv_right, iv_enter, iv_disconnect;
    private ImageView iv_setting, iv_isConnect;
    private ImageButton iv_back, iv_home;
    private LinearLayout ll_keyboard, ll_channel;
    private ImageButton iv_rewind, iv_pause, iv_forward, iv_refresh;
    private ImageButton iv_menu, iv_volumeDown, iv_volumeUp, iv_volumeMute;
    private TextView tv_selectDevice;
    public static DeviceBean ConnectingDevice;
    public static String RokuLocation = null;
    public static String RokuLocationUrl = RemoteUtils.getRokuLocationUrl(RokuLocation);
    private EditText et_keyboard;
    private Vibrator vibrator;
    private View coverView;
    private View view_disconnect_coverBlack10, view_keyboard_coverBlack10, view_channel_coverBlack10;
    private View view_back_coverBlack10, view_home_coverBlack10, view_rewind_coverBlack10, view_play_pause_coverBlack10;
    private View view_forward_coverBlack10, view_backspace_coverBlack10, view_menu_coverBlack10, view_volume_down_coverBlack10;
    private View view_volume_mute_coverBlack10, view_volume_up_coverBlack10;
    private View view_ok_coverBlack10;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                //更新主线程
                setConnectionStatus(RokuLocation != null);
            }
            return false;

        }
    });

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "FragmentRemote Control onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    public void setBackEvent() {
        if (OnlineDeviceUtils.mDeviceData_onLine.size() > 0) {
            DeviceManageHelper helper = new DeviceManageHelper(getContext());
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = null;
            for (DeviceBean bean : OnlineDeviceUtils.mDeviceData_onLine) {
                cursor = db.query(DeviceManageHelper.TABLE_HISTORY, null, DeviceManageHelper.USER_DEVICE_UDN + "=?", new String[]{bean.getUserDeviceUDN()}, null, null, null, null);
                if (cursor.getCount() > 0) {
                    RokuLocation = bean.getUserDeviceIpAddress();
                    RokuLocationUrl = RemoteUtils.getRokuLocationUrl(RokuLocation);
                    ConnectingDevice = bean;
                    break;
                }
            }
            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 添加布局监听器
        Log.d(TAG, "onCreateView: ");
        return view = inflater.inflate(R.layout.fragment_remote_control_tab, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        et_keyboard = view.findViewById(R.id.et_keyboard_edit_homepage);

        view_disconnect_coverBlack10 = view.findViewById(R.id.view_disconnect_coverBlack10);
        view_keyboard_coverBlack10 = view.findViewById(R.id.view_keyboard_coverBlack10);
        view_channel_coverBlack10 = view.findViewById(R.id.view_channel_coverBlack10);
        view_back_coverBlack10 = view.findViewById(R.id.view_back_coverBlack10);
        view_home_coverBlack10 = view.findViewById(R.id.view_home_coverBlack10);
        view_rewind_coverBlack10 = view.findViewById(R.id.view_rewind_coverBlack10);
        view_play_pause_coverBlack10 = view.findViewById(R.id.view_play_pause_coverBlack10);
        view_forward_coverBlack10 = view.findViewById(R.id.view_forward_coverBlack10);
        view_backspace_coverBlack10 = view.findViewById(R.id.view_backspace_coverBlack10);
        view_menu_coverBlack10 = view.findViewById(R.id.view_menu_coverBlack10);
        view_volume_down_coverBlack10 = view.findViewById(R.id.view_volume_down_coverBlack10);
        view_volume_mute_coverBlack10 = view.findViewById(R.id.view_volume_mute_coverBlack10);
        view_volume_up_coverBlack10 = view.findViewById(R.id.view_volume_up_coverBlack10);
        view_ok_coverBlack10 = view.findViewById(R.id.view_ok_coverBlack10);

        coverView = view.findViewById(R.id.view_coverBlack80);
        iv_setting = view.findViewById(R.id.iv_setting_homepage);
        iv_isConnect = view.findViewById(R.id.iv_isConnected);
        iv_disconnect = view.findViewById(R.id.iv_disconnect_homepage);
        iv_disconnect.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_disconnect_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_disconnect_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_disconnect);
                return true;
            }
            return false;
        });
        tv_selectDevice = view.findViewById(R.id.tv_select_device_homepage);
        ll_keyboard = view.findViewById(R.id.ll_keyboard_homepage);
        ll_keyboard.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_keyboard_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_keyboard_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(ll_keyboard);
                return true;
            }
            return false;
        });
        ll_channel = view.findViewById(R.id.ll_channel_homepage);
        ll_channel.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_channel_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_channel_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(ll_channel);
                return true;
            }
            return false;
        });

        iv_up = view.findViewById(R.id.iv_up_homepage);
        iv_up.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                iv_up.setBackgroundResource(R.mipmap.up_press);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                iv_up.setBackgroundResource(R.mipmap.up);
                onClick(iv_up);
                return true;
            }
            return false;
        });
        iv_down = view.findViewById(R.id.iv_down_homepage);
        iv_down.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                iv_down.setBackgroundResource(R.mipmap.down_press);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                iv_down.setBackgroundResource(R.mipmap.down);
                onClick(iv_down);
                return true;
            }
            return false;
        });
        iv_left = view.findViewById(R.id.iv_left_homepage);
        iv_left.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                iv_left.setBackgroundResource(R.mipmap.left_press);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                iv_left.setBackgroundResource(R.mipmap.left);
                onClick(iv_left);
                return true;
            }
            return false;
        });
        iv_right = view.findViewById(R.id.iv_right_homepage);
        iv_right.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                iv_right.setBackgroundResource(R.mipmap.right_press);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                iv_right.setBackgroundResource(R.mipmap.right);
                onClick(iv_right);
                return true;
            }
            return false;
        });
        iv_enter = view.findViewById(R.id.iv_ok_homepage);

        iv_enter.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_ok_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_ok_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_enter);
                return true;
            }
            return false;
        });

        iv_back = view.findViewById(R.id.iv_back_homepage);
        iv_back.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_back_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_back_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_back);
                return true;
            }
            return false;
        });
        iv_home = view.findViewById(R.id.iv_home_homepage);
        iv_home.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_home_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_home_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_home);
                return true;
            }
            return false;
        });
        iv_rewind = view.findViewById(R.id.iv_rewind_homepage);
        iv_rewind.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_rewind_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_rewind_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_rewind);
                return true;
            }
            return false;
        });
        iv_pause = view.findViewById(R.id.iv_play_pause_homepage);
        iv_pause.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_play_pause_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_play_pause_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_pause);
                return true;
            }
            return false;
        });
        iv_forward = view.findViewById(R.id.iv_forward_homepage);
        iv_forward.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_forward_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_forward_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_forward);
                return true;
            }
            return false;
        });
        iv_refresh = view.findViewById(R.id.iv_backspace_homepage);
        iv_refresh.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_backspace_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_backspace_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_refresh);
                return true;
            }
            return false;
        });
        iv_menu = view.findViewById(R.id.iv_menu_homepage);
        iv_menu.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_menu_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_menu_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_menu);
                return true;
            }
            return false;
        });
        iv_volumeDown = view.findViewById(R.id.iv_volume_down_homepage);
        iv_volumeDown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_volume_down_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_volume_down_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_volumeDown);
                return true;
            }
            return false;
        });
        iv_volumeMute = view.findViewById(R.id.iv_volume_mute_homepage);
        iv_volumeMute.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_volume_mute_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_volume_mute_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_volumeMute);
                return true;
            }
            return false;
        });
        iv_volumeUp = view.findViewById(R.id.iv_volume_up_homepage);
        iv_volumeUp.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_volume_up_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_volume_up_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_volumeUp);
                return true;
            }
            return false;
        });
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getContext(), "设置");
                Intent intent_setting = new Intent(getContext(), SettingActivity.class);
                startActivity(intent_setting);
            }
        });
        iv_disconnect.setOnClickListener(this);
        tv_selectDevice.setOnClickListener(this);
        ll_keyboard.setOnClickListener(this);
        ll_channel.setOnClickListener(this);
        iv_up.setOnClickListener(this);
        iv_down.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        iv_enter.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        iv_rewind.setOnClickListener(this);
        iv_pause.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
        iv_volumeDown.setOnClickListener(this);
        iv_volumeMute.setOnClickListener(this);
        iv_volumeUp.setOnClickListener(this);

        vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        OnlineDeviceUtils.setOnConnectedListener(new OnlineDeviceUtils.OnConnectedListener() {
            @Override
            public void autoConnect() {
                setBackEvent();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setConnectionStatus(RokuLocation != null);
                    }
                });
            }

            @Override
            public void disConnect() {
                if (OnlineDeviceUtils.mDeviceData_onLine.size() <= 0) {
                    Log.d(TAG, "deviceData_online is zero");
                    RokuLocation = null;
                    RokuLocationUrl = null;
                    ConnectingDevice = null;
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

        });
//
//        coverView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(TAG, "onTouch: ");
//                return false;
//
//            }
//        });

    }

    @Override
    public void onClick(View v) {
        if (SettingActivity.isVibrator && vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(20L);
        }
        if (RokuLocation == null) {
            DeviceManageHelper helper = new DeviceManageHelper(getContext());
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(DeviceManageHelper.TABLE_HISTORY, null, null, null, null, null, null);
            if (cursor.getCount() <= 0) {
                Intent intent = new Intent(getContext(), DeviceAdd.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(getContext(), DeviceManage.class);
                startActivity(intent);
            }
            return;
        }
        switch (v.getId()) {
            case R.id.iv_disconnect_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/PowerOff");
                MobclickAgent.onEvent(getContext(), "关机");
                break;
            case R.id.tv_select_device_homepage:
//                findDevice();
                Intent intent = new Intent(getContext(), DeviceManage.class);
                startActivity(intent);
                MobclickAgent.onEvent(getContext(), "切换设备");
                break;
            case R.id.ll_keyboard_homepage:
                MainActivity.keypress_board = true;
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                coverView.setVisibility(View.VISIBLE);
                MobclickAgent.onEvent(getContext(), "键盘");
                break;
            case R.id.ll_channel_homepage:
                launchChannel();
                break;
            case R.id.iv_up_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Up");
                MobclickAgent.onEvent(getContext(), "上");
                break;
            case R.id.iv_down_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Down");
                MobclickAgent.onEvent(getContext(), "下");
                break;
            case R.id.iv_left_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Left");
                MobclickAgent.onEvent(getContext(), "左");
                break;
            case R.id.iv_right_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Right");
                MobclickAgent.onEvent(getContext(), "右");
                break;
            case R.id.iv_ok_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Select");
                MobclickAgent.onEvent(getContext(), "确定");
                break;
            case R.id.iv_back_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Back");
                MobclickAgent.onEvent(getContext(), "返回");
                break;
            case R.id.iv_home_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Home");
                MobclickAgent.onEvent(getContext(), "首页");
                break;
            case R.id.iv_rewind_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Rev");
                MobclickAgent.onEvent(getContext(), "倒退");
                break;
            case R.id.iv_play_pause_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Play");
                MobclickAgent.onEvent(getContext(), "播放/暂停");
                break;
            case R.id.iv_forward_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Fwd");
                MobclickAgent.onEvent(getContext(), "前进");
                break;
            case R.id.iv_backspace_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Backspace");
                MobclickAgent.onEvent(getContext(), "刷新");
                break;
            case R.id.iv_menu_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Info");
                MobclickAgent.onEvent(getContext(), "菜单");
                break;
            case R.id.iv_volume_down_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/VolumeDown");
                MobclickAgent.onEvent(getContext(), "音量减");
                break;
            case R.id.iv_volume_mute_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/VolumeMute");
                MobclickAgent.onEvent(getContext(), "静音");
                break;
            case R.id.iv_volume_up_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/VolumeUp");
                MobclickAgent.onEvent(getContext(), "音量加");
                break;
        }
    }


    private void launchChannel() {
        OkHttpClient client = new OkHttpClient();
        String url = RokuLocationUrl + "query/apps";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: query:" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String channelXml = response.body().string();
                Log.d(TAG, "onResponse: query" + channelXml);
                String[] lines = channelXml.split("\n");
                boolean isInstall = false;
                for (String line : lines) {
                    if (line.startsWith("\t<app id=\"706370\"")) {
                        RemoteUtils.httpPost(RokuLocationUrl, "launch/706370"); //已经存在该频道，无需安装，直接启动
                        isInstall = true;
                    }
                }
                if (!isInstall)
                    RemoteUtils.httpPost(RokuLocationUrl, "install/706370");//未存在该频道，需要安装
            }
        });
    }


    @Override
    public void onResume() {
        OnlineDeviceUtils.setOnConnectedListener(new OnlineDeviceUtils.OnConnectedListener() {
            @Override
            public void autoConnect() {
                setBackEvent();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setConnectionStatus(RokuLocation != null);
                    }
                });
            }

            @Override
            public void disConnect() {
                if (OnlineDeviceUtils.mDeviceData_onLine.size() <= 0) {
                    Log.d(TAG, "deviceData_online is zero");
                    FragmentRemoteControl.RokuLocation = null;
                    FragmentRemoteControl.RokuLocationUrl = null;
                    FragmentRemoteControl.ConnectingDevice = null;
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

        });
        setConnectionStatus(RokuLocation != null);
        super.onResume();
        if (et_keyboard != null)
            et_keyboard.clearFocus();
    }


    private void setConnectionStatus(boolean flag) {
        Log.d(TAG, "setConnectionStatus-flag: " + flag);
        if (flag) {
            RokuLocationUrl = RemoteUtils.getRokuLocationUrl(RokuLocation);
            String deviceName = ConnectingDevice.getUserDeviceName();
            iv_isConnect.setImageResource(R.mipmap.connected_homepage);

            tv_selectDevice.setText(deviceName);
            Log.d(TAG, "setConnectionStatus-RokuLocationUrl: " + RokuLocationUrl);

        } else {
            RokuLocationUrl = null;
            iv_isConnect.setImageResource(R.mipmap.no_connected);
            tv_selectDevice.setText(R.string.Select_device);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (et_keyboard != null)
            et_keyboard.clearFocus();
    }


}
