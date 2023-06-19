package com.example.casttvandroiddemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.casttvandroiddemo.adapter.CastVideoListAdapter;
import com.example.casttvandroiddemo.helper.DeviceManageHelper;
import com.example.casttvandroiddemo.utils.AppManage;
import com.example.casttvandroiddemo.utils.RemoteUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CastVideoListActivity extends AppCompatActivity {
    private static final String TAG = "CastVideoListActivity";
    private RecyclerView rv_castVideoList;
    private ImageView iv_back;
    private TextView tv_castVideoCnt;
    private CastVideoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cast_video_list);
        initView();
        AppManage.Admob_bannerInit(this, findViewById(R.id.AdView));
    }

    private void initView() {
        rv_castVideoList = (RecyclerView) findViewById(R.id.rv_castVideoList);
        iv_back = (ImageView) findViewById(R.id.iv_back_castVideoList);
        tv_castVideoCnt = (TextView) findViewById(R.id.tv_castVideoCount);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getApplicationContext(), "返回");
                finish();
            }
        });

        adapter = new CastVideoListAdapter(WebViewActivity.mVideoBean, this, this);
        adapter.setOnItemClickListener(new CastVideoListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                MobclickAgent.onEvent(getApplicationContext(), "投屏");
                //判断是否有网络连接，有则判断是否存在投屏频道，无则跳转连接
                if (FragmentRemoteControl.RokuLocation == null) {
                    //若有历史连接设备，则跳转到设备管理页，否则跳转到设备添加页
                    DeviceManageHelper helper = new DeviceManageHelper(getApplicationContext());
                    SQLiteDatabase db = helper.getReadableDatabase();
                    if(db.query(DeviceManageHelper.TABLE_HISTORY, null, null, null, null, null, null).getCount() <= 0){
                        startActivity(new Intent(getApplicationContext(), DeviceAdd.class));
                    }else{
                        startActivity(new Intent(getApplicationContext(), DeviceManage.class));
                    }
                    db.close();
                    helper.close();
                    return ;
                }


                //判断是否存在该频道，有则投屏，无则跳转安装
                RemoteUtils.isExistsChannelToCast(FragmentRemoteControl.RokuLocationUrl, new RemoteUtils.ChannelLaunchCallback() {
                    @Override
                    public void onChannelLaunchResult(boolean isInstall) {
                        if (isInstall) {
                            try {
                                showDialogCastSuccess();
                                RemoteUtils.castToTv(FragmentRemoteControl.RokuLocationUrl, WebViewActivity.mVideoBean.get(WebViewActivity.mVideoBean.size() - position - 1).getVideoRealUrl());
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            showDialogChannelInstall();
                        }
                    }
                });


            }
        });
        rv_castVideoList.setLayoutManager(new LinearLayoutManager(this));
        rv_castVideoList.setAdapter(adapter);
    }

    private void showDialogCastSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Handler handler = new Handler();
                Dialog dialog = new Dialog(CastVideoListActivity.this);
                dialog.setContentView(R.layout.dialog_cast_success);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                dialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.cancel();
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int cnt = 0;
        if (WebViewActivity.mVideoBean != null)
            cnt = WebViewActivity.mVideoBean.size();
        String videoCount = getResources().getString(R.string.x_videos_in_total, cnt);
        tv_castVideoCnt.setText(videoCount);
    }

    public void showDialogChannelInstall() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_channel_tip);
        dialog.show();
        dialog.findViewById(R.id.tv_cancelInstall_channelDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getApplicationContext(), "取消安装");
                dialog.cancel();
            }
        });
        dialog.findViewById(R.id.tv_determineInstall_channelDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getApplicationContext(), "安装");
                RemoteUtils.httpPost(FragmentRemoteControl.RokuLocationUrl, "install/706370");//未存在该频道，需要安装
                dialog.cancel();
            }
        });
    }
}