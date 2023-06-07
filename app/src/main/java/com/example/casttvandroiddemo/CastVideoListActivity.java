package com.example.casttvandroiddemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.casttvandroiddemo.adapter.CastVideoListAdapter;

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
    }

    private void initView() {
        rv_castVideoList = (RecyclerView) findViewById(R.id.rv_castVideoList);
        iv_back = (ImageView) findViewById(R.id.iv_back_castVideoList);
        tv_castVideoCnt = (TextView) findViewById(R.id.tv_castVideoCount);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        adapter = new CastVideoListAdapter(WebViewActivity.mVideoBean, this, this);
        adapter.setOnItemClickListener(new CastVideoListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                try {
                    castToTv(WebViewActivity.mVideoBean.get(position).getVideoRealUrl());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        rv_castVideoList.setLayoutManager(new LinearLayoutManager(this));
        rv_castVideoList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int cnt = 0;
        if(WebViewActivity.mVideoBean != null)
            cnt = WebViewActivity.mVideoBean.size();
        tv_castVideoCnt.setText("共" + cnt + "个视频");
    }

    public void castToTv(String realVideoUrl) throws UnsupportedEncodingException {
        String getUrl = "http://192.168.50.228:8060/input/698776?url=" + URLEncoder.encode(realVideoUrl, "UTF-8") + "&t=v&name=video&format=Default";
        Request request = new Request.Builder()
                .url(getUrl)
                .post(RequestBody.create(MediaType.parse("application/json"), ""))
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + "success");
            }
        });
    }
}