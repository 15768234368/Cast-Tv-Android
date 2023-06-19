package com.example.casttvandroiddemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

import me.jessyan.autosize.AutoSizeConfig;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SettingActivity";
    private Switch aSwitch;
    private RelativeLayout rl_enableClosedCaptioning, rl_feedback, rl_sharing, rl_userComment, rl_privacyPolicy, rl_userPolicy;
    private ImageView iv_back;
    public static boolean isVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        aSwitch = (Switch) findViewById(R.id.sw_buttonVibration_setting);
        rl_enableClosedCaptioning = (RelativeLayout) findViewById(R.id.rl_enableClosedCaptioning_setting);
        rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback_setting);
        rl_sharing = (RelativeLayout) findViewById(R.id.rl_sharing_setting);
        rl_userComment = (RelativeLayout) findViewById(R.id.rl_userComment_setting);
        rl_privacyPolicy = (RelativeLayout) findViewById(R.id.rl_privacyPolicy_setting);
        rl_userPolicy = (RelativeLayout) findViewById(R.id.rl_userPolicy_setting);

        iv_back = (ImageView) findViewById(R.id.iv_back_setting);

        aSwitch.setOnClickListener(this);
        rl_enableClosedCaptioning.setOnClickListener(this);
        rl_feedback.setOnClickListener(this);
        rl_sharing.setOnClickListener(this);
        rl_userComment.setOnClickListener(this);
        rl_privacyPolicy.setOnClickListener(this);
        rl_userPolicy.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        aSwitch.setChecked(isVibrator);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sw_buttonVibration_setting:
                isVibrator = aSwitch.isChecked();
                SharedPreferences sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isVibrator", isVibrator);
                editor.apply();
                if(isVibrator)
                    MobclickAgent.onEvent(getApplicationContext(), "按钮震动开");
                else
                    MobclickAgent.onEvent(getApplicationContext(), "按钮震动关");
                break;
            case R.id.rl_enableClosedCaptioning_setting:
                MobclickAgent.onEvent(getApplicationContext(), "开启隐藏字幕教程");
                Intent intent_ecc = new Intent(this, EnableClosedCaptioningActivity.class);
                startActivity(intent_ecc);
                break;
            case R.id.iv_back_setting:
                MobclickAgent.onEvent(getApplicationContext(), "关闭");
                finish();
                break;
            case R.id.rl_feedback_setting:
                MobclickAgent.onEvent(getApplicationContext(), "意见反馈");
                Intent intent_feedback = new Intent(Intent.ACTION_SEND);
                intent_feedback.setType("text/plain");
                intent_feedback.putExtra(Intent.EXTRA_EMAIL, new String[]{"WebCasterTV@outlook.com"});
                startActivity(Intent.createChooser(intent_feedback, getString(R.string.Select_Mail_Client)));
                break;
            case R.id.rl_sharing_setting:
                MobclickAgent.onEvent(getApplicationContext(), "分享给好友");
                //输入App的安装网址
                String sharingUrl = "";
                Intent intent_sharing = new Intent();
                intent_sharing.setAction(Intent.ACTION_SEND);
                intent_sharing.putExtra(Intent.EXTRA_TEXT, sharingUrl);
                intent_sharing.setType("text/plain");
                startActivity(Intent.createChooser(intent_sharing, null));
                break;
            case R.id.rl_userComment_setting:
                MobclickAgent.onEvent(getApplicationContext(), "评价我们");
                //输入评论的网址
                try {
                    String commentUrl = "https://www.baidu.com";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(commentUrl)));
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                break;
            case R.id.rl_privacyPolicy_setting:
                MobclickAgent.onEvent(getApplicationContext(), "隐私协议");
                try {
                    String privacyPolicyUrl = "https://webcastertv.github.io/AndWebCaster/PrivacyPolicy/index.html";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl)));
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                break;
            case R.id.rl_userPolicy_setting:
                MobclickAgent.onEvent(getApplicationContext(), "用户协议");
                try {
                    String userPolicyUrl = "https://webcastertv.github.io/AndWebCaster/UserAgreement/index.html";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userPolicyUrl)));
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                break;
        }
    }
}