package com.example.casttvandroiddemo;

import static com.example.casttvandroiddemo.FragmentRemoteControl.RokuLocationUrl;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.casttvandroiddemo.utils.StringUtils;

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
                if (StringUtils.containsChinese(url)) {
                    newUrl = "https://www.google.com/search?q=" + url;
                    Log.d(TAG, "onQueryTextSubmit: " + newUrl);
                }else{
                    if (url.startsWith("https://") || url.startsWith("http://")) {
                        newUrl = url;
                    } else if (url.startsWith("www")) {
                        newUrl = "https://" + url;
                    } else {
                        newUrl = "https://www.google.com/search?q=" + url;
                    }
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
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        //获取TextView
        TextView textView = (TextView) searchView.findViewById(id);//设置字体大小为14sp
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//14sp
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // 移除布局监听器
        return view = inflater.inflate(R.layout.fragment_internet_tab, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + RokuLocationUrl);
    }

    @Override
    public void onPause() {
        super.onPause();
        searchView.clearFocus();
    }
}
