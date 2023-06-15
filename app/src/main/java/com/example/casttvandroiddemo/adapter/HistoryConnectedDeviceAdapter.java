package com.example.casttvandroiddemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casttvandroiddemo.bean.DeviceBean;
import com.example.casttvandroiddemo.R;

import java.util.List;

public class HistoryConnectedDeviceAdapter extends RecyclerView.Adapter<HistoryConnectedDeviceAdapter.MyViewHolder> {
    private List<DeviceBean> mData;
    private Context context;
    private boolean isDelete = false;
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public HistoryConnectedDeviceAdapter(List<DeviceBean> mData, Context context) {
        this.mData = mData;
        this.context = context;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_common_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.OnItemClick(view, position);
            }
        });
        DeviceBean bean = mData.get(position);

        SpannableString spannableName = new SpannableString(bean.getUserDeviceName());
        SpannableString spannableLocation = new SpannableString(bean.getUserDeviceLocation());
        ForegroundColorSpan colorSpan_name;
        ForegroundColorSpan colorSpan_Location;
        if (bean.getIsOnline() == 0) {
            colorSpan_name = new ForegroundColorSpan(0x4D202020);
            colorSpan_Location = new ForegroundColorSpan(0x4D999999);
            holder.iv_device_icon.setImageResource(R.mipmap.device_icon_not_online_device_manage);
            holder.rl_back.setBackgroundResource(R.drawable.shape_common_device_unselected);

        } else if (bean.getIsOnline() == 1) {
            colorSpan_name = new ForegroundColorSpan(0xFFFFFFFF);
            colorSpan_Location = new ForegroundColorSpan(0xFFF7F7F7);
            holder.iv_device_icon.setImageResource(R.mipmap.device_icon_connected);
            holder.iv_connected_icon.setVisibility(View.VISIBLE);
            holder.iv_connected_icon.setImageResource(R.mipmap.connected_device_manage);
            holder.rl_back.setBackgroundResource(R.drawable.shape_common_device_selected);
        } else {
            colorSpan_name = new ForegroundColorSpan(0xFF202020);
            colorSpan_Location = new ForegroundColorSpan(0xFF999999);
            holder.iv_device_icon.setImageResource(R.mipmap.device_icon);
            holder.rl_back.setBackgroundResource(R.drawable.shape_common_device_unselected);
        }
        spannableName.setSpan(colorSpan_name, 0, spannableName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableLocation.setSpan(colorSpan_Location, 0, spannableLocation.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.tv_userDeviceName.setText(spannableName);
        holder.tv_userDeviceLocation.setText(spannableLocation);
        if (isDelete) {
            //增加删除的逻辑,平常的bean.getIsDelete都是为0，当值为1的时候，删除框出现，当值为2的时候，选中删除框
            if (bean.getIsDelete() == 0) {
                holder.itemView.setEnabled(true);
                holder.iv_connected_icon.setVisibility(View.INVISIBLE);
            } else if (bean.getIsDelete() == 1) {
                holder.iv_connected_icon.setVisibility(View.VISIBLE);
                holder.iv_connected_icon.setImageResource(R.mipmap.unselect_delete_device);
            } else {
                holder.iv_connected_icon.setVisibility(View.VISIBLE);
                holder.iv_connected_icon.setImageResource(R.mipmap.select_delete_device);
            }
            //统一删除的时候样式
            colorSpan_name = new ForegroundColorSpan(0xFF202020);
            colorSpan_Location = new ForegroundColorSpan(0xFF999999);
            holder.iv_device_icon.setImageResource(R.mipmap.device_icon);
            holder.rl_back.setBackgroundResource(R.drawable.shape_common_device_unselected);
            spannableName.setSpan(colorSpan_name, 0, spannableName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableLocation.setSpan(colorSpan_Location, 0, spannableLocation.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.tv_userDeviceName.setText(spannableName);
            holder.tv_userDeviceLocation.setText(spannableLocation);
        } else {
            if (bean.getIsOnline() == 1) {
                holder.iv_connected_icon.setVisibility(View.VISIBLE);
                holder.iv_connected_icon.setImageResource(R.mipmap.connected_device_manage);
                holder.rl_back.setBackgroundResource(R.drawable.shape_common_device_selected);
            } else {
                holder.iv_connected_icon.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_userDeviceName, tv_userDeviceLocation;
        ImageView iv_device_icon, iv_connected_icon;
        RelativeLayout rl_back;

        public MyViewHolder(@NonNull View view) {
            super(view);
            tv_userDeviceName = (TextView) view.findViewById(R.id.tv_user_device_name);
            tv_userDeviceLocation = (TextView) view.findViewById(R.id.tv_user_device_location);
            iv_device_icon = (ImageView) view.findViewById(R.id.iv_deviceIcon_deviceManage);
            iv_connected_icon = (ImageView) view.findViewById(R.id.iv_deviceConnected_deviceManage);
            rl_back = (RelativeLayout) view.findViewById(R.id.rl_item_common_device_bg);
        }
    }
}
