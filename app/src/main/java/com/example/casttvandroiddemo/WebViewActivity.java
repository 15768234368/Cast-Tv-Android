package com.example.casttvandroiddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG = "WebViewActivity";
    private String loadingUrl;
    private WebView webView;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        loadingUrl = intent.getStringExtra("url");
        initView();
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if(message.obj != null)
                updateSearchViewUrl((String) message.obj);
            return true;
        }
    });
    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        webView = (WebView) findViewById(R.id.webView);
        searchView = (SearchView) findViewById(R.id.searchView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.setWebViewClient(new WebViewClient() {
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
                if (newUrl.startsWith("http:") || newUrl.startsWith("https:")) {
                    view.loadUrl(newUrl);
                    return false;
                }
                return true;
            }

        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                loadingUrl = view.getUrl();
                updateSearchViewUrl(loadingUrl);
                Log.d(TAG, "onReceivedTitle: " + loadingUrl);
            }
        });
        webView.loadUrl(loadingUrl);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                webView.loadUrl(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public void updateSearchViewUrl(String loadingUrl) {
        searchView.setQuery(loadingUrl, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 通过获得网页播放网址去获取视频资源的真正url
     *
     * @param view
     */
    public void castUrlToTv(View view) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                String url = loadingUrl;
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Mobile Safari/537.36")
                        .build();

                //发送HTTP请求并获取响应
                try {
                    Response response = client.newCall(request).execute();
                    String responseBody = response.body().string();
                    Log.d(TAG, "run: " + responseBody);
                    Pattern pattern = Pattern.compile("options = (\\{.*\\})");
                    Matcher matcher = pattern.matcher(responseBody);
                    if (matcher.find()) {
                        String jsonOptions = matcher.group(1);

                        //解析JSON数据
                        JSONObject jsonObject = new JSONObject(jsonOptions);
                        final String readyVideoUrl = jsonObject.optString("readyVideoUrl");
                        Message message = new Message();
                        message.obj = readyVideoUrl;
                        handler.sendMessage(message);
                        //打印解析结果

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}