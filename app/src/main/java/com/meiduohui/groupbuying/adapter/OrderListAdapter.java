package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.bean.OrderBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.utils.PxUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class OrderListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<OrderBean> mList;
    private boolean mIsShop;

    public void setShop(boolean shop) {
        mIsShop = shop;
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onCancel(int position);

        void onPay(int position);

        void onDelete(int position);

        void onUse(int position);
    }

    public OrderListAdapter(Context context, ArrayList<OrderBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_order_message, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext)
                .load(mList.get(position).getShop_img())
                .apply(new RequestOptions().error(R.drawable.icon_tab_usericon))
                .into(holder.mCivShopImg);

        String url = mList.get(position).getM_video();

        if (!TextUtils.isEmpty(url)) {
            url = url + CommonParameters.VIDEO_END;
        } else {
            String urls = mList.get(position).getM_img();
            url = urls.split(",")[0];
        }

        Glide.with(mContext)
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(PxUtils.dip2px(mContext,5))))
                .load(url)
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(holder.mIvMImg);

        holder.mTvShopName.setText(mList.get(position).getShop_name());
        holder.mTvOrderNum.setText("订单号：" + mList.get(position).getOrder_num());
        holder.mTvStateIntro.setText(mList.get(position).getState_intro());
        holder.mTvMTitle.setText(mList.get(position).getM_title());
        holder.mTvTime.setText(mList.get(position).getTime());
        holder.mTvOrderPrice.setText(mList.get(position).getOrder_price());
        holder.mTvMPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.mTvMPrice.setText("¥ " + mList.get(position).getM_price());

        final String state = mList.get(position).getState();

        LogUtils.i("OrderListAdapter" + "getView state " + state);

        switch (state) {

            case "0":
                holder.mTvDelOrder.setVisibility(View.GONE);
                holder.mTvCancelOrder.setVisibility(View.VISIBLE);
                holder.mTvGoPay.setVisibility(View.VISIBLE);
                holder.mTvUse.setVisibility(View.GONE);
                break;

            case "1":
                holder.mTvDelOrder.setVisibility(View.GONE);
                holder.mTvCancelOrder.setVisibility(View.GONE);
                holder.mTvGoPay.setVisibility(View.GONE);
                holder.mTvUse.setVisibility(View.VISIBLE);
                break;

            case "2":
                holder.mTvDelOrder.setVisibility(View.GONE);
                holder.mTvCancelOrder.setVisibility(View.GONE);
                holder.mTvGoPay.setVisibility(View.GONE);
                holder.mTvUse.setVisibility(View.GONE);
                break;

            case "3":
                holder.mTvDelOrder.setVisibility(View.VISIBLE);
                holder.mTvCancelOrder.setVisibility(View.GONE);
                holder.mTvGoPay.setVisibility(View.GONE);
                holder.mTvUse.setVisibility(View.GONE);
                break;
        }

        if (mIsShop)
            holder.ll_option.setVisibility(View.GONE);
        else
            holder.ll_option.setVisibility(View.VISIBLE);

        holder.mTvDelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onDelete(position);
            }
        });
        holder.mTvCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onCancel(position);
            }
        });
        holder.mTvGoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onPay(position);
            }
        });
        holder.mTvUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onUse(position);
            }
        });


        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.civ_shop_img)
        CircleImageView mCivShopImg;
        @BindView(R.id.tv_shop_name)
        TextView mTvShopName;
        @BindView(R.id.tv_order_num)
        TextView mTvOrderNum;
        @BindView(R.id.tv_state_intro)
        TextView mTvStateIntro;
        @BindView(R.id.iv_m_img)
        ImageView mIvMImg;
        @BindView(R.id.tv_m_title)
        TextView mTvMTitle;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.tv_order_price)
        TextView mTvOrderPrice;
        @BindView(R.id.tv_m_price)
        TextView mTvMPrice;
        @BindView(R.id.ll_option)
        LinearLayout ll_option;
        @BindView(R.id.tv_del_order)
        TextView mTvDelOrder;
        @BindView(R.id.tv_cancel_order)
        TextView mTvCancelOrder;
        @BindView(R.id.tv_go_pay)
        TextView mTvGoPay;
        @BindView(R.id.tv_use)
        TextView mTvUse;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
