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

public class InternetHistoryList extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "InternetHistoryList";
    private ImageView iv_back;
    private SearchView sv_search;
    private TextView tv_cancel;
    private RecyclerView rv_historyList;
    private InternetHistoryAdapter adapter;
    private List<InternetHistoryBean> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_history_list);
        mData = new ArrayList<>();
        loadData();
        initView();
    }

    private void loadData() {
        mData.clear();
        InternetHistoryHelper helper = new InternetHistoryHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sortOrder = "timestamp DESC";
        Cursor cursor = db.query(InternetHistoryHelper.TABLE_HISTORY, null, null, null, null, null, sortOrder);
        while(cursor.moveToNext()){
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String itemTitle = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String itemUrl = cursor.getString(cursor.getColumnIndexOrThrow("url"));
            String itemTimestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
            mData.add(new InternetHistoryBean(itemId, itemTitle, itemUrl, itemTimestamp));
        }
        cursor.close();
        db.close();
        helper.close();
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back_internetHistory);
        sv_search = (SearchView) findViewById(R.id.sv_search_historyList);
        sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                tv_cancel.bringToFront();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // 获取SearchView的布局参数

                return false;
            }
        });
        sv_search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) sv_search.getLayoutParams();
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    //光标获取焦点
                    layoutParams.width = dpToPx(280); // 初始宽度（单位为像素
                    Log.d(TAG, "onFocusChange: " + "hasFocus");
                }else{
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