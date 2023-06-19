package com.example.casttvandroiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.umeng.analytics.MobclickAgent;

import java.util.Locale;

public class FragmentCommonUrl extends Fragment implements View.OnClickListener{
    private View view;
    public static final String youtubeUrl = "https://www.youtube.com/";
    public static final String biliBiliUrl = "https://bilibili.com/";
    public static final String espnUrl = "https://www.espn.com/";

    private LinearLayout ll_youtube, ll_bilibili, ll_espn;
    private Locale currentLocale;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        currentLocale = getResources().getConfiguration().locale;
        String language = currentLocale.getLanguage();
        ll_youtube = view.findViewById(R.id.ll_keyTo_youtube);
        ll_bilibili = view.findViewById(R.id.ll_keyTo_BiliBili);
        ll_espn = view.findViewById(R.id.ll_keyTo_ESPN);

        if(language.equals("zh")){
            ll_bilibili.setVisibility(View.VISIBLE);
            ll_espn.setVisibility(View.INVISIBLE);
            ll_youtube.setVisibility(View.INVISIBLE);
            ll_bilibili.setOnClickListener(this);
        }else{
            ll_bilibili.setVisibility(View.INVISIBLE);
            ll_espn.setVisibility(View.VISIBLE);
            ll_youtube.setVisibility(View.VISIBLE);
            ll_youtube.setOnClickListener(this);
            ll_espn.setOnClickListener(this);

        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return view = inflater.inflate(R.layout.fragment_common_urls, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_keyTo_youtube:
                navigateToWebActivityByUrl(youtubeUrl);
                MobclickAgent.onEvent(getContext(), "Youtube");
                break;
            case R.id.ll_keyTo_BiliBili:
                navigateToWebActivityByUrl(biliBiliUrl);
                MobclickAgent.onEvent(getContext(), "BiliBili");
                break;
            case R.id.ll_keyTo_ESPN:
                navigateToWebActivityByUrl(espnUrl);
                MobclickAgent.onEvent(getContext(), "ESPN");
                break;
        }
    }

    public void navigateToWebActivityByUrl(String url){
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}
