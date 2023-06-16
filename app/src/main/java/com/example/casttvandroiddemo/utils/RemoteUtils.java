package com.example.casttvandroiddemo.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteUtils {
    private static final String TAG = "RemoteUtils";

    public static void castToTv(String RokuLocationUrl, String realVideoUrl) throws UnsupportedEncodingException {
        String getUrl = RokuLocationUrl + "input/706370?url=" + URLEncoder.encode(realVideoUrl, "UTF-8") + "&t=v&name=video&format=Default";
        Request request = new Request.Builder()
                .url(getUrl)
                .post(RequestBody.create(MediaType.parse("application/json"), ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    public interface ChannelLaunchCallback {
        void onChannelLaunchResult(boolean isInstall);
    }

    public static void isExistsChannelToCast(String RokuLocationUrl, ChannelLaunchCallback ca) {
        OkHttpClient client = new OkHttpClient();
        String url = RokuLocationUrl + "query/apps";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String channelXml = response.body().string();
                Log.d(TAG, "onResponse: query" + channelXml);
                String[] lines = channelXml.split("\n");
                boolean isInstall = false;
                for (String line : lines) {
                    if (line.startsWith("\t<app id=\"706370\"")) {
                        isInstall = true;
                        break;
                    }
                }
                ca.onChannelLaunchResult(isInstall);
            }
        });
    }

    public static void installChannelToCast() {

    }

    public static String getRokuLocationUrl(String ipAddress) {
        return "http://" + ipAddress + ":8060/";
    }

    public static void httpPost(String RokuLocationUrl, String method) {
        OkHttpClient client = new OkHttpClient();
        String url = RokuLocationUrl + method;
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

}
