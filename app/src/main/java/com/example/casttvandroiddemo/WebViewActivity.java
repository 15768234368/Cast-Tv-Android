package com.example.casttvandroiddemo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.casttvandroiddemo.bean.CastVideoBean;
import com.example.casttvandroiddemo.helper.InternetHistoryHelper;
import com.example.casttvandroiddemo.utils.IntentUtils;
import com.example.casttvandroiddemo.utils.StringUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private RelativeLayout rl_detectCastContentTip;
    private View view_detectCastBg;
    private TextView tv_closeCastContentTip;
    public static List<CastVideoBean> mVideoBean = new ArrayList<>();
    public ProgressBar progressBar;

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
                        SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
                        boolean isCastTip = sp.getBoolean("castTip", false);
                        if (!isCastTip) {
                            showDetectCastTip();
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean("castTip", true);
                            editor.apply();
                        }
                        iv_cast.setImageResource(R.mipmap.cast_lighted_browser_cast);
                        iv_cast.setEnabled(true);
                    }
                }, 2000);
            }
            return true;
        }
    });

    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        webView = (WebView) findViewById(R.id.webView);
        setWebViewSetting();
        searchView = (SearchView) findViewById(R.id.searchView);
        setSearchViewSetting();
        iv_back = (ImageView) findViewById(R.id.iv_back_browserCast);
        iv_forward = (ImageView) findViewById(R.id.iv_forward_browserCast);
        iv_cast = (ImageView) findViewById(R.id.iv_cast_browserCast);
        iv_history = (ImageView) findViewById(R.id.iv_history_browserCast);
        iv_remote = (ImageView) findViewById(R.id.iv_remote_browserCast);
        view_detectCastBg = (View) findViewById(R.id.view_detectCastBg);
        rl_detectCastContentTip = (RelativeLayout) findViewById(R.id.rl_detectCastContentTip);
        tv_closeCastContentTip = (TextView) findViewById(R.id.tv_closeCastContentTip);

        iv_back.setOnClickListener(this);
        iv_forward.setOnClickListener(this);
        iv_cast.setOnClickListener(this);
        iv_history.setOnClickListener(this);
        iv_remote.setOnClickListener(this);
        tv_closeCastContentTip.setOnClickListener(this);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //失去焦点，恢复网址内容
                if (!hasFocus) {
                    searchView.setQuery(loadingUrl, false);
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showDetectCastTip() {
//        1.使用视图遮盖实现
        iv_forward.setImageResource(R.mipmap.forward_lighted_browser_cast);

        view_detectCastBg.setVisibility(View.VISIBLE);
        rl_detectCastContentTip.setVisibility(View.VISIBLE);
        iv_cast.setImageResource(R.mipmap.cast_lighted_browser_cast);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true; // 禁止WebView处理点击事件
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void closeDetectCastTip() {
        updateIsForwardStatus(webView.canGoForward());
        view_detectCastBg.setVisibility(View.INVISIBLE);
        rl_detectCastContentTip.setVisibility(View.INVISIBLE);
        webView.setOnTouchListener(null); // 移除触摸事件监听器，恢复点击事件处理
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
            private ProgressBar progressBar = WebViewActivity.this.progressBar;

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d(TAG, "onProgressChanged: " + newProgress);
                progressBar.setProgress(newProgress);
                if (progressBar.getProgress() >= 0 && progressBar.getProgress() < 100)
                    progressBar.setVisibility(View.VISIBLE);
                if (progressBar.getProgress() >= 100)
                    progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                loadingUrl = view.getUrl();
                updateSearchViewUrl(loadingUrl);
                Log.d(TAG, "onReceivedTitle: " + loadingUrl);
                updateIsForwardStatus(webView.canGoForward());
                Log.d(TAG, "onReceivedTitle: " + title);
                if (!title.isEmpty()) {
                    if (loadingUrl.startsWith("https://m.bilibili.com/video")) {
                        getRealVideoUrlFromBiliBili(loadingUrl);
                    } else if (loadingUrl.startsWith("https://m.youtube.com/")) {
                        getRealVideoUrlFromYouTuBe(loadingUrl);
                    } else if (loadingUrl.startsWith("https://www.espn.com")) {
                        getRealVideoUrlFromESPN(loadingUrl);
                    }
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
                if (StringUtils.containsChinese(s)) {
                    String newUrl = "https://www.google.com/search?q=" + s;
                    Log.d(TAG, "onQueryTextSubmit: " + newUrl);
                    webView.loadUrl(newUrl);
                    return false;
                }
                if (s.startsWith("https://") || s.startsWith("http://")) {
                    webView.loadUrl(s);
                } else if (s.startsWith("www")) {
                    webView.loadUrl("https://" + s);
                } else {
                    String newUrl = "https://www.google.com/search?q=" + s;
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
                Log.d(TAG, "run: " + responseBody);

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
                String videoTitle = null;
                if (element != null) {
                    videoTitle = element.attr("content");
                } else {
                    return;
                }

                //获取视频的图片Url
                elements = doc.select("[itemprop=image]");
                element = elements.first();
                String videoImageUrl = null;
                if (element != null) {
                    videoImageUrl = element.attr("content");
                } else {
                    return;
                }
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

    @Override
    protected void onResume() {
        closeDetectCastTip();
        Log.d(TAG, "onResume: " + "web");
        super.onResume();
    }

    public void getRealVideoUrlFromESPN(String url) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36")
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resHtml = response.body().string();

                    String pattern = "\"href\":\"https://www\\.espn\\.com/video/clip\\?id=(\\d+)\"";
                    Pattern idPattern = Pattern.compile(pattern);
                    Matcher idMatcher = idPattern.matcher(resHtml);

                    Set<String> uniqueIds = new HashSet<>();
                    while (idMatcher.find()) {
                        String idNumber = idMatcher.group(1);
                        uniqueIds.add(idNumber);
                    }

                    if (!uniqueIds.isEmpty()) {
                        for (String idNumber : uniqueIds) {
                            Log.d(TAG, idNumber);
                            String clipUrl = "https://www.espn.com/video/clip?id=" + idNumber;
                            Request clipRequest = new Request.Builder()
                                    .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36")
                                    .url(clipUrl)
                                    .build();
                            client.newCall(clipRequest).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        String resClipHtml = response.body().string();

                                        String patternUrl = "\"contentUrl\"\\s*:\\s*\"([^\"]+)\"";
                                        String patternThumbnailUrl = "\"thumbnailURL\"\\s*:\\s*\"([^\"]+)\"";
                                        String patternTitle = "<h1[^>]*>(.*?)</h1>";

                                        Pattern urlPattern = Pattern.compile(patternUrl);
                                        Pattern thumbnailUrlPattern = Pattern.compile(patternThumbnailUrl);
                                        Pattern titlePattern = Pattern.compile(patternTitle);

                                        Matcher urlMatcher = urlPattern.matcher(resClipHtml);
                                        Matcher thumbnailUrlMatcher = thumbnailUrlPattern.matcher(resClipHtml);
                                        Matcher titleMatcher = titlePattern.matcher(resClipHtml);
                                        String videoTitle = null;
                                        String readyVideoUrl = null;
                                        String videoImageUrl = null;
                                        if (titleMatcher.find()) {
                                            videoTitle = titleMatcher.group(1);
                                            Log.d(TAG, "video title: " + videoTitle);
                                        } else {
                                            Log.d(TAG, "video Image not found");
                                        }
                                        if (urlMatcher.find()) {
                                            readyVideoUrl = urlMatcher.group(1);
                                            Log.d(TAG, "video URL: " + readyVideoUrl);
                                        } else {
                                            Log.d(TAG, "Video URL not found");
                                        }

                                        if (thumbnailUrlMatcher.find()) {
                                            videoImageUrl = thumbnailUrlMatcher.group(1);
                                        } else {
                                            Log.d(TAG, "Thumbnail URL not found");
                                        }

                                        Message message = new Message();
                                        message.obj = new CastVideoBean(videoImageUrl, videoTitle, readyVideoUrl, clipUrl);
                                        handler.sendMessage(message);
                                        Log.d(TAG, "1: ");
                                    } else {
                                        Log.d(TAG, "Failed to fetch clip HTML");
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.d(TAG, "Failed to fetch HTML");
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
                IntentUtils.goToActivity(this, RemoteControlActivity.class);
                break;
            case R.id.tv_closeCastContentTip:
                closeDetectCastTip();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoBean.clear();
        Log.d(TAG, "onDestroy: ");
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

class Thumbnail {
    private List<Thumbnails> thumbnails;

    public List<Thumbnails> getThumbnails() {
        return thumbnails;
    }
}

class Thumbnails {
    private String url;

    public String getUrl() {
        return url;
    }
}