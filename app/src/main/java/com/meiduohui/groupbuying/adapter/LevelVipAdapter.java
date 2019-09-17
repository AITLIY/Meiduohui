package com.meiduohui.groupbuying.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.IndexBean;

import java.util.List;

/**
 * Created by Administrator on 2018/12/25.
 */

public class LevelVipAdapter extends RecyclerView.Adapter<LevelVipAdapter.ViewHolder> {

    // ① 创建Adapter
    private List<IndexBean.DataBean.MessageInfoBean> mDatas;
//    private JoinVipInterface mJoinVip;
    public LevelVipAdapter(List<IndexBean.DataBean.MessageInfoBean> data) {
        mDatas = data;
//        mJoinVip = joinVip;
    }

    //② 创建ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
//        public final TextView level_name;
//        public final TextView level_price;
//        public final TextView join_tv;

        public ViewHolder(View v) {
            super(v);
//            level_name = v.findViewById(R.id.level_name);
//            level_price = v.findViewById(R.id.level_price);
//            join_tv = v.findViewById(R.id.join_tv);
        }
    }

    //③ 在Adapter中实现3个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
//        holder.level_name.setText(mDatas.get(position).getLevel_name()+"["+mDatas.get(position).getLevel_validity()+"天]");
//        holder.level_price.setText(mDatas.get(position).getLevel_price()+"元");
//        holder.join_tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                mJoinVip.onPayVip(mDatas.get(position));
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
