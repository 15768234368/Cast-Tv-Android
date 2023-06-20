package com.example.casttvandroiddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.example.casttvandroiddemo.utils.AdInsert;
import com.example.casttvandroiddemo.utils.OnlineDeviceUtils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.umeng.analytics.MobclickAgent;

public class EnableClosedCaptioningActivity extends AppCompatActivity {
    private static final String TAG = "EnableClosedCaptioning";
    private InterstitialAd mInterstitialAd;
    private ImageView iv_back;
    private VideoView videoView;
    public static AdInsert adInsert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_closed_captioning);
        initView();
        adInsert = new AdInsert(this, this);
        adInsert.initAd();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back_setting);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getApplicationContext(), "关闭");
                if (!adInsert.isEmpty()) {
                    Log.d(TAG, "onClick: is not empty");
                    adInsert.showAd();
                } else {
                    finish();
                }
            }
        });
        videoView = findViewById(R.id.video_closed_captioning_setting);
        int resId = getResources().getIdentifier("subtitular", "raw", getPackageName());
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
        videoView.setVideoURI(videoUri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onBackPressed() {
        MobclickAgent.onEvent(getApplicationContext(), "关闭");
        if (!adInsert.isEmpty()) {
            Log.d(TAG, "onClick: is not empty");
            adInsert.showAd();
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        OnlineDeviceUtils.saveLatestOnLineDevice(this, FragmentRemoteControl.ConnectingDevice);
    }
}