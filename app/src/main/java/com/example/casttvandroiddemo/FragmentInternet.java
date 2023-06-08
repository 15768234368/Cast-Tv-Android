package com.example.casttvandroiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentInternet extends Fragment {
    private static final String TAG = "FragmentInternet";
    private View view;
    private SearchView searchView;
    private FragmentCommonUrl fragmentCommonUrl;
    private FragmentManager manager;
    private ImageView iv_setting;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        selectTab0();
    }

    private void selectTab0() {
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragment(transaction);

        if (fragmentCommonUrl == null) {
            fragmentCommonUrl = new FragmentCommonUrl();
            transaction.add(R.id.containerInternet, fragmentCommonUrl);
        } else {
            transaction.show(fragmentCommonUrl);
        }


        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {


        if (fragmentCommonUrl != null) {
            transaction.hide(fragmentCommonUrl);
        }
    }

    private void initView() {
        iv_setting = (ImageView) view.findViewById(R.id.iv_setting_homepage);
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
        manager = getFragmentManager();
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String url) {
                String newUrl;
                if (url.startsWith("https") || url.startsWith("http")) {
                    newUrl = url;
                }else{
                    newUrl = "https://www.google.com.hk/search?q=" + url;
                }
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", newUrl);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 移除布局监听器
        getActivity().getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(((MainActivity) getActivity()).getKeyboardLayoutListener());
        return view = inflater.inflate(R.layout.fragment_internet_tab, container, false);
    }


}
