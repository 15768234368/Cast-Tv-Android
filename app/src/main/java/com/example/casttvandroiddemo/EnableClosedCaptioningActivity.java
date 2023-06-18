package com.example.casttvandroiddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

public class EnableClosedCaptioningActivity extends AppCompatActivity {
    private ImageView iv_back;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_closed_captioning);
        initView();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back_setting);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
}