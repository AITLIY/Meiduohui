package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.NewOrderBean;
import com.meiduohui.groupbuying.utils.TimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/12/25.
 */

public class SelectCouponListAdapter extends RecyclerView.Adapter<SelectCouponListAdapter.ViewHolder> {

    private Context mContext;
    private List<NewOrderBean.QuanInfoBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ① 创建Adapter
    public SelectCouponListAdapter(Context context, List<NewOrderBean.QuanInfoBean> list) {
        mContext = context;
        mList = list;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        String q_type = mList.get(position).getQ_type();
        String s_type = "优惠券";
        if (q_type.equals("1")) {
            holder.tv_q_price.setText("¥" + mList.get(position).getQ_price());
            s_type = "代金券";

        } else if (q_type.equals("2")) {
            double discount = Double.parseDouble(mList.get(position).getQ_price()) * 10;
            holder.tv_q_price.setText( discount + "折");
            s_type = "折扣券";

        } else if (q_type.equals("3")) {
            holder.tv_q_price.setText("¥" + mList.get(position).getQ_price());
            s_type = "会员券";
        }
        holder.tv_q_type.setText(s_type);

        String q_id = mList.get(position).getM_id();
        String s_content = "商家通用券";
        if (q_id.equals("0"))
            s_content = "商家通用券";
        else
            s_content = "套餐专用券";
        holder.tv_content.setText(s_content);

        String startTime = mList.get(position).getStart_time();
        String endTime = mList.get(position).getEnd_time();
        if (startTime==null)
            startTime = "0";
        if (endTime==null)
            endTime = "0";

        holder.tv_use_time.setText("有效时间：" + TimeUtils.LongToString(Long.parseLong(startTime),"yyyy.MM.dd")
                + " - " + TimeUtils.LongToString(Long.parseLong(endTime),"yyyy.MM.dd"));

        if (!TextUtils.isEmpty(mList.get(position).getTitle()))
            holder.tv_shop_name.setText("适用于：" + mList.get(position).getTitle());
        else
            holder.tv_shop_name.setText("适用商家：" + mList.get(position).getShop_name());

        holder.rl_item.setOnClickListener(new View.OnClickListener() {
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

        @BindView(R.id.tv_q_price)
        TextView tv_q_price;
        @BindView(R.id.tv_q_type)
        TextView tv_q_type;
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.tv_use_time)
        TextView tv_use_time;
        @BindView(R.id.tv_shop_name)
        TextView tv_shop_name;
        @BindView(R.id.rl_item)
        LinearLayout rl_item;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
