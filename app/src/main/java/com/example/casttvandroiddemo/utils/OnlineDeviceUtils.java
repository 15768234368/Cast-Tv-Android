package com.example.casttvandroiddemo.utils;

import android.util.Log;

import com.example.casttvandroiddemo.bean.DeviceBean;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OnlineDeviceUtils {
    private static final String TAG = "OnlineDeviceUtils";
    private static final String SSDP_MSEARCH = "M-SEARCH * HTTP/1.1\r\n" +
            "Host: 239.255.255.250:1900\r\n" +
            "Man: \"ssdp:discover\"\r\n" +
            "ST: roku:ecp\r\n\r\n";
    private static final int BUFFER_SIZE = 4096;
    public static List<String> mRokuLocation_onLine = new ArrayList<>();
    public static List<DeviceBean> mDeviceData_onLine = new ArrayList<>();
    private static OnConnectedListener onConnectedListener;
    public interface OnConnectedListener{
        public void autoConnect();
        
        public void disConnect();
    }

    public static void setOnConnectedListener(OnConnectedListener onConnectedListener) {
        OnlineDeviceUtils.onConnectedListener = onConnectedListener;
    }

    public static void findDevice() {
        mRokuLocation_onLine.clear();
        mDeviceData_onLine.clear();
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
                        Log.e(TAG, "Failed to send data: " + e.getMessage());
                        if(onConnectedListener != null)
                            onConnectedListener.disConnect();
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
                        processDeviceResponses(responseList);
                    }
//                    else{
//                        //测试
//                        mRokuLocation_onLine.add("123");
//                        mDeviceData_onLine.add(new DeviceBean("123","123","123","123"));
//                        if(onConnectedListener != null)
//                            onConnectedListener.autoConnect();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void processDeviceResponses(List<String> responses) {
        // 处理每个设备的响应
        for (String response : responses) {
            // 解析响应报文，提取设备信息
            String rokuLocation = extractRokuLocation(response);
            Log.d(TAG, "processDeviceResponses: " + rokuLocation);
            // 处理设备信息，可以将其显示在界面上或进行其他操作
            mRokuLocation_onLine.add(rokuLocation);
        }
        getDeviceInfo(mRokuLocation_onLine);

        //测试检测到多个项目
//        for (int i = 0; i < 5; ++i) {
//            String id = "192.168.121.00" + String.valueOf(i);
//            mDeviceData_onLine.add(new DeviceBean(id, id, id));
//            mRokuLocation_onLine.add(id);
//        }
    }

    public static String extractRokuLocation(String response) {
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (line.startsWith("LOCATION:")) {
                // 提取LOCATION字段的值
                return line.substring(line.indexOf(":") + 1).trim();
            }
        }
        return null;
    }

    public static void getDeviceInfo(List<String> LocationList) {
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
                    if (!matcher.find()) {
                        return ;
                    }
                    ipAddress = matcher.group(0);
                    int i;
                    for (i = 0; i < mDeviceData_onLine.size(); ++i) {
                        if (info[0].equals(mDeviceData_onLine.get(i).getUserDeviceUDN())) {
                            mDeviceData_onLine.get(i).setUserDeviceName(info[1]);
                            mDeviceData_onLine.get(i).setUserDeviceLocation(info[2]);
                            mDeviceData_onLine.get(i).setUserDeviceIpAddress(ipAddress);
                            break;
                        }
                    }
                    if (i >= mDeviceData_onLine.size())
                        mDeviceData_onLine.add(new DeviceBean(info[0], info[1], info[2], ipAddress));
                    if(onConnectedListener != null)
                        onConnectedListener.autoConnect();
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
    public static String[] getInfoFromXML(String body) {
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

}
