package com.example.casttvandroiddemo;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;

public class MySpan extends ClickableSpan {
    private static final String TAG = "MySpan";
    private String tag;
    private String url;

    public MySpan(String tag, String url) {
        this.tag = tag;
        this.url = url;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        // ds.setColor(getResources().getColor(R.color.selector_blue)); // 设置颜色
        ds.setUnderlineText(false); // 去掉下划线
    }

    @Override
    public void onClick(View widget) {
        Log.i(TAG, "onClick: " + url + " " + tag); // 重写点击事件
    }
}