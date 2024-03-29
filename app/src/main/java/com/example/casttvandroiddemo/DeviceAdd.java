package com.example.casttvandroiddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.casttvandroiddemo.adapter.AddDeviceListAdapter;
import com.example.casttvandroiddemo.bean.DeviceBean;
import com.example.casttvandroiddemo.helper.DeviceManageHelper;
import com.example.casttvandroiddemo.utils.AppManage;
import com.example.casttvandroiddemo.utils.OnlineDeviceUtils;
import com.example.casttvandroiddemo.utils.RemoteUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeviceAdd extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DeviceAdd";
    private static final String SSDP_MSEARCH = "M-SEARCH * HTTP/1.1\r\n" +
            "Host: 239.255.255.250:1900\r\n" +
            "Man: \"ssdp:discover\"\r\n" +
            "ST: roku:ecp\r\n\r\n";
    private static final int BUFFER_SIZE = 4096;
    private ImageView iv_searchImage, iv_back, iv_refresh;
    private Timer timer;
    private Button btn_research, btn_goToSetting;
    private TextView tv_ipTitle1, tv_ipTitle2;
    private List<String> mRokuLocation = new ArrayList<>(OnlineDeviceUtils.mRokuLocation_onLine);
    private List<DeviceBean> mDeviceData = new ArrayList<>(OnlineDeviceUtils.mDeviceData_onLine);
    private RecyclerView recyclerView;
    private AddDeviceListAdapter adapter;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            //if what == 0 则为计时器
            //if what == 1 为搜索到了，转入搜索到了的设备列表
            if (message.what == 0) {
                int second = (int) message.obj;
                //设定为8秒，搜索8秒之后，仍未搜索到，则显示"未检测到Roku",  因为500毫秒显示一张，所以这里定义为10
                if (second < 16) {
                    Log.d(TAG, "second: " + second);
                    int index = second % 4;
                    switch (index) {
                        case 0:
                            iv_searchImage.setImageResource(R.mipmap.search1);
                            break;
                        case 1:
                            iv_searchImage.setImageResource(R.mipmap.search2);
                            break;
                        case 2:
                            iv_searchImage.setImageResource(R.mipmap.search3);
                            break;
                        case 3:
                            iv_searchImage.setImageResource(R.mipmap.search4);
                            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                            if (networkInfo != null && networkInfo.isConnected()) {
                                // 有网络连接
                                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                                    // 当前连接为 Wi-Fi 或 移动数据网络
                                    // 执行相关操作
                                }
                            } else {
                                // 无网络连接
                                // 执行相关操作
                                setNoNetWorkConnecting();
                                timer.cancel();
                            }

                            break;
                    }

                } else {
                    Log.d(TAG, "handleMessage: " + "time is overed");
                    timer.cancel();
                    setUndetected();
                }
            } else if (message.what == 1) {
                if (btn_research.getVisibility() == View.VISIBLE)
                    btn_research.setVisibility(View.INVISIBLE);
                iv_refresh.setVisibility(View.VISIBLE);
                //展示搜索到了的列表
                Log.d(TAG, "handleMessage: " + "message is 1");
                showScannedDeviceList();
                if (dialog != null)
                    dialog.cancel();
            }
            return false;
        }
    });
    private Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_add);
        mDeviceData.clear();
        mRokuLocation.clear();
        initView();
        searchDevice();
        AppManage.Admob_bannerInit(this, findViewById(R.id.AdView));
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back_deviceManage);
        iv_searchImage = (ImageView) findViewById(R.id.iv_searchImage);
        iv_refresh = (ImageView) findViewById(R.id.iv_refresh_deviceManage);
        btn_research = (Button) findViewById(R.id.btn_research_deviceManage);
        btn_goToSetting = (Button) findViewById(R.id.btn_goToSetting_deviceManage);
        tv_ipTitle1 = (TextView) findViewById(R.id.tv_ipTitle1);
        tv_ipTitle2 = (TextView) findViewById(R.id.tv_ipTitle2);
        recyclerView = (RecyclerView) findViewById(R.id.rv_addItemList);
        iv_back.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
        btn_research.setOnClickListener(this);
        btn_goToSetting.setOnClickListener(this);
        adapter = new AddDeviceListAdapter(this, mDeviceData);
    }

    private void searchDevice() {
        iv_searchImage.setImageResource(R.mipmap.search1);
        tv_ipTitle1.setText(R.string.Automatically_search_the_device___);
        tv_ipTitle2.setText(R.string.make_sure_the_android_device_is_on_the_same_wi_fi_as_roku);
        btn_research.setVisibility(View.INVISIBLE);
        btn_goToSetting.setVisibility(View.INVISIBLE);
        findDevice();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                msg.obj = i++;
                handler.sendMessage(msg);

            }
        }, 0, 500);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_research_deviceManage:
                searchDevice();
                break;
            case R.id.iv_back_deviceManage:
                finish();
                break;
            case R.id.btn_goToSetting_deviceManage:
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                break;
            case R.id.iv_refresh_deviceManage:
                refreshPage();
                break;
        }
    }

    public void refreshPage() {
        mDeviceData.clear();
        mRokuLocation.clear();
        showRefreshDialog();
        searchDevice();
    }

    public void setUndetected() {
        iv_searchImage.setVisibility(View.VISIBLE);
        iv_searchImage.setImageResource(R.mipmap.no_device_detected);
        recyclerView.setVisibility(View.INVISIBLE);
        tv_ipTitle1.setVisibility(View.VISIBLE);
        tv_ipTitle2.setVisibility(View.VISIBLE);
        tv_ipTitle1.setText(R.string.No_Roku_detected);
        tv_ipTitle2.setText(R.string.Please_check_the_Android_device_is_on_the_same_WiFi_as_Roku);
        btn_research.setVisibility(View.VISIBLE);
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
    }

    public void setNoNetWorkConnecting() {
        iv_searchImage.setVisibility(View.VISIBLE);
        iv_searchImage.setImageResource(R.mipmap.no_network_device_manage);
        recyclerView.setVisibility(View.INVISIBLE);
        tv_ipTitle1.setVisibility(View.VISIBLE);
        tv_ipTitle2.setVisibility(View.VISIBLE);
        tv_ipTitle1.setText(R.string.Please_allow_network_permissions_and_connect_to_WiFi);
        tv_ipTitle2.setText(R.string.otherwise_you_will_not_be_able_to_connect_to_Roku_devices);
        btn_goToSetting.setVisibility(View.VISIBLE);
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
    }

    public void showScannedDeviceList() {
        Log.d(TAG, "showScannedDeviceList: " + mDeviceData.size());
        timer.cancel();
        //将其他控件销毁
        iv_searchImage.setVisibility(View.INVISIBLE);
        tv_ipTitle1.setVisibility(View.INVISIBLE);
        tv_ipTitle2.setVisibility(View.INVISIBLE);
        btn_research.setVisibility(View.INVISIBLE);
        btn_goToSetting.setVisibility(View.INVISIBLE);
        //展示已搜索到设备列表
        recyclerView.setVisibility(View.VISIBLE);
        iv_refresh.setVisibility(View.VISIBLE);
        adapter.setOnItemClickListener(new AddDeviceListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                //选中某个设备，并存入历史连接数据库

                FragmentRemoteControl.RokuLocation = mDeviceData.get(position).getUserDeviceIpAddress();
                FragmentRemoteControl.RokuLocationUrl = RemoteUtils.getRokuLocationUrl(FragmentRemoteControl.RokuLocation);
                try {
                    FragmentRemoteControl.ConnectingDevice = (DeviceBean) mDeviceData.get(position).clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                Log.d(TAG, "OnItemClick: " + mDeviceData.get(position).getUserDeviceIpAddress());
                Log.d(TAG, "OnItemClick: " + mRokuLocation.get(position));
                DeviceManageHelper helper = new DeviceManageHelper(getApplicationContext());

                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                DeviceBean bean = mDeviceData.get(position);
                values.put(DeviceManageHelper.USER_DEVICE_UDN, bean.getUserDeviceUDN());
                values.put(DeviceManageHelper.USER_DEVICE_NAME, bean.getUserDeviceName());
                values.put(DeviceManageHelper.USER_DEVICE_LOCATION, bean.getUserDeviceLocation());
                values.put(DeviceManageHelper.USER_DEVICE_IPADDRESS, bean.getUserDeviceIpAddress());

                long rowId = db.insert(DeviceManageHelper.TABLE_HISTORY, null, values);
                if (rowId == -1) {
                    // 主键冲突异常捕获
                    // 进行更新操作
                    values.remove(DeviceManageHelper.USER_DEVICE_UDN); // 移除主键值
                    String whereClause = DeviceManageHelper.USER_DEVICE_UDN + "=?";
                    String[] whereArgs = new String[]{bean.getUserDeviceUDN()};
                    db.update(DeviceManageHelper.TABLE_HISTORY, values, whereClause, whereArgs);
                    Log.d(TAG, "OnItemClick: " + bean.getUserDeviceUDN());
                }

                db.close();
                finish();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void showConnectDialog() {
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_connecting);
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

    public void showRefreshDialog() {
        iv_refresh.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        adapter.notifyDataSetChanged();
        if (recyclerView != null && mDeviceData.size() > 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }

    public synchronized void findDevice() {
        final List<String> responseList = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    DatagramSocket socket = new DatagramSocket();
                    socket.setSoTimeout(2000);

                    byte[] requestData = SSDP_MSEARCH.getBytes();
                    InetAddress address = InetAddress.getByName("239.255.255.250");
                    DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, address, 1900);

                    try{
                        socket.send(requestPacket);
                    }catch (IOException e){
                        Log.d(TAG, "run: net work is not connect");
                        FragmentRemoteControl.ConnectingDevice = null;
                        FragmentRemoteControl.RokuLocationUrl = null;
                        FragmentRemoteControl.RokuLocation = null;
                        if(OnlineDeviceUtils.onConnectedListener != null)
                            OnlineDeviceUtils.onConnectedListener.disConnect();
                        return;
                    }

                    while (true) {
                        byte[] buffer = new byte[BUFFER_SIZE];
                        DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                        try {
                            socket.receive(responsePacket);
                            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                            responseList.add(response);
                        } catch (SocketTimeoutException e) {
                            // 超时，停止接收响应
                            break;
                        }
                    }
                    socket.close();
                    if (responseList.size() > 0) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                processDeviceResponses(responseList);
                            }
                        });
                    } else {
                        timer.cancel();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setUndetected();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void processDeviceResponses(List<String> responses) {
        // 处理每个设备的响应
        for (String response : responses) {
            // 解析响应报文，提取设备信息
            String rokuLocation = OnlineDeviceUtils.extractRokuLocation(response);
            // 处理设备信息，可以将其显示在界面上或进行其他操作
            mRokuLocation.add(rokuLocation);
        }
        getDeviceInfo(mRokuLocation);
        //测试检测到多个项目
//        for (int i = 0; i < 5; ++i) {
//            String id = "192.168.121.00" + String.valueOf(i);
//            mDeviceData.add(new DeviceBean(id, id, id));
//            mRokuLocation.add(id);
//        }
//        Message message = new Message();
//        message.what = 1;
//        handler.sendMessageDelayed(message, 3000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OnlineDeviceUtils.mDeviceData_onLine = mDeviceData;
        OnlineDeviceUtils.mRokuLocation_onLine = mRokuLocation;
    }

    public void getDeviceInfo(List<String> LocationList) {
        for (String item : LocationList) {
            String url = item + "query/device-info";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    String[] info = getInfoFromXML(responseBody);
                    String ipAddress = null;
                    //使用正则表达式提取出ip地址
                    Pattern pattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
                    Matcher matcher = pattern.matcher(url);
                    if (matcher.find()) {
                        ipAddress = matcher.group(0);
                    }
                    int i;
                    for (i = 0; i < mDeviceData.size(); ++i) {
                        if (info[0].equals(mDeviceData.get(i).getUserDeviceUDN())) {
                            mDeviceData.get(i).setUserDeviceName(info[1]);
                            mDeviceData.get(i).setUserDeviceLocation(info[2]);
                            mDeviceData.get(i).setUserDeviceIpAddress(ipAddress);
                            break;
                        }
                    }
                    if (i >= mDeviceData.size()) {
                        mDeviceData.add(new DeviceBean(info[0], info[1], info[2], ipAddress));
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessageDelayed(message, 3000);
                    }
                }
            });
        }
    }

    /**
     * 从XML文件中解析出deviceName和deviceLocation
     * 0-UDN 1-Name 2-Location
     *
     * @param body XML的String形式
     * @return deviceName和deviceLocation
     */
    public String[] getInfoFromXML(String body) {
        String deviceUDN = null;
        String deviceName = null;
        String deviceLocation = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(body)));

            //获取根元素
            Element root = document.getDocumentElement();
            //获取<udn> 元素
            NodeList udnNodeList = root.getElementsByTagName("udn");
            if (udnNodeList.getLength() > 0) {
                Element udnElement = (Element) udnNodeList.item(0);
                deviceUDN = udnElement.getTextContent();
            }
            //获取<user-device-name> 元素
            NodeList nameNodeList = root.getElementsByTagName("user-device-name");
            if (nameNodeList.getLength() > 0) {
                Element nameElement = (Element) nameNodeList.item(0);
                deviceName = nameElement.getTextContent();
                // 在这里使用 deviceName 做进一步的操作
            }

            // 获取 <user-device-location> 元素
            NodeList locationNodeList = root.getElementsByTagName("user-device-location");
            if (locationNodeList.getLength() > 0) {
                Element locationElement = (Element) locationNodeList.item(0);
                deviceLocation = locationElement.getTextContent();
                // 在这里使用 deviceLocation 做进一步的操作
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{deviceUDN, deviceName, deviceLocation};
    }

    @Override
    protected void onPause() {
        super.onPause();
        OnlineDeviceUtils.saveLatestOnLineDevice(this, FragmentRemoteControl.ConnectingDevice);
    }
}