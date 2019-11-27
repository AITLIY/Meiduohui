package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.AddressBean;
import com.meiduohui.groupbuying.bean.MessageInfoBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/12/25.
 */

public class AdressListAdapter extends RecyclerView.Adapter<AdressListAdapter.ViewHolder> {

    private Context mContext;
    private List<AddressBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ① 创建Adapter
    public AdressListAdapter(Context context, List<AddressBean> list) {
        mContext = context;
        mList = list;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public AdressListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adrsss, parent, false);
        return new AdressListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdressListAdapter.ViewHolder holder, final int position) {
        LogUtils.i("AdressListAdapter onBindViewHolder position " + position);
        holder.tv_county_name.setText(mList.get(position).getName());

        holder.ll_county_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    //② 创建ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_county_name)
        LinearLayout ll_county_name;
        @BindView(R.id.tv_county_name)
        TextView tv_county_name;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }
}
