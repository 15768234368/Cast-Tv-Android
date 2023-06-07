package com.example.casttvandroiddemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casttvandroiddemo.R;
import com.example.casttvandroiddemo.WebViewActivity;
import com.example.casttvandroiddemo.bean.InternetHistoryBean;

import java.util.List;

public class InternetHistoryAdapter extends RecyclerView.Adapter<InternetHistoryAdapter.MyHolder> {
    private List<InternetHistoryBean> mData;
    private Context mContext;

    public InternetHistoryAdapter(List<InternetHistoryBean> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_history_internet, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        InternetHistoryBean bean = mData.get(position);
        holder.netTitle.setText(bean.getTitle());
        holder.netUrl.setText(bean.getUrl());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra("url", bean.getUrl());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private TextView netTitle;
        private TextView netUrl;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            netTitle = (TextView) itemView.findViewById(R.id.tv_historyItem_title);
            netUrl = (TextView) itemView.findViewById(R.id.tv_historyItem_url);
        }
    }
}
