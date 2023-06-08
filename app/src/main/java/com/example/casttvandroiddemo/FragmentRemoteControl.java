package com.example.casttvandroiddemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.casttvandroiddemo.utils.RemoteUtils;

import java.io.IOException;

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
    private ImageView iv_up, iv_down, iv_left, iv_right, iv_enter;
    private ImageView iv_setting, iv_disconnect, iv_cast, iv_isConnect;
    private ImageView iv_back, iv_home;
    private LinearLayout ll_keyboard, ll_channel;
    private ImageView iv_rewind, iv_pause, iv_forward, iv_refresh;
    private ImageView iv_menu, iv_volumeDown, iv_volumeUp, iv_volumeMute;
    private TextView tv_selectDevice;
    public static String RokuLocation = null;
    public static String RokuLocationUrl = RemoteUtils.getRokuLocationUrl(RokuLocation);


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 添加布局监听器
        getActivity().getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(((MainActivity) getActivity()).getKeyboardLayoutListener());
        return view = inflater.inflate(R.layout.fragment_remote_control_tab, container, false);
    }

    private void initView() {
        iv_setting = view.findViewById(R.id.iv_setting_homepage);
        iv_isConnect = view.findViewById(R.id.iv_isConnected);
        iv_disconnect = view.findViewById(R.id.iv_disconnect_homepage);
        tv_selectDevice = view.findViewById(R.id.tv_select_device_homepage);
        iv_cast = view.findViewById(R.id.iv_cast_homepage);
        ll_keyboard = view.findViewById(R.id.ll_keyboard_homepage);
        ll_channel = view.findViewById(R.id.ll_channel_homepage);
        iv_up = view.findViewById(R.id.iv_up_homepage);
        iv_down = view.findViewById(R.id.iv_down_homepage);
        iv_left = view.findViewById(R.id.iv_left_homepage);
        iv_right = view.findViewById(R.id.iv_right_homepage);
        iv_enter = view.findViewById(R.id.iv_ok_homepage);
        iv_back = view.findViewById(R.id.iv_back_homepage);
        iv_home = view.findViewById(R.id.iv_home_homepage);
        iv_rewind = view.findViewById(R.id.iv_rewind_homepage);
        iv_pause = view.findViewById(R.id.iv_play_pause_homepage);
        iv_forward = view.findViewById(R.id.iv_forward_homepage);
        iv_refresh = view.findViewById(R.id.iv_backspace_homepage);
        iv_menu = view.findViewById(R.id.iv_menu_homepage);
        iv_volumeDown = view.findViewById(R.id.iv_volume_down_homepage);
        iv_volumeMute = view.findViewById(R.id.iv_volume_mute_homepage);
        iv_volumeUp = view.findViewById(R.id.iv_volume_up_homepage);

        iv_setting.setOnClickListener(this);
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_setting_homepage:
                Intent intent_setting = new Intent(getContext(), SettingActivity.class);
                startActivity(intent_setting);
                break;
            case R.id.iv_disconnect_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/PowerOff");
                break;
            case R.id.tv_select_device_homepage:
//                findDevice();
                Intent intent = new Intent(getContext(), DeviceManage.class);
                startActivity(intent);
                break;
            case R.id.ll_keyboard_homepage:
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;
            case R.id.ll_channel_homepage:
                launchChannel();
                break;
            case R.id.iv_up_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Up");
                break;
            case R.id.iv_down_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Down");
                break;
            case R.id.iv_left_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Left");
                break;
            case R.id.iv_right_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Right");
                break;
            case R.id.iv_ok_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Select");
                break;
            case R.id.iv_back_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Back");
                break;
            case R.id.iv_home_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Home");
                break;
            case R.id.iv_rewind_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Rev");
                break;
            case R.id.iv_play_pause_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Play");
                break;
            case R.id.iv_forward_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Fwd");
                break;
            case R.id.iv_backspace_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Backspace");
                break;
            case R.id.iv_menu_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/Info");
                break;
            case R.id.iv_volume_down_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/VolumeDown");
                break;
            case R.id.iv_volume_mute_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/VolumeMute");
                break;
            case R.id.iv_volume_up_homepage:
                RemoteUtils.httpPost(RokuLocationUrl,"keypress/VolumeUp");
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
                    if (line.startsWith("\t<app id=\"698776\"")) {
                        RemoteUtils.httpPost(RokuLocationUrl,"launch/698776"); //已经存在该频道，无需安装，直接启动
                        isInstall = true;
                    }
                }
                if (!isInstall)
                    RemoteUtils.httpPost(RokuLocationUrl,"install/698776");//未存在该频道，需要安装
            }
        });
    }




    @Override
    public void onResume() {
        setConnectionStatus(RokuLocation != null);
        if (RokuLocation != null)
            RokuLocationUrl = RemoteUtils.getRokuLocationUrl(RokuLocation);
        else
            RokuLocationUrl = null;

        super.onResume();
    }

    private void setControlEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
    }

    private void setControlsClickable(boolean clickable) {
        setControlEnabled(iv_disconnect, clickable);
        setControlEnabled(iv_cast, clickable);
        setControlEnabled(ll_channel, clickable);
        setControlEnabled(iv_up, clickable);
        setControlEnabled(iv_down, clickable);
        setControlEnabled(iv_left, clickable);
        setControlEnabled(iv_right, clickable);
        setControlEnabled(iv_enter, clickable);
        setControlEnabled(iv_back, clickable);
        setControlEnabled(iv_home, clickable);
        setControlEnabled(iv_rewind, clickable);
        setControlEnabled(iv_pause, clickable);
        setControlEnabled(iv_forward, clickable);
        setControlEnabled(iv_refresh, clickable);
        setControlEnabled(iv_menu, clickable);
        setControlEnabled(iv_volumeDown, clickable);
        setControlEnabled(iv_volumeMute, clickable);
        setControlEnabled(iv_volumeUp, clickable);
    }

    private void setConnectionStatus(boolean flag) {
        if (flag) {
            iv_isConnect.setImageResource(R.mipmap.connected_homepage);
            tv_selectDevice.setText("Streaming");
            setControlsClickable(true);
        } else {
            iv_isConnect.setImageResource(R.mipmap.no_connected);
            tv_selectDevice.setText("选择连接设备");
            setControlsClickable(false);
        }
    }
}
