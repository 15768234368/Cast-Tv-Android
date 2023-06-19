package com.example.casttvandroiddemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.casttvandroiddemo.utils.AppManage;
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

import me.jessyan.autosize.AutoSizeConfig;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private InterstitialAd mInterstitialAd;
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

        showAd();
    }

    private void showAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                                finish();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
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
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(SettingActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
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

    @Override
    public void onBackPressed() {
        onClick(iv_back);
    }
}