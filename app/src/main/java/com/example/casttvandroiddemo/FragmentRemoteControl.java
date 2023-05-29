package com.example.casttvandroiddemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FragmentRemoteControl extends Fragment implements View.OnClickListener{
    private static final String TAG = "FragmentRemoteControl";
    private View view;
    private Button btn_up, btn_down, btn_left, btn_right, btn_enter;
    private Button btn_setting, btn_vip, btn_powerOff, btn_connect, btn_cast;
    private Button btn_keyboard, btn_channel, btn_back, btn_home;
    private Button btn_rewind, btn_pause, btn_forward, btn_refresh;
    private Button btn_detail, btn_volumeDown, btn_volumeUp, btn_volumeMute;

    private String RokuLocation;
    private static final String SSDP_MSEARCH = "M-SEARCH * HTTP/1.1\r\n" +
            "Host: 239.255.255.250:1900\r\n" +
            "Man: \"ssdp:discover\"\r\n" +
            "ST: roku:ecp\r\n\r\n";
    private static final int BUFFER_SIZE = 4096;
    private ProgressDialog progressDialog;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return view = inflater.inflate(R.layout.fragment_remote_control_tab, container, false);
    }
    private void initView() {
        btn_powerOff = (Button) view.findViewById(R.id.btn_keyToPowerOff);
        btn_connect = (Button) view.findViewById(R.id.btn_requestConnection);
        btn_keyboard = (Button) view.findViewById(R.id.btn_keyboard);
        btn_channel = (Button) view.findViewById(R.id.btn_channel);
        btn_up = (Button) view.findViewById(R.id.btn_keyToUp);
        btn_down = (Button) view.findViewById(R.id.btn_keyToDown);
        btn_left = (Button) view.findViewById(R.id.btn_keyToLeft);
        btn_right = (Button) view.findViewById(R.id.btn_keyToRight);
        btn_enter = (Button) view.findViewById(R.id.btn_keyToSelect);
        btn_back = (Button) view.findViewById(R.id.btn_keyToBack);
        btn_home = (Button) view.findViewById(R.id.btn_keyToHome);
        btn_rewind = (Button) view.findViewById(R.id.btn_keyToRewind);
        btn_pause = (Button) view.findViewById(R.id.btn_keyToPause);
        btn_forward = (Button) view.findViewById(R.id.btn_keyToForward);
        btn_refresh = (Button) view.findViewById(R.id.btn_keyToRefresh);
        btn_detail = (Button) view.findViewById(R.id.btn_keyToDetail);
        btn_volumeDown = (Button) view.findViewById(R.id.btn_keyToVolumeDown);
        btn_volumeMute = (Button) view.findViewById(R.id.btn_keyToMute);
        btn_volumeUp = (Button) view.findViewById(R.id.btn_keyToVolumeUp);


        btn_powerOff.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
        btn_keyboard.setOnClickListener(this);
        btn_channel.setOnClickListener(this);
        btn_up.setOnClickListener(this);
        btn_down.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        btn_enter.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        btn_rewind.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_forward.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_detail.setOnClickListener(this);
        btn_volumeDown.setOnClickListener(this);
        btn_volumeMute.setOnClickListener(this);
        btn_volumeUp.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_keyToPowerOff:
                httpPost("keypress/PowerOff");
                break;
            case R.id.btn_requestConnection:
                findDevice();
                break;
            case R.id.btn_keyboard:
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                break;
            case R.id.btn_channel:
                launchChannel();
                break;
            case R.id.btn_keyToUp:
                httpPost("keypress/Up");
                break;
            case R.id.btn_keyToDown:
                httpPost("keypress/Down");
                break;
            case R.id.btn_keyToLeft:
                httpPost("keypress/Left");
                break;
            case R.id.btn_keyToRight:
                httpPost("keypress/Right");
                break;
            case R.id.btn_keyToSelect:
                httpPost("keypress/Select");
                break;
            case R.id.btn_keyToBack:
                httpPost("keypress/Back");
                break;
            case R.id.btn_keyToHome:
                httpPost("keypress/Home");
                break;
            case R.id.btn_keyToRewind:
                httpPost("keypress/Rev");
                break;
            case R.id.btn_keyToPause:
                httpPost("keypress/Play");
                break;
            case R.id.btn_keyToForward:
                httpPost("keypress/Fwd");
                break;
            case R.id.btn_keyToRefresh:
                httpPost("keypress/InstantReplay");
                break;
            case R.id.btn_keyToDetail:
                httpPost("keypress/Info");
                break;
            case R.id.btn_keyToVolumeDown:
                httpPost("keypress/VolumeDown");
                break;
            case R.id.btn_keyToMute:
                httpPost("keypress/VolumeMute");
                break;
            case R.id.btn_keyToVolumeUp:
                httpPost("keypress/VolumeUp");
                break;
        }
    }
    private void launchChannel() {
        OkHttpClient client = new OkHttpClient();
        String url = RokuLocation + "query/apps";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: query:" + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String channelXml = response.body().string();
                Log.d(TAG, "onResponse: query" + channelXml);
                String[] lines = channelXml.split("\n");
                boolean isInstall = false;
                for (String line : lines) {
                    if (line.startsWith("\t<app id=\"698776\"")) {
                        httpPost("launch/698776"); //已经存在该频道，无需安装，直接启动
                        isInstall = true;
                    }
                }
                if (!isInstall)
                    httpPost("install/698776");//未存在该频道，需要安装
            }
        });
    }


    private void httpPost(String method) {
        OkHttpClient client = new OkHttpClient();
        String url = RokuLocation + method;
        RequestBody requestBody = RequestBody.create(MediaType.get("text/plain"), "");
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: " + method + " " + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "onResponse: " + method + " " + response.body().string());
            }
        });
    }
    private void findDevice() {
        final String[] response = {null};
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("正在加载，请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    DatagramSocket socket = new DatagramSocket();
                    socket.setSoTimeout(2000);

                    byte[] requestData = SSDP_MSEARCH.getBytes();
                    InetAddress address = InetAddress.getByName("239.255.255.250");
                    DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, address, 1900);

                    socket.send(requestPacket);

                    byte[] buffer = new byte[BUFFER_SIZE];
                    DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(responsePacket);

                    response[0] = new String(responsePacket.getData(), 0, responsePacket.getLength());
                    //获取Roku的ip地址
                    String[] lines = response[0].split("\n");
                    for (String line : lines) {
                        if (line.startsWith("LOCATION:")) {
                            //提取location字段的值
                            RokuLocation = line.substring(line.indexOf(":") + 1).trim();
                            Log.d(TAG, "findDevice: " + RokuLocation);
                            break;
                        }
                    }
                    socket.close();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }).start();


    }
}
