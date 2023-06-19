package com.example.casttvandroiddemo;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.casttvandroiddemo.utils.IntentUtils;
import com.example.casttvandroiddemo.utils.OnlineDeviceUtils;

import me.jessyan.autosize.AutoSizeConfig;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";
    Handler handler = new Handler();
    Context context = (Context) this;
    private static final String key = "isAccept";
    private TextView tv_cancelUse;
    private TextView tv_accept;
    private static final long COUNTER_TIME = 3;

    private long secondsRemaining;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
        setBackEvent();
        // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
        createTimer(COUNTER_TIME);
    }
    private void createTimer(long seconds) {

        CountDownTimer countDownTimer =
                new CountDownTimer(seconds * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        Log.d(TAG, "onTick: " + millisUntilFinished);
                        secondsRemaining = ((millisUntilFinished / 1000) + 1);
                    }

                    @Override
                    public void onFinish() {
                        secondsRemaining = 0;

                        Application application = getApplication();

//                        // If the application is not an instance of MyApplication, log an error message and
//                        // start the MainActivity without showing the app open ad.
//                        if (!(application instanceof MyApplication)) {
//                            Log.e(TAG, "Failed to cast application to MyApplication.");
//                            startMainActivity();
//                            return;
//                        }

                        // Show the app open ad.
                        ((MyApplication) application)
                                .showAdIfAvailable(
                                        StartActivity.this,
                                        new MyApplication.OnShowAdCompleteListener() {
                                            @Override
                                            public void onShowAdComplete() {
                                                startMainActivity();
                                                finish();
                                            }
                                        });
                    }
                };
        countDownTimer.start();
    }

    /** Start the MainActivity. */
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
    private void setBackEvent() {
        OnlineDeviceUtils.findDevice();
        SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        SettingActivity.isVibrator = sp.getBoolean("isVibrator", false);
    }

    private void initView() {
        tv_cancelUse = (TextView) findViewById(R.id.tv_cancelUse);
        tv_accept = (TextView) findViewById(R.id.tv_accept);
    }

//    public void showCustomDialog() {
//        //创建自定义弹窗
//        dialog = new Dialog(this);
//        dialog.setContentView(R.layout.dialog_privacy_agreement);
//        dialog.setCancelable(false);
//        dialog.show();
//        tv_cancelUse = (TextView) dialog.findViewById(R.id.tv_cancelUse);
//        tv_accept = (TextView) dialog.findViewById(R.id.tv_accept);
//
//        tv_cancelUse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                closeDialog();
//                finish();
//            }
//        });
//
//        tv_accept.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences sp = getSharedPreferences("agreement", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putString(key, "accepted");
//                editor.apply();
//                IntentUtils.goToActivity(context, MainActivity.class);
//            }
//        });
//    }
//
//    public void closeDialog() {
//        dialog.dismiss();
//    }
//
//    public boolean isAccept() {
//        SharedPreferences sp = getSharedPreferences("agreement", MODE_PRIVATE);
//        String flag = sp.getString(key, "");
//        return !flag.equals("");
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}