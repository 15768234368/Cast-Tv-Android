package com.example.casttvandroiddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.casttvandroiddemo.bean.DeviceBean;
import com.example.casttvandroiddemo.helper.DeviceManageHelper;
import com.example.casttvandroiddemo.utils.OnlineDeviceUtils;
import com.example.casttvandroiddemo.utils.RemoteUtils;
import com.example.casttvandroiddemo.utils.ViewUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteControlActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RemoteControlActivity";
    private ImageView iv_up, iv_down, iv_left, iv_right, iv_enter;
    private ImageView iv_close, iv_disconnect, iv_isConnect;
    private ImageView iv_back, iv_home;
    private LinearLayout ll_keyboard, ll_channel;
    private ImageView iv_rewind, iv_pause, iv_forward, iv_refresh;
    private ImageView iv_menu, iv_volumeDown, iv_volumeUp, iv_volumeMute;
    private TextView tv_selectDevice;
    public String RokuLocation = FragmentRemoteControl.RokuLocation;
    public String RokuLocationUrl = FragmentRemoteControl.RokuLocationUrl;
    private Vibrator vibrator;
    private View coverView;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener;

    //键盘弹出的变量
    private LinearLayout ll_edit;
    private EditText et_edit;
    private ImageView iv_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        initView();
    }

    private void initView() {

        ll_edit = (LinearLayout) findViewById(R.id.ll_keyboard_edit_homepage);
        et_edit = (EditText) findViewById(R.id.et_keyboard_edit_homepage);
        iv_edit = (ImageView) findViewById(R.id.iv_keyboard_edit_homepage);
        coverView = findViewById(R.id.view_coverBlack80);

        iv_close = findViewById(R.id.iv_close_homepage);
        iv_isConnect = findViewById(R.id.iv_isConnected);
        iv_disconnect = findViewById(R.id.iv_disconnect_homepage);
        tv_selectDevice = findViewById(R.id.tv_select_device_homepage);
        ll_keyboard = findViewById(R.id.ll_keyboard_homepage);
        ll_channel = findViewById(R.id.ll_channel_homepage);
        iv_up = findViewById(R.id.iv_up_homepage);
        iv_down = findViewById(R.id.iv_down_homepage);
        iv_left = findViewById(R.id.iv_left_homepage);
        iv_right = findViewById(R.id.iv_right_homepage);
        iv_enter = findViewById(R.id.iv_ok_homepage);
        iv_back = findViewById(R.id.iv_back_homepage);
        iv_home = findViewById(R.id.iv_home_homepage);
        iv_rewind = findViewById(R.id.iv_rewind_homepage);
        iv_pause = findViewById(R.id.iv_play_pause_homepage);
        iv_forward = findViewById(R.id.iv_forward_homepage);
        iv_refresh = findViewById(R.id.iv_backspace_homepage);
        iv_menu = findViewById(R.id.iv_menu_homepage);
        iv_volumeDown = findViewById(R.id.iv_volume_down_homepage);
        iv_volumeMute = findViewById(R.id.iv_volume_mute_homepage);
        iv_volumeUp = findViewById(R.id.iv_volume_up_homepage);

        iv_close.setOnClickListener(this);
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

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);


        //设置全局监听
        keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = getWindow().getDecorView().getRootView().getHeight();

                int keyboardHeight = screenHeight - r.bottom;
                boolean isKeyboardOpen = keyboardHeight > screenHeight * 0.15;

                // 根据键盘的显示/隐藏状态进行相应的处理
                if (isKeyboardOpen) {
                    // 键盘显示时的处理逻辑
                    ll_edit.setVisibility(View.VISIBLE);
                    coverView.setVisibility(View.VISIBLE);
                    setEnabled(false);
                } else {
                    // 键盘隐藏时的处理逻辑
                    ll_edit.setVisibility(View.INVISIBLE);
                    coverView.setVisibility(View.INVISIBLE);
                    setEnabled(true);
                }
            }
        };
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        //设置收起键盘的监听器
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                et_edit.setText("");
                if (coverView != null)
                    coverView.setVisibility(View.INVISIBLE);
            }


        });

        //设置EditText的监听器
        ViewUtils.setEditViewLimit(et_edit);
    }

    private void setEnabled(boolean flag) {
        findViewById(R.id.iv_disconnect_homepage).setEnabled(flag);
        findViewById(R.id.tv_select_device_homepage).setEnabled(flag);
        findViewById(R.id.ll_keyboard_homepage).setEnabled(flag);
        findViewById(R.id.ll_channel_homepage).setEnabled(flag);
        findViewById(R.id.iv_up_homepage).setEnabled(flag);
        findViewById(R.id.iv_down_homepage).setEnabled(flag);
        findViewById(R.id.iv_left_homepage).setEnabled(flag);
        findViewById(R.id.iv_right_homepage).setEnabled(flag);
        findViewById(R.id.iv_ok_homepage).setEnabled(flag);
        findViewById(R.id.iv_back_homepage).setEnabled(flag);
        findViewById(R.id.iv_home_homepage).setEnabled(flag);
        findViewById(R.id.iv_rewind_homepage).setEnabled(flag);
        findViewById(R.id.iv_play_pause_homepage).setEnabled(flag);
        findViewById(R.id.iv_forward_homepage).setEnabled(flag);
        findViewById(R.id.iv_backspace_homepage).setEnabled(flag);
        findViewById(R.id.iv_menu_homepage).setEnabled(flag);
        findViewById(R.id.iv_volume_down_homepage).setEnabled(flag);
        findViewById(R.id.iv_volume_mute_homepage).setEnabled(flag);
        findViewById(R.id.iv_volume_up_homepage).setEnabled(flag);

    }

    @Override
    protected void onDestroy() {
        getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (SettingActivity.isVibrator && vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(20L);
        }
        if (RokuLocation == null) {
            DeviceManageHelper helper = new DeviceManageHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(DeviceManageHelper.TABLE_HISTORY, null, null, null, null, null, null);
            if (cursor.getCount() <= 0) {
                Intent intent = new Intent(this, DeviceAdd.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, DeviceManage.class);
                startActivity(intent);
            }
            cursor.close();
            db.close();
            return;
        }
        switch (v.getId()) {
            case R.id.iv_close_homepage:
                finish();
                break;
            case R.id.iv_disconnect_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/PowerOff");
                break;
            case R.id.tv_select_device_homepage:
                Intent intent = new Intent(this, DeviceManage.class);
                startActivity(intent);
                break;
            case R.id.ll_keyboard_homepage:
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                coverView.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_channel_homepage:
                launchChannel();
                break;
            case R.id.iv_up_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Up");
                break;
            case R.id.iv_down_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Down");
                break;
            case R.id.iv_left_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Left");
                break;
            case R.id.iv_right_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Right");
                break;
            case R.id.iv_ok_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Select");
                break;
            case R.id.iv_back_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Back");
                break;
            case R.id.iv_home_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Home");
                break;
            case R.id.iv_rewind_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Rev");
                break;
            case R.id.iv_play_pause_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Play");
                break;
            case R.id.iv_forward_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Fwd");
                break;
            case R.id.iv_backspace_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Backspace");
                break;
            case R.id.iv_menu_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/Info");
                break;
            case R.id.iv_volume_down_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/VolumeDown");
                break;
            case R.id.iv_volume_mute_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/VolumeMute");
                break;
            case R.id.iv_volume_up_homepage:
                RemoteUtils.httpPost(RokuLocationUrl, "keypress/VolumeUp");
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

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
        RokuLocation = FragmentRemoteControl.RokuLocation;
        RokuLocationUrl = FragmentRemoteControl.RokuLocationUrl;
        setConnectionStatus(RokuLocation != null);
        if (RokuLocation != null)
            RokuLocationUrl = RemoteUtils.getRokuLocationUrl(RokuLocation);
        else
            RokuLocationUrl = null;
        Log.d(TAG, "onResume: " + RokuLocationUrl);
        super.onResume();
    }


    private void setConnectionStatus(boolean flag) {
        Log.d(TAG, "setConnectionStatus: ");
        if (flag) {
            String deviceName = FragmentRemoteControl.ConnectingDevice.getUserDeviceName();
            iv_isConnect.setImageResource(R.mipmap.connected_homepage);

            tv_selectDevice.setText(deviceName);

        } else {
            iv_isConnect.setImageResource(R.mipmap.no_connected);
            tv_selectDevice.setText(R.string.Select_device);
        }
    }
}
