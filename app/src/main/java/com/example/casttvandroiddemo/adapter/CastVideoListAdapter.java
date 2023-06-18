package com.example.casttvandroiddemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casttvandroiddemo.R;
import com.example.casttvandroiddemo.WebViewActivity;
import com.example.casttvandroiddemo.bean.CastVideoBean;
import com.example.casttvandroiddemo.utils.ViewUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CastVideoListAdapter extends RecyclerView.Adapter<CastVideoListAdapter.MyHolder> {
    private static final String TAG = "CastVideoListAdapter";
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private List<CastVideoBean> mData = WebViewActivity.mVideoBean;
    private Context mContext;
    private Activity mActivity;

    public CastVideoListAdapter(List<CastVideoBean> mData, Context mContext, Activity mActivity) {
        this.mData = mData;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_cast_video_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + position);
        CastVideoBean bean = mData.get(mData.size() - position - 1);
        holder.tv_videoTitle.setText(bean.getVideoTitle());
        getBitmapFromUrl(holder, bean.getVideoImageUrl());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.OnItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private ImageView iv_videoImage, iv_castToTv;
        private TextView tv_videoTitle;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            iv_videoImage = (ImageView) itemView.findViewById(R.id.iv_castItem_videoImage);
            iv_castToTv = (ImageView) itemView.findViewById(R.id.iv_castItem_castToTv);
            tv_videoTitle = (TextView) itemView.findViewById(R.id.tv_castItem_videoTitle);
        }
    }

    public void getBitmapFromUrl(MyHolder holder, String videoPicUrl) {
        // 创建OkHttpClient实例
        OkHttpClient client = new OkHttpClient();

// 创建请求对象
        Request request = new Request.Builder()
                .url(videoPicUrl)
                .build();

// 发送异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    // 获取响应的数据流
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        // 将响应的数据流转换为字节数组
                        byte[] imageBytes;
                        try {
                            imageBytes = responseBody.bytes();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                holder.iv_videoImage.setImageBitmap(ViewUtils.getRoundedCornerBitmap(bitmap, 100));
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
