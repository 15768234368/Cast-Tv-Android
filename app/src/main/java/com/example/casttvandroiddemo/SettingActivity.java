package com.example.casttvandroiddemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SettingActivity";
    private Switch aSwitch;
    private RelativeLayout rl_enableClosedCaptioning, rl_feedback, rl_sharing, rl_userComment, rl_privacyPolicy, rl_userPolicy;
    private ImageView iv_back;

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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sw_buttonVibration_setting:
                getApplicationContext();
//                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//                vibrator.vibrate(200);
                break;
            case R.id.rl_enableClosedCaptioning_setting:
                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(1000);
                Intent intent_ecc = new Intent(this, EnableClosedCaptioningActivity.class);
                startActivity(intent_ecc);
                break;
            case R.id.iv_back_setting:
                finish();
                break;
            case R.id.rl_feedback_setting:
                Intent intent_feedback = new Intent(Intent.ACTION_SEND);
                intent_feedback.setType("text/plain");
                startActivity(Intent.createChooser(intent_feedback, "选择邮件客户端"));
                break;
            case R.id.rl_sharing_setting:
                //输入App的安装网址
                String sharingUrl = "123";
                Intent intent_sharing = new Intent();
                intent_sharing.setAction(Intent.ACTION_SEND);
                intent_sharing.putExtra(Intent.EXTRA_TEXT, sharingUrl);
                intent_sharing.setType("text/plain");
                startActivity(Intent.createChooser(intent_sharing, null));
                break;
            case R.id.rl_userComment_setting:
                try {
                    String commentUrl = "";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(commentUrl)));
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                break;
            case R.id.rl_privacyPolicy_setting:
                try {
                    String privacyPolicyUrl = "https://androidcasttv.github.io/AndCastTV/PrivacyPolicy/zh.html";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl)));
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                break;
            case R.id.rl_userPolicy_setting:
                try {
                    String userPolicyUrl = "https://androidcasttv.github.io/AndCastTV/UserAgreement/zh.html";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(userPolicyUrl)));
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                break;
        }
    }
}