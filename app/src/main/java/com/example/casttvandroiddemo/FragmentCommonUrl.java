package com.example.casttvandroiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentCommonUrl extends Fragment implements View.OnClickListener{
    private View view;
    public static final String youtubeUrl = "https://www.youtube.com/";
    public static final String biliBiliUrl = "https://bilibili.com/";
    public static final String espnUrl = "https://www.espn.com/";
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        view.findViewById(R.id.ll_keyTo_youtube).setOnClickListener(this);
        view.findViewById(R.id.ll_keyTo_BiliBili).setOnClickListener(this);
        view.findViewById(R.id.ll_keyTo_ESPN).setOnClickListener(this);
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
                break;
            case R.id.ll_keyTo_BiliBili:
                navigateToWebActivityByUrl(biliBiliUrl);
                break;
            case R.id.ll_keyTo_ESPN:
                navigateToWebActivityByUrl(espnUrl);
                break;
        }
    }

    public void navigateToWebActivityByUrl(String url){
        Intent intent = new Intent(getContext(), WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }
}
