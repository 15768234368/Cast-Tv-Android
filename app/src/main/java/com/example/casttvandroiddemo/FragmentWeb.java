package com.example.casttvandroiddemo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentWeb extends Fragment {
    private static final String TAG = "FragmentWeb";
    private View view;
    private WebView webView;
    private String url = "https://www.baidu.com";
    private OnPageLoadedListener onPageLoadedListener;
    public interface OnPageLoadedListener{
        void onPageLoaded(String url);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        webView = (WebView) view.findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //页面开始加载时的操作
                Log.d(TAG, "onPageStarted: " + url);
            }


            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                //加载页面出错的操作
                Log.d(TAG, "onReceivedError: " + error.toString());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "onPageFinished: " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
                if(newUrl.startsWith("http:") || newUrl.startsWith("https:")){
                    view.loadUrl(newUrl);
                    Log.d(TAG, "shouldOverrideUrlLoading: http:/https:" + newUrl);

                }
                Log.d(TAG, "shouldOverrideUrlLoading:other " + newUrl);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(url);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null){
            url = bundle.getString("url");
        }
        return view = inflater.inflate(R.layout.fragment_webview, container, false);
    }
    public void setOnPageLoadedListener(OnPageLoadedListener listener) {
        this.onPageLoadedListener = listener;
    }

    public void updateSearchViewUrl(String url){
        if(onPageLoadedListener != null){
            onPageLoadedListener.onPageLoaded(url);
        }
    }
}