package com.example.casttvandroiddemo;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.casttvandroiddemo.utils.ViewUtils;

import java.io.IOException;

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

    //键盘弹出的变量
    private LinearLayout ll_edit, ll_navigate;
    private EditText et_edit;
    private ImageView iv_edit;
    private int isResume = 0;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        selectTab(0);


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

        ll_navigate = (LinearLayout) findViewById(R.id.ll_bottom);
        ll_edit = (LinearLayout) findViewById(R.id.ll_keyboard_edit_homepage);
        et_edit = (EditText) findViewById(R.id.et_keyboard_edit_homepage);
        iv_edit = (ImageView) findViewById(R.id.iv_keyboard_edit_homepage);
        //设置EditText的监听器
        ViewUtils.setEditViewLimit(et_edit);
        et_edit.addTextChangedListener(new TextWatcher() {
            int index = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();
                if (!newText.equals("") && newText.length() > index) {
                    if (FragmentRemoteControl.RokuLocation != null) {
                        String RokuLocationUrl = getRokuLocationUrl(FragmentRemoteControl.RokuLocation);
                        httpPost(RokuLocationUrl + "keypress/Lit_" + newText.charAt(newText.length() - 1));
                    }

                }
                if (index >= newText.length()) {
                    if (FragmentRemoteControl.RokuLocation != null) {
                        String RokuLocationUrl = getRokuLocationUrl(FragmentRemoteControl.RokuLocation);
                        httpPost(RokuLocationUrl + "keypress/Backspace");
                    }
                }
                index = newText.length();
            }
        });
        //设置全局监听
        keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                int screenHeight = getWindow().getDecorView().getRootView().getHeight();

                int keyboardHeight = screenHeight - r.bottom;
                boolean isKeyboardOpen = keyboardHeight > screenHeight * 0.15;

                // 根据键盘的显示/隐藏状态进行相应的处理
                if (isKeyboardOpen) {
                    if(isResume != 1){
                        et_edit.requestFocus();
                    }
                    isResume = 0;
                    // 键盘显示时的处理逻辑
                    ll_navigate.setVisibility(View.INVISIBLE);
                    ll_edit.setVisibility(View.VISIBLE);
                    setEnabled(false);

                    if (fragmentRemoteControl != null) {
                        fragmentRemoteControl.getView().findViewById(R.id.view_coverBlack80).setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                Log.d(TAG, "onTouch: ");
                                et_edit.clearFocus();
                                return false;
                            }
                        });
                    }
                } else {
                    // 键盘隐藏时的处理逻辑
                    ll_navigate.setVisibility(View.VISIBLE);
                    ll_edit.setVisibility(View.INVISIBLE);
                    setEnabled(true);
                }
            }
        };

        //设置收起键盘的监听器
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                et_edit.setText("");
                if (fragmentRemoteControl != null) {
                    View CoverView = fragmentRemoteControl.getView().findViewById(R.id.view_coverBlack80);
                    if (CoverView != null)
                        CoverView.setVisibility(View.INVISIBLE);
                }

            }
        });
        et_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: " + hasFocus);
                if (!hasFocus) {
                    closeKeyBoard();
                }
            }
        });

    }

    private void selectTab(int containerNum) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragment(transaction);
        switch (containerNum) {
            case 0:
                if (fragmentRemoteControl == null) {
                    fragmentRemoteControl = new FragmentRemoteControl();
                    transaction.add(R.id.container, fragmentRemoteControl);
                } else {
                    transaction.show(fragmentRemoteControl);
                }
                iv_remoteControl.setImageResource(R.mipmap.remote_homepage_selected);
                tv_remoteControl.setTextColor(0xFF0BBD6A);
                getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

                break;


            case 1:
                if (fragmentInternet == null) {
                    fragmentInternet = new FragmentInternet();
                    transaction.add(R.id.container, fragmentInternet);
                } else {
                    transaction.show(fragmentInternet);
                }

                iv_browserView.setImageResource(R.mipmap.browser_homepage_selected);
                tv_browserView.setTextColor(0xFF0BBD6A);
                getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if (fragmentRemoteControl != null)
            transaction.hide(fragmentRemoteControl);
        if (fragmentInternet != null)
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

    private void httpPost(String url) {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.get("text/plain"), "");
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: " + url + " " + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "onResponse: " + url + " " + response.body().string());
            }
        });
    }

    public String getRokuLocationUrl(String ipAddress) {
        return "http://" + ipAddress + ":8060/";
    }

    public ViewTreeObserver.OnGlobalLayoutListener getKeyboardLayoutListener() {
        return keyboardLayoutListener;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String FRAGMENTS_TAG = "android:support:fragments";
        outState.remove(FRAGMENTS_TAG);
    }

    @Override
    protected void onPause() {
        isResume = 1;
        et_edit.clearFocus();
        super.onPause();

    }


    public void closeKeyBoard() {
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();

        int keyboardHeight = screenHeight - r.bottom;
        boolean isKeyboardOpen = keyboardHeight > screenHeight * 0.15;

        // 根据键盘的显示/隐藏状态进行相应的处理
        if (isKeyboardOpen) {
            ll_navigate.setVisibility(View.VISIBLE);
            ll_edit.setVisibility(View.INVISIBLE);
            et_edit.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ll_edit.getWindowToken(), 0);

            if (fragmentRemoteControl != null) {
                View CoverView = fragmentRemoteControl.getView().findViewById(R.id.view_coverBlack80);
                if (CoverView != null)
                    CoverView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setEnabled(boolean flag) {
        if (fragmentRemoteControl != null) {
            fragmentRemoteControl.getView().findViewById(R.id.iv_disconnect_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.tv_select_device_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.ll_keyboard_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.ll_channel_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_up_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_down_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_left_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_right_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_ok_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_back_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_home_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_rewind_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_play_pause_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_forward_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_backspace_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_menu_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_volume_down_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_volume_mute_homepage).setEnabled(flag);
            fragmentRemoteControl.getView().findViewById(R.id.iv_volume_up_homepage).setEnabled(flag);
        }

    }

}
