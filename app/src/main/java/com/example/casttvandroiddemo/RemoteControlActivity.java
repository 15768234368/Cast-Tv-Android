package com.example.casttvandroiddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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

import com.example.casttvandroiddemo.bean.DeviceBean;
import com.example.casttvandroiddemo.helper.DeviceManageHelper;
import com.example.casttvandroiddemo.utils.OnlineDeviceUtils;
import com.example.casttvandroiddemo.utils.RemoteUtils;
import com.example.casttvandroiddemo.utils.ViewUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RemoteControlActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RemoteControlActivity";
    private ImageView iv_up, iv_down, iv_left, iv_right, iv_enter;
    private ImageView iv_close, iv_disconnect, iv_isConnect;
    private ImageView iv_back, iv_home;
    private LinearLayout ll_keyboard, ll_channel;
    private ImageView iv_rewind, iv_pause, iv_forward, iv_refresh;
    private ImageView iv_menu, iv_volumeDown, iv_volumeUp, iv_volumeMute;
    private TextView tv_selectDevice;
    private Vibrator vibrator;
    private View coverView;
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener;


    private View view_disconnect_coverBlack10, view_keyboard_coverBlack10, view_channel_coverBlack10;
    private View view_back_coverBlack10, view_home_coverBlack10, view_rewind_coverBlack10, view_play_pause_coverBlack10;
    private View view_forward_coverBlack10, view_backspace_coverBlack10, view_menu_coverBlack10, view_volume_down_coverBlack10;
    private View view_volume_mute_coverBlack10, view_volume_up_coverBlack10;
    private View view_ok_coverBlack10;
    private boolean keypress_board = false;
    //键盘弹出的变量
    private LinearLayout ll_edit;
    private EditText et_edit;
    private ImageView iv_edit;
    private int isResume = 0;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                //更新主线程
                setConnectionStatus(FragmentRemoteControl.RokuLocation != null);
            }
            return false;

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Remote Control Activity onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        view_disconnect_coverBlack10 = findViewById(R.id.view_disconnect_coverBlack10);
        view_keyboard_coverBlack10 = findViewById(R.id.view_keyboard_coverBlack10);
        view_channel_coverBlack10 = findViewById(R.id.view_channel_coverBlack10);
        view_back_coverBlack10 = findViewById(R.id.view_back_coverBlack10);
        view_home_coverBlack10 = findViewById(R.id.view_home_coverBlack10);
        view_rewind_coverBlack10 = findViewById(R.id.view_rewind_coverBlack10);
        view_play_pause_coverBlack10 = findViewById(R.id.view_play_pause_coverBlack10);
        view_forward_coverBlack10 = findViewById(R.id.view_forward_coverBlack10);
        view_backspace_coverBlack10 = findViewById(R.id.view_backspace_coverBlack10);
        view_menu_coverBlack10 = findViewById(R.id.view_menu_coverBlack10);
        view_volume_down_coverBlack10 = findViewById(R.id.view_volume_down_coverBlack10);
        view_volume_mute_coverBlack10 = findViewById(R.id.view_volume_mute_coverBlack10);
        view_volume_up_coverBlack10 = findViewById(R.id.view_volume_up_coverBlack10);
        view_ok_coverBlack10 = findViewById(R.id.view_ok_coverBlack10);

        ll_edit = (LinearLayout) findViewById(R.id.ll_keyboard_edit_homepage);
        et_edit = (EditText) findViewById(R.id.et_keyboard_edit_homepage);
        iv_edit = (ImageView) findViewById(R.id.iv_keyboard_edit_homepage);
        coverView = findViewById(R.id.view_coverBlack80);

        iv_close = findViewById(R.id.iv_close_homepage);
        iv_isConnect = findViewById(R.id.iv_isConnected);
        iv_disconnect = findViewById(R.id.iv_disconnect_homepage);
        iv_disconnect.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_disconnect_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_disconnect_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_disconnect);
                return true;
            }
            return false;
        });
        tv_selectDevice = findViewById(R.id.tv_select_device_homepage);
        ll_keyboard = findViewById(R.id.ll_keyboard_homepage);
        ll_keyboard.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_keyboard_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_keyboard_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(ll_keyboard);
                return true;
            }
            return false;
        });
        ll_channel = findViewById(R.id.ll_channel_homepage);
        ll_channel.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_channel_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_channel_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(ll_channel);
                return true;
            }
            return false;
        });

        iv_up = findViewById(R.id.iv_up_homepage);
        iv_up.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                iv_up.setBackgroundResource(R.mipmap.up_press);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                iv_up.setBackgroundResource(R.mipmap.up);
                onClick(iv_up);
                return true;
            }
            return false;
        });
        iv_down = findViewById(R.id.iv_down_homepage);
        iv_down.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                iv_down.setBackgroundResource(R.mipmap.down_press);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                iv_down.setBackgroundResource(R.mipmap.down);
                onClick(iv_down);
                return true;
            }
            return false;
        });
        iv_left = findViewById(R.id.iv_left_homepage);
        iv_left.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                iv_left.setBackgroundResource(R.mipmap.left_press);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                iv_left.setBackgroundResource(R.mipmap.left);
                onClick(iv_left);
                return true;
            }
            return false;
        });
        iv_right = findViewById(R.id.iv_right_homepage);
        iv_right.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                iv_right.setBackgroundResource(R.mipmap.right_press);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                iv_right.setBackgroundResource(R.mipmap.right);
                onClick(iv_right);
                return true;
            }
            return false;
        });
        iv_enter = findViewById(R.id.iv_ok_homepage);

        iv_enter.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_ok_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_ok_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_enter);
                return true;
            }
            return false;
        });

        iv_back = findViewById(R.id.iv_back_homepage);
        iv_back.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_back_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_back_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_back);
                return true;
            }
            return false;
        });
        iv_home = findViewById(R.id.iv_home_homepage);
        iv_home.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_home_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_home_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_home);
                return true;
            }
            return false;
        });
        iv_rewind = findViewById(R.id.iv_rewind_homepage);
        iv_rewind.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_rewind_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_rewind_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_rewind);
                return true;
            }
            return false;
        });
        iv_pause = findViewById(R.id.iv_play_pause_homepage);
        iv_pause.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_play_pause_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_play_pause_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_pause);
                return true;
            }
            return false;
        });
        iv_forward = findViewById(R.id.iv_forward_homepage);
        iv_forward.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_forward_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_forward_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_forward);
                return true;
            }
            return false;
        });
        iv_refresh = findViewById(R.id.iv_backspace_homepage);
        iv_refresh.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_backspace_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_backspace_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_refresh);
                return true;
            }
            return false;
        });
        iv_menu = findViewById(R.id.iv_menu_homepage);
        iv_menu.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_menu_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_menu_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_menu);
                return true;
            }
            return false;
        });
        iv_volumeDown = findViewById(R.id.iv_volume_down_homepage);
        iv_volumeDown.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_volume_down_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_volume_down_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_volumeDown);
                return true;
            }
            return false;
        });
        iv_volumeMute = findViewById(R.id.iv_volume_mute_homepage);
        iv_volumeMute.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_volume_mute_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_volume_mute_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_volumeMute);
                return true;
            }
            return false;
        });
        iv_volumeUp = findViewById(R.id.iv_volume_up_homepage);
        iv_volumeUp.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 按下时的操作
                view_volume_up_coverBlack10.setVisibility(View.VISIBLE);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                // 松开或取消时的操作
                view_volume_up_coverBlack10.setVisibility(View.INVISIBLE);
                onClick(iv_volumeUp);
                return true;
            }
            return false;
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_disconnect.setOnClickListener(this);
        tv_selectDevice.setOnClickListener(this);
        ll_keyboard.setOnClickListener(this);
        ll_channel.setOnClickListener(this);
        iv_up.setOnClickListener(this);
        iv_down.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        iv_enter.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        iv_rewind.setOnClickListener(this);
        iv_pause.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
        iv_refresh.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
        iv_volumeDown.setOnClickListener(this);
        iv_volumeMute.setOnClickListener(this);
        iv_volumeUp.setOnClickListener(this);

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);


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
                    if (keypress_board) {
                        et_edit.requestFocus();
                        keypress_board = false;
                    }
                    // 键盘显示时的处理逻辑
                    ll_edit.setVisibility(View.VISIBLE);
                    coverView.setVisibility(View.VISIBLE);
                    setEnabled(false);
                    findViewById(R.id.view_coverBlack80).setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Log.d(TAG, "onTouch: ");
                            et_edit.clearFocus();
                            return false;
                        }
                    });
                } else {
                    // 键盘隐藏时的处理逻辑
                    ll_edit.setVisibility(View.INVISIBLE);
                    coverView.setVisibility(View.INVISIBLE);
                    setEnabled(true);
                }
            }
        };
        et_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "onFocusChange: " + hasFocus);
                if (!hasFocus) {
                    if (iv_edit != null)
                        onClick(iv_edit);
                }
            }
        });
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        //设置收起键盘的监听器
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                et_edit.setText("");
                if (coverView != null)
                    coverView.setVisibility(View.INVISIBLE);
            }


        });
        //设置EditText的监听器
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
                        String RokuLocationUrl = FragmentRemoteControl.RokuLocationUrl;
                        char c = newText.charAt(newText.length() - 1);
                        if (c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
                            RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Lit_" + c);
                    }

                }
                if (index >= newText.length()) {
                    if (FragmentRemoteControl.RokuLocation != null) {
                        String RokuLocationUrl = FragmentRemoteControl.RokuLocationUrl;
                        RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Backspace");
                    }
                }
                index = newText.length();
            }
        });
    }


    public void setBackEvent() {
        if (OnlineDeviceUtils.mDeviceData_onLine.size() > 0) {
            DeviceManageHelper helper = new DeviceManageHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = null;
            for (DeviceBean bean : OnlineDeviceUtils.mDeviceData_onLine) {
                cursor = db.query(DeviceManageHelper.TABLE_HISTORY, null, DeviceManageHelper.USER_DEVICE_UDN + "=?", new String[]{bean.getUserDeviceUDN()}, null, null, null, null);
                if (cursor.getCount() > 0) {
                    FragmentRemoteControl.RokuLocation = bean.getUserDeviceIpAddress();
                    FragmentRemoteControl.RokuLocationUrl = RemoteUtils.getRokuLocationUrl(FragmentRemoteControl.RokuLocation);
                    FragmentRemoteControl.ConnectingDevice = bean;
                    break;
                }
            }
            if (cursor != null)
                cursor.close();
            if (db != null)
                db.close();

        }
    }

    private void setEnabled(boolean flag) {
        findViewById(R.id.iv_disconnect_homepage).setEnabled(flag);
        findViewById(R.id.tv_select_device_homepage).setEnabled(flag);
        findViewById(R.id.ll_keyboard_homepage).setEnabled(flag);
        findViewById(R.id.ll_channel_homepage).setEnabled(flag);
        findViewById(R.id.iv_up_homepage).setEnabled(flag);
        findViewById(R.id.iv_down_homepage).setEnabled(flag);
        findViewById(R.id.iv_left_homepage).setEnabled(flag);
        findViewById(R.id.iv_right_homepage).setEnabled(flag);
        findViewById(R.id.iv_ok_homepage).setEnabled(flag);
        findViewById(R.id.iv_back_homepage).setEnabled(flag);
        findViewById(R.id.iv_home_homepage).setEnabled(flag);
        findViewById(R.id.iv_rewind_homepage).setEnabled(flag);
        findViewById(R.id.iv_play_pause_homepage).setEnabled(flag);
        findViewById(R.id.iv_forward_homepage).setEnabled(flag);
        findViewById(R.id.iv_backspace_homepage).setEnabled(flag);
        findViewById(R.id.iv_menu_homepage).setEnabled(flag);
        findViewById(R.id.iv_volume_down_homepage).setEnabled(flag);
        findViewById(R.id.iv_volume_mute_homepage).setEnabled(flag);
        findViewById(R.id.iv_volume_up_homepage).setEnabled(flag);

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (SettingActivity.isVibrator && vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(20L);
        }
        if (FragmentRemoteControl.RokuLocation == null) {
            DeviceManageHelper helper = new DeviceManageHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            Cursor cursor = db.query(DeviceManageHelper.TABLE_HISTORY, null, null, null, null, null, null);
            if (cursor.getCount() <= 0) {
                Intent intent = new Intent(this, DeviceAdd.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, DeviceManage.class);
                startActivity(intent);
            }
            cursor.close();
            db.close();
            return;
        }
        switch (v.getId()) {
            case R.id.iv_disconnect_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/PowerOff");
                break;
            case R.id.tv_select_device_homepage:
                Intent intent = new Intent(this, DeviceManage.class);
                startActivity(intent);
                break;
            case R.id.ll_keyboard_homepage:
                keypress_board = true;
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                coverView.setVisibility(View.VISIBLE);
                break;
            case R.id.ll_channel_homepage:
                launchChannel();
                break;
            case R.id.iv_up_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Up");
                break;
            case R.id.iv_down_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Down");
                break;
            case R.id.iv_left_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Left");
                break;
            case R.id.iv_right_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Right");
                break;
            case R.id.iv_ok_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Select");
                break;
            case R.id.iv_back_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Back");
                break;
            case R.id.iv_home_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Home");
                break;
            case R.id.iv_rewind_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Rev");
                break;
            case R.id.iv_play_pause_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Play");
                break;
            case R.id.iv_forward_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Fwd");
                break;
            case R.id.iv_backspace_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Backspace");
                break;
            case R.id.iv_menu_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/Info");
                break;
            case R.id.iv_volume_down_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/VolumeDown");
                break;
            case R.id.iv_volume_mute_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/VolumeMute");
                break;
            case R.id.iv_volume_up_homepage:
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "keypress/VolumeUp");
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        OnlineDeviceUtils.saveLatestOnLineDevice(this, FragmentRemoteControl.ConnectingDevice);
    }

    private void launchChannel() {
        OkHttpClient client = new OkHttpClient();
        String url = FragmentRemoteControl.RokuLocationUrl + "query/apps";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: query:" + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String channelXml = response.body().string();
                Log.d(TAG, "onResponse: query" + channelXml);
                String[] lines = channelXml.split("\n");
                boolean isInstall = false;
                for (String line : lines) {
                    if (line.startsWith("\t<app id=\"706370\"")) {
                        RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "launch/706370"); //已经存在该频道，无需安装，直接启动
                        isInstall = true;
                    }
                }
                if (!isInstall)
                    RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "install/706370");//未存在该频道，需要安装
            }
        });
    }

    @Override
    public void onResume() {
        OnlineDeviceUtils.setOnConnectedListener(new OnlineDeviceUtils.OnConnectedListener() {
            @Override
            public void autoConnect() {
                setBackEvent();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setConnectionStatus(FragmentRemoteControl.RokuLocation != null);
                    }
                });
            }

            @Override
            public void disConnect() {
                if (OnlineDeviceUtils.mDeviceData_onLine.size() <= 0) {
                    Log.d(TAG, "deviceData_online is zero");
                    FragmentRemoteControl.RokuLocation = null;
                    FragmentRemoteControl.RokuLocationUrl = null;
                    FragmentRemoteControl.ConnectingDevice = null;
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

        });
        setConnectionStatus(FragmentRemoteControl.RokuLocation != null);
        Log.d(TAG, "onResume: " + FragmentRemoteControl.RokuLocationUrl);
        super.onResume();

        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();

        int keyboardHeight = screenHeight - r.bottom;
        boolean isKeyboardOpen = keyboardHeight > screenHeight * 0.15;

        // 根据键盘的显示/隐藏状态进行相应的处理
        if (isKeyboardOpen) {
            Log.d(TAG, "onResume: 键盘正在打开");
            closeKeyBoard();
        } else {
            Log.d(TAG, "onResume: 键盘正在关闭");
        }


    }

    private void setConnectionStatus(boolean flag) {
        Log.d(TAG, "setConnectionStatus: ");
        if (flag) {
            String deviceName = FragmentRemoteControl.ConnectingDevice.getUserDeviceName();
            iv_isConnect.setImageResource(R.mipmap.connected_homepage);

            tv_selectDevice.setText(deviceName);

        } else {
            iv_isConnect.setImageResource(R.mipmap.no_connected);
            tv_selectDevice.setText(R.string.Select_device);
        }
    }

    public void closeKeyBoard() {
        Log.d(TAG, "closeKeyBoard: 123");
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();

        int keyboardHeight = screenHeight - r.bottom;
        boolean isKeyboardOpen = keyboardHeight > screenHeight * 0.15;

        // 根据键盘的显示/隐藏状态进行相应的处理
        if (isKeyboardOpen) {
            ll_edit.setVisibility(View.INVISIBLE);
            et_edit.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(ll_edit.getWindowToken(), 0);
            if (coverView != null)
                coverView.setVisibility(View.INVISIBLE);
        }
    }

}
