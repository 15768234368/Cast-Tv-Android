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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_showRemoteControl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(0);
            }
        });

        findViewById(R.id.btn_showInternet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(1);
            }
        });
        selectTab(0);
//        initView();
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
                break;
            case 1:
                if(fragmentInternet == null) {
                    fragmentInternet = new FragmentInternet();
                    transaction.add(R.id.container, fragmentInternet);
                }else{
                    transaction.show(fragmentInternet);
                }
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if(fragmentRemoteControl != null)
            transaction.hide(fragmentRemoteControl);
        if(fragmentInternet != null)
            transaction.hide(fragmentInternet);
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
