package com.example.casttvandroiddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import me.jessyan.autosize.internal.CustomAdapt;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements CustomAdapt {
    private static final String TAG = "MainActivity";

    private FragmentRemoteControl fragmentRemoteControl;
    private FragmentInternet fragmentInternet;
    private ImageView iv_remoteControl, iv_browserView;
    private TextView tv_remoteControl, tv_browserView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        selectTab(0);
//        initView();
    }

    private void initView() {
        iv_remoteControl = findViewById(R.id.iv_remote_homepage);
        iv_remoteControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(0);
            }
        });

        iv_browserView = findViewById(R.id.iv_browser_homepage);
        iv_browserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(1);
            }
        });

        tv_remoteControl = findViewById(R.id.tv_remote_homepage);
        tv_browserView = findViewById(R.id.tv_browser_homepage);
    }

    private void selectTab(int containerNum){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragment(transaction);
        switch (containerNum){
            case 0:
                if(fragmentRemoteControl == null){
                    fragmentRemoteControl = new FragmentRemoteControl();
                    transaction.add(R.id.container, fragmentRemoteControl);
                }else{
                    transaction.show(fragmentRemoteControl);
                }
                iv_remoteControl.setImageResource(R.mipmap.remote_homepage_selected);
                tv_remoteControl.setTextColor(0xFF0BBD6A);
                break;


            case 1:
                if(fragmentInternet == null) {
                    fragmentInternet = new FragmentInternet();
                    transaction.add(R.id.container, fragmentInternet);
                }else{
                    transaction.show(fragmentInternet);
                }
                iv_browserView.setImageResource(R.mipmap.browser_homepage_selected);
                tv_browserView.setTextColor(0xFF0BBD6A);
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if(fragmentRemoteControl != null)
            transaction.hide(fragmentRemoteControl);
        if(fragmentInternet != null)
            transaction.hide(fragmentInternet);
        iv_remoteControl.setImageResource(R.mipmap.remote_homepage_unselected);
        iv_browserView.setImageResource(R.mipmap.browser_homepage_unselected);
        tv_remoteControl.setTextColor(0XFF666666);
        tv_browserView.setTextColor(0XFF666666);
    }







    @Override
    public boolean isBaseOnWidth() {
        return true;
    }

    @Override
    public float getSizeInDp() {
        return 0;
    }

}
