package com.example.casttvandroiddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class EnableClosedCaptioningActivity extends AppCompatActivity {
    private ImageView iv_back;
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
    }
}