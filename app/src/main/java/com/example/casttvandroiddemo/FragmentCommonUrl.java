package com.example.casttvandroiddemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentCommonUrl extends Fragment {
    private View view;
    FragmentManager manager;
    FragmentTransaction transaction;
    FragmentWeb fragmentWeb = null;

    private OnPageLoadedListener onPageLoadedListener;

    public interface OnPageLoadedListener {
        void onPageLoader(String url);
    }

    public void setOnPageLoadedListener(OnPageLoadedListener onPageLoadedListener) {
        this.onPageLoadedListener = onPageLoadedListener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        manager = getFragmentManager();
        transaction = manager.beginTransaction();
        view.findViewById(R.id.btn_keyToYoutube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.youtube.com/";
                if (fragmentWeb != null) {
                    transaction.remove(fragmentWeb);
                }
                fragmentWeb = new FragmentWeb();
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                fragmentWeb.setArguments(bundle);
                transaction.add(R.id.containerInternet, fragmentWeb);
                transaction.commit();
                onPageLoadedListener.onPageLoader(url);
            }
        });

        view.findViewById(R.id.btn_keyToBiliBili).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://m.bilibili.com/";
                if (fragmentWeb != null) {
                    transaction.remove(fragmentWeb);
                }
                fragmentWeb = new FragmentWeb();
                fragmentWeb.setOnPageLoadedListener(new FragmentWeb.OnPageLoadedListener() {
                    @Override
                    public void onPageLoaded(String url) {

                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                fragmentWeb.setArguments(bundle);
                transaction.add(R.id.containerInternet, fragmentWeb);
                transaction.commit();
                onPageLoadedListener.onPageLoader(url);
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return view = inflater.inflate(R.layout.fragment_common_urls, container, false);
    }
}
