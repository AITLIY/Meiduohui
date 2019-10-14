package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.ShopInfoBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/12/25.
 */

public class GeneralCouponListAdapter extends RecyclerView.Adapter<GeneralCouponListAdapter.ViewHolder> {

    private Context mContext;
    private List<ShopInfoBean.MInfoBean.SQuanInfoBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ① 创建Adapter
    public GeneralCouponListAdapter(Context context, List<ShopInfoBean.MInfoBean.SQuanInfoBean> list) {
        mContext = context;
        mList = list;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public GeneralCouponListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_general_coupon, parent, false);
        return new GeneralCouponListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GeneralCouponListAdapter.ViewHolder holder, final int position) {

        holder.tv_q_title.setText(mList.get(position).getQ_content());
        holder.tv_have_quan.setOnClickListener(new View.OnClickListener() {
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

        @BindView(R.id.tv_q_title)
        TextView tv_q_title;
        @BindView(R.id.tv_have_quan)
        TextView tv_have_quan;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }
}
