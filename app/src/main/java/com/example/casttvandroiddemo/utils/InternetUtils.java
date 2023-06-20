package com.example.casttvandroiddemo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.casttvandroiddemo.R;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

public class InternetUtils {
    private static final String TAG = "InternetUtils";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public static final String JSON_FILENAME = "comment.json";
    public static final String JSON_URL = "https://webcastertv.github.io/comment.json";

    public static long getCommentSpan(Context context) {
        try {
            FileInputStream fis = context.openFileInput(JSON_FILENAME);
            String jsonString = new Scanner(fis).useDelimiter("\\A").next();
            fis.close();

            JSONObject jsonObject = new JSONObject(jsonString);
            long userOpenAppCount = jsonObject.getLong("userOpenAppCount");

            return userOpenAppCount;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static void downloadCommentSpanJsonFile(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(JSON_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    Log.d(TAG, "run: " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 将下载的 JSON 数据写入应用程序的内部存储中
                        FileOutputStream fos = context.openFileOutput(JSON_FILENAME, Context.MODE_PRIVATE);
                        InputStream inputStream = connection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            Log.d(TAG, "run: success");
                        }
                        fos.close();
                        inputStream.close();
                    }
                } catch (Exception e) {
                    Log.d(TAG, "run: error");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*** 跳转google play*/
    public static void openGooglePlay(Context context) {
        String playPackage = "com.example.casttvandroiddemo";
        try {
            String currentPackageName = context.getPackageName();
            if (currentPackageName != null) {
                Uri currentPackageUri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, currentPackageUri);
                intent.setPackage(playPackage);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Uri currentPackageUri = Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, currentPackageUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}