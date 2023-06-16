package com.example.casttvandroiddemo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.casttvandroiddemo.utils.IntentUtils;
import com.example.casttvandroiddemo.utils.OnlineDeviceUtils;

import me.jessyan.autosize.AutoSizeConfig;

public class StartActivity extends AppCompatActivity {
    Handler handler = new Handler();
    Context context = (Context) this;
    private static final String key = "isAccept";
    private TextView tv_cancelUse;
    private TextView tv_accept;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isAccept()) {
                IntentUtils.goToActivity(context, MainActivity.class);

            } else {
                showCustomDialog();
            }
        }
    };
    private Dialog dialog;

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
        handler.postDelayed(runnable, 1000);
        setBackEvent();
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

    public void showCustomDialog() {
        //创建自定义弹窗
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_privacy_agreement);
        dialog.setCancelable(false);
        dialog.show();
        tv_cancelUse = (TextView) dialog.findViewById(R.id.tv_cancelUse);
        tv_accept = (TextView) dialog.findViewById(R.id.tv_accept);

        tv_cancelUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
                finish();
            }
        });

        tv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("agreement", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(key, "accepted");
                editor.apply();
                IntentUtils.goToActivity(context, MainActivity.class);
            }
        });
    }

    public void closeDialog() {
        dialog.dismiss();
    }

    public boolean isAccept() {
        SharedPreferences sp = getSharedPreferences("agreement", MODE_PRIVATE);
        String flag = sp.getString(key, "");
        return !flag.equals("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}