package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.WithdrawalBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/12/25.
 */

public class WithdrawalListAdapter extends RecyclerView.Adapter<WithdrawalListAdapter.ViewHolder> {

    private Context mContext;
    private List<WithdrawalBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ① 创建Adapter
    public WithdrawalListAdapter(Context context, List<WithdrawalBean> list) {
        mContext = context;
        mList = list;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdrawal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.mTvXingming.setText(mList.get(position).getXingming());
        holder.mTvTxMoney.setText(mList.get(position).getTx_money());
        holder.mTvTxTime.setText(mList.get(position).getTx_time());
        holder.mTvStateIntro.setText(mList.get(position).getState_intro());

        holder.mLlItem.setOnClickListener(new View.OnClickListener() {
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

        @BindView(R.id.tv_xingming)
        TextView mTvXingming;
        @BindView(R.id.tv_tx_money)
        TextView mTvTxMoney;
        @BindView(R.id.tv_tx_time)
        TextView mTvTxTime;
        @BindView(R.id.tv_state_intro)
        TextView mTvStateIntro;
        @BindView(R.id.ll_item)
        LinearLayout mLlItem;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
