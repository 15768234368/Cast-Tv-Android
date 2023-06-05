package com.example.casttvandroiddemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casttvandroiddemo.R;
import com.example.casttvandroiddemo.bean.DeviceBean;

import java.util.List;

public class AddDeviceListAdapter extends RecyclerView.Adapter<AddDeviceListAdapter.MyHolder> {
    private Context context;
    private List<DeviceBean> mData;
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void OnItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AddDeviceListAdapter(Context context, List<DeviceBean> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_add_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
        DeviceBean bean = mData.get(position);
        holder.tv_deviceName.setText(bean.getUserDeviceName());
        holder.tv_deviceLocation.setText(bean.getUserDeviceLocation());
        holder.tv_deviceIpAddress.setText("ip地址:" + bean.getUserDeviceIpAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.OnItemClick(view, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_deviceName, tv_deviceLocation, tv_deviceIpAddress;
        private ImageView iv_next;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_deviceName = (TextView) itemView.findViewById(R.id.tv_item_user_deviceName);
            tv_deviceLocation = (TextView) itemView.findViewById(R.id.tv_item_user_deviceLocation);
            tv_deviceIpAddress = (TextView) itemView.findViewById(R.id.tv_item_user_deviceIpAddress);
            iv_next = (ImageView) itemView.findViewById(R.id.iv_next_addDevice);
        }
    }
}
