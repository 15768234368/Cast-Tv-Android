package com.example.casttvandroiddemo;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.webkit.WebView;

import com.example.casttvandroiddemo.Service.VolumeService;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.UnitsManager;


public class MyApplication extends Application {
    private WebView webView;

    public WebView getWebView() {
        return webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initAutoSize();

        //启动服务
        startService(new Intent(this, VolumeService.class));
    }

    private void initAutoSize() {
        AutoSizeConfig.getInstance().getUnitsManager();
        AutoSizeConfig.getInstance()
                .setDesignWidthInDp(360)  //设置设计图的宽度，单位dp
                .setDesignHeightInDp(640) //设置设计图的高度，单位dp
                .setExcludeFontScale(true) //是否排除系统字体的影响
                .setUseDeviceSize(false)  //是否使用机型自带的字体配置
                .setLog(BuildConfig.DEBUG) //是否打印日志
                .setCustomFragment(true); //是否支持自定义适配Fragment
    }

}
