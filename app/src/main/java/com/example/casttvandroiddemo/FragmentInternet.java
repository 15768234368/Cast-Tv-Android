package com.example.casttvandroiddemo;

import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentInternet extends Fragment{
    private static final String TAG = "FragmentInternet";
    private View view;
    private SearchView searchView;
    private FragmentCommonUrl fragmentCommonUrl;
    private FragmentWeb fragmentWeb;
    private FragmentManager manager;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        selectTab(0);
    }

    private void selectTab(int tabNum) {
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragment(transaction);
        switch (tabNum){
            case 0:
                if(fragmentCommonUrl == null){
                    fragmentCommonUrl = new FragmentCommonUrl();
                    transaction.add(R.id.containerInternet, fragmentCommonUrl);
                }else {
                    transaction.show(fragmentCommonUrl);
                }
                fragmentCommonUrl.setOnPageLoadedListener(new FragmentCommonUrl.OnPageLoadedListener() {
                    @Override
                    public void onPageLoader(String url) {
                        searchView.setQuery(url, false);
                    }
                });
                break;
            case 1:
                if(fragmentWeb == null){
                    fragmentWeb = new FragmentWeb();
                    transaction.add(R.id.containerInternet, fragmentWeb);
                }else{
                    transaction.show(fragmentWeb);
                }
                break;
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if(fragmentCommonUrl != null){
            transaction.hide(fragmentCommonUrl);
        }
        if(fragmentWeb != null){
            transaction.hide(fragmentWeb);
        }
    }

    private void initView() {
        manager = getFragmentManager();
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String url) {
                FragmentTransaction newTransaction = manager.beginTransaction();
                if(fragmentWeb != null){
                    newTransaction.remove(fragmentWeb);
                }
                fragmentWeb = new FragmentWeb();
                fragmentWeb.setOnPageLoadedListener(new FragmentWeb.OnPageLoadedListener() {
                    @Override
                    public void onPageLoaded(String url) {
                        searchView.setQuery(url, false);
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                Log.d(TAG, "onQueryTextSubmit: " + url);
                fragmentWeb.setArguments(bundle);
                newTransaction.add(R.id.containerInternet, fragmentWeb);
                newTransaction.commit();
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
        return view = inflater.inflate(R.layout.fragment_internet_tab, container, false);
    }


}
