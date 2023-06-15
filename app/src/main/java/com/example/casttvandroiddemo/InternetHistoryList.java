package com.example.casttvandroiddemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.casttvandroiddemo.adapter.InternetHistoryAdapter;
import com.example.casttvandroiddemo.bean.InternetHistoryBean;
import com.example.casttvandroiddemo.helper.InternetHistoryHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InternetHistoryList extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "InternetHistoryList";
    private ImageView iv_back, iv_contentEmptyBg;
    private SearchView sv_search;
    private TextView tv_cancel, tv_contentEmptyTitle;
    private RecyclerView rv_historyList;
    private InternetHistoryAdapter adapter;
    private List<InternetHistoryBean> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_history_list);
        mData = new ArrayList<>();
        initView();
        loadData();
    }

    private void loadData() {
        mData.clear();
        InternetHistoryHelper helper = new InternetHistoryHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sortOrder = "timestamp DESC";
        Cursor cursor = db.query(InternetHistoryHelper.TABLE_HISTORY, null, null, null, null, null, sortOrder);
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String itemTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String itemUrl = cursor.getString(cursor.getColumnIndexOrThrow("url"));
            String itemTimestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
            mData.add(new InternetHistoryBean(itemId, itemTitle, itemUrl, itemTimestamp));
        }
        cursor.close();
        db.close();
        helper.close();
        if (mData.size() <= 0) {
            showEmptyListBg();
        }else{
            closeEmptyListBg();
        }
        adapter.notifyDataSetChanged();
    }

    private void showEmptyListBg() {
        iv_contentEmptyBg.setVisibility(View.VISIBLE);
        tv_contentEmptyTitle.setVisibility(View.VISIBLE);
    }

    private void closeEmptyListBg() {
        iv_contentEmptyBg.setVisibility(View.INVISIBLE);
        tv_contentEmptyTitle.setVisibility(View.INVISIBLE);
    }

    private void initView() {
        iv_contentEmptyBg = (ImageView) findViewById(R.id.iv_content_is_empty_bg);
        tv_contentEmptyTitle = (TextView) findViewById(R.id.tv_content_is_empty_title);

        iv_back = (ImageView) findViewById(R.id.iv_back_internetHistory);
        sv_search = (SearchView) findViewById(R.id.sv_search_historyList);
        sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                tv_cancel.bringToFront();
                searchKey(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // 获取SearchView的布局参数
                searchKey(transferToCapitalization(s));
                return false;
            }
        });
        sv_search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) sv_search.getLayoutParams();

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    //光标获取焦点
                    layoutParams.width = dpToPx(270); // 初始宽度（单位为像素
                    Log.d(TAG, "onFocusChange: " + "hasFocus");
                } else {
                    //光标失去焦点
                    layoutParams.width = dpToPx(320); // 更宽的宽度（单位为像素
                    Log.d(TAG, "onFocusChange: " + "noFocus");
                }
                // 应用新的布局参数
                sv_search.setLayoutParams(layoutParams);
                sv_search.bringToFront();
            }
        });
        sv_search.bringToFront();

        tv_cancel = (TextView) findViewById(R.id.tv_cancel_historyList);
        rv_historyList = (RecyclerView) findViewById(R.id.rv_historyList_internetHistory);

        iv_back.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        adapter = new InternetHistoryAdapter(mData, this);
        rv_historyList.setLayoutManager(new LinearLayoutManager(this));
        rv_historyList.setAdapter(adapter);
    }

    private String transferToCapitalization(String s) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < s.length(); ++i){
            if(s.charAt(i) >= 'A' && s.charAt(i) <= 'Z'){
                result.append((char) (s.charAt(i) + 32));
            } else {
                result.append(s.charAt(i));
            }
        }
        return result.toString();
    }


    private void searchKey(String s) {
        Log.d(TAG, "searchKey: " + s);
        if (s.equals("")) {
            loadData();
        } else {
            Pattern pattern = Pattern.compile(".*" + s + ".*");
            mData.clear();
            InternetHistoryHelper helper = new InternetHistoryHelper(this);
            SQLiteDatabase db = helper.getReadableDatabase();
            String sortOrder = "timestamp DESC";
            Cursor cursor = db.query(InternetHistoryHelper.TABLE_HISTORY, null, null, null, null, null, sortOrder);
            while (cursor.moveToNext()) {
                long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String itemTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String itemUrl = cursor.getString(cursor.getColumnIndexOrThrow("url"));
                String itemTimestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                Matcher matcher = pattern.matcher(itemTitle);
                if (matcher.find())
                    mData.add(new InternetHistoryBean(itemId, itemTitle, itemUrl, itemTimestamp));
            }
            cursor.close();
            db.close();
            helper.close();
        }
        adapter.notifyDataSetChanged();
        if(mData.size() <= 0){
            showEmptyListBg();
        }else{
            closeEmptyListBg();
        }
    }

    public int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_internetHistory:
                finish();
                break;
            case R.id.tv_cancel_historyList:
                sv_search.clearFocus();
                tv_cancel.requestFocus();
                // 隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;

        }
    }
}