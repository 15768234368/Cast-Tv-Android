package com.example.casttvandroiddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.casttvandroiddemo.utils.IntentUtils;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "WebViewActivity";
    private String loadingUrl;
    private WebView webView;
    private SearchView searchView;
    private ImageView iv_back, iv_forward, iv_cast, iv_history, iv_remote;
    private String realVideoUrl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        loadingUrl = intent.getStringExtra("url");
        initView();
        setEvent();
    }

    private void setEvent() {
        iv_cast.setEnabled(false);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if(message.obj != null){
                realVideoUrl = (String) message.obj;
                Handler delayPost = new Handler();
                delayPost.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_cast.setImageResource(R.mipmap.cast_lighted_browser_cast);
                        iv_cast.setEnabled(true);
                    }
                }, 2000);
            }
            return true;
        }
    });
    private void initView() {
        webView = (WebView) findViewById(R.id.webView);
        setWebViewSetting();
        searchView = (SearchView) findViewById(R.id.searchView);
        setSearchViewSetting();
        iv_back = (ImageView) findViewById(R.id.iv_back_browserCast);
        iv_forward = (ImageView) findViewById(R.id.iv_forward_browserCast);
        iv_cast = (ImageView) findViewById(R.id.iv_cast_browserCast);
        iv_history = (ImageView) findViewById(R.id.iv_history_browserCast);
        iv_remote = (ImageView) findViewById(R.id.iv_remote_browserCast);

        iv_back.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
        iv_cast.setOnClickListener(this);
        iv_history.setOnClickListener(this);
        iv_remote.setOnClickListener(this);
    }
    /**
     * 对私有变量webView进行初始化设置
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void setWebViewSetting(){
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
                updateIsForwardStatus(webView.canGoForward());
                if(loadingUrl.startsWith("https://m.bilibili.com/video")){
                    getRealVideoUrl();
                }
            }
        });
        webView.loadUrl(loadingUrl);
    }
    public void setSearchViewSetting(){
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
    public void updateIsForwardStatus(boolean flag){
        if(flag){
            iv_forward.setImageResource(R.mipmap.forward_lighted_browser_cast);
            iv_forward.setEnabled(true);
        }else{
            iv_forward.setImageResource(R.mipmap.forward_unlighted_browser_cast);
            iv_forward.setEnabled(false);
        }
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
     */
    public void getRealVideoUrl() {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back_browserCast:
                if(webView.canGoBack())
                    webView.goBack();
                else
                    finish();
                break;
            case R.id.iv_forward_browserCast:
                webView.goForward();
                break;
            case R.id.iv_cast_browserCast:
                castToTv();
                break;
            case R.id.iv_history_browserCast:
                break;
            case R.id.iv_remote_browserCast:
                IntentUtils.goToActivity(this, MainActivity.class);
                finish();
                break;
        }
    }

    private void castToTv() {
    }
}