package com.example.casttvandroiddemo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.casttvandroiddemo.bean.CastVideoBean;
import com.example.casttvandroiddemo.helper.InternetHistoryHelper;
import com.example.casttvandroiddemo.utils.IntentUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "WebViewActivity";
    private String loadingUrl;
    private WebView webView;
    private SearchView searchView;
    private ImageView iv_back, iv_forward, iv_cast, iv_history, iv_remote;
    public static List<CastVideoBean> mVideoBean = new ArrayList<>();

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
            CastVideoBean bean = (CastVideoBean) message.obj;
            if (message.obj != null) {
                int i;
                for (i = 0; i < mVideoBean.size(); ++i) {
                    if (mVideoBean.get(i).getVideoFirstUrl().equals(bean.getVideoFirstUrl()))
                        break;
                }
                if (i >= mVideoBean.size())
                    mVideoBean.add(bean);
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
    public void setWebViewSetting() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //页面开始加载时的操作
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
                if (loadingUrl.startsWith("https://m.bilibili.com/video")) {
                    getRealVideoUrlFromBiliBili(loadingUrl);
                } else if (loadingUrl.startsWith("https://m.youtube.com/")) {
                    getRealVideoUrlFromYouTuBe(loadingUrl);
                }
                SaveHistoryToDB(loadingUrl, title);
            }
        });
        webView.loadUrl(loadingUrl);
    }

    private void SaveHistoryToDB(String loadingUrl, String title) {
        InternetHistoryHelper helper = new InternetHistoryHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InternetHistoryHelper.URL, loadingUrl);
        values.put(InternetHistoryHelper.TITLE, title);
        db.insert(InternetHistoryHelper.TABLE_HISTORY, null, values);
        db.close();
    }

    public void setSearchViewSetting() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.startsWith("https") || s.startsWith("http")) {
                    webView.loadUrl(s);
                } else {
                    String newUrl = "https://www.google.com.hk/search?q=" + s;
                    Log.d(TAG, "onQueryTextSubmit: " + newUrl);
                    webView.loadUrl(newUrl);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public void updateIsForwardStatus(boolean flag) {
        if (flag) {
            iv_forward.setImageResource(R.mipmap.forward_lighted_browser_cast);
            iv_forward.setEnabled(true);
        } else {
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
     */
    public void getRealVideoUrlFromBiliBili(String loadingUrl) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String responseBody = null;
                String readyVideoUrl = null;
                String url = loadingUrl;
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Mobile Safari/537.36")
                        .build();


                //发送HTTP请求并获取响应,获得返回的网页数据
                try {
                    Response response = client.newCall(request).execute();
                    responseBody = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //获得视频的url
                Pattern pattern_videoUrl = Pattern.compile("options = (\\{.*\\})");
                Matcher matcher = pattern_videoUrl.matcher(responseBody);
                if (matcher.find()) {
                    String jsonOptions = matcher.group(1);
                    //解析JSON数据
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonOptions);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    readyVideoUrl = jsonObject.optString("readyVideoUrl");
                }

                //获得视频的标题
                Document doc = Jsoup.parse(responseBody);
                Elements elements = doc.select("[itemprop=name]");
                Element element = elements.first();
                String videoTitle = element.attr("content");

                //获取视频的图片Url
                elements = doc.select("[itemprop=image]");
                element = elements.first();
                String videoImageUrl = element.attr("content");
                Log.d(TAG, "run: " + videoTitle);
                Log.d(TAG, "run: " + videoImageUrl);

                //将视频的url，图片url，标题发送
                Message message = new Message();
                message.obj = new CastVideoBean(videoImageUrl, videoTitle, readyVideoUrl, url);
                handler.sendMessage(message);
            }
        });
        thread.start();
    }

    public void getRealVideoUrlFromYouTuBe(String url) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("cookie", "VISITOR_INFO1_LIVE=oOs_cS24QWs; YSC=OIpUy83rrgM; PREF=tz=Asia.Shanghai&f5=20000&f4=4000000; GPS=1")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    // 在这里处理响应结果
                    String jsonStr = extractJsonData(responseBody);
                    if (jsonStr != null) {
                        VideoData videoData = parseJsonData(jsonStr);
                        if (videoData != null) {
                            String readyVideoUrl = videoData.getStreamingData().getFormats().get(0).getUrl();
                            String videoTitle = videoData.getVideoDetails().getTitle();
                            String videoImageUrl = videoData.getVideoDetails().getThumbnail().getThumbnails().get(0).getUrl();
                            Log.d(TAG, "Video URL: " + readyVideoUrl);
                            Log.d(TAG, "Video Title: " + videoTitle);
                            Log.d(TAG, "Video Photo URL" + videoImageUrl);
                            //将视频的url，图片url，标题发送
                            Message message = new Message();
                            message.obj = new CastVideoBean(videoImageUrl, videoTitle, readyVideoUrl, url);
                            handler.sendMessage(message);
                        }
                    }
                } else {
                    // 处理请求失败的情况
                    Log.d(TAG, "onResponse: failed");
                }
            }
        });
    }

    private String extractJsonData(String htmlData) {
        String patternString = "var ytInitialPlayerResponse = (.*?);var";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(htmlData);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private VideoData parseJsonData(String jsonStr) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(jsonStr, VideoData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_browserCast:
                if (webView.canGoBack())
                    webView.goBack();
                else
                    finish();
                break;
            case R.id.iv_forward_browserCast:
                webView.goForward();
                break;
            case R.id.iv_cast_browserCast:
                Intent intent_cast = new Intent(this, CastVideoListActivity.class);
                startActivity(intent_cast);
                break;
            case R.id.iv_history_browserCast:
                Intent intent_history = new Intent(this, InternetHistoryList.class);
                startActivity(intent_history);
                break;
            case R.id.iv_remote_browserCast:
                IntentUtils.goToActivity(this, MainActivity.class);
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoBean.clear();
    }
}

class VideoData {
    private StreamingData streamingData;

    private VideoDetails videoDetails;

    public StreamingData getStreamingData() {
        return streamingData;
    }

    public VideoDetails getVideoDetails() {
        return videoDetails;
    }
}

class StreamingData {
    private List<Format> formats;

    public List<Format> getFormats() {
        return formats;
    }
}

class Format {
    private String url;

    public String getUrl() {
        return url;
    }
}

class VideoDetails {
    private String title;
    private Thumbnail thumbnail;
    public String getTitle() {
        return title;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }
}

class Thumbnail{
    private List<Thumbnails> thumbnails;

    public List<Thumbnails> getThumbnails() {
        return thumbnails;
    }
}

class Thumbnails{
    private String url;

    public String getUrl() {
        return url;
    }
}