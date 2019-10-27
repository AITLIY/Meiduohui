package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.ShopCouponBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class ShopCouponListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ShopCouponBean> mList;

    public ShopCouponListAdapter(Context context, ArrayList<ShopCouponBean> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_shop_coupon, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String q_type = mList.get(position).getQ_type();
        String s_type = "优惠券";
        if (q_type.equals("1")) {
            holder.mTvQPrice.setText("¥" + mList.get(position).getQ_price());
            s_type = "代金券";

        } else if (q_type.equals("2")) {

            double discount = Double.parseDouble(mList.get(position).getQ_price()) * 10;
            holder.mTvQPrice.setText( discount + "折");
            s_type = "折扣券";

        } else if (q_type.equals("3")) {
            holder.mTvQPrice.setText("¥" + mList.get(position).getQ_price());
            s_type = "会员券";
        }
        holder.mTvQType.setText(s_type);
        holder.mTvQContent.setText(mList.get(position).getQ_content());

//        String startTime = mList.get(position).get();
//        String endTime = mList.get(position).get();
//        if (startTime == null)
//            startTime = "0";
//        if (endTime == null)
//            endTime = "0";
//
//        holder.mTvUseTime.setText("有效时间：" + TimeUtils.LongToString(Long.parseLong(startTime), "yyyy.MM.dd")
//                + " - " + TimeUtils.LongToString(Long.parseLong(endTime), "yyyy.MM.dd"));
        holder.mTvSYQuanCount.setText("已抢数量：" + mList.get(position).getS_y_quan_count());
        holder.mTvSWQuanCount.setText("未抢数量：" + mList.get(position).getS_w_quan_count());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_q_price)
        TextView mTvQPrice;
        @BindView(R.id.tv_q_type)
        TextView mTvQType;
        @BindView(R.id.tv_q_content)
        TextView mTvQContent;
        @BindView(R.id.tv_use_time)
        TextView mTvUseTime;
        @BindView(R.id.tv_s_y_quan_count)
        TextView mTvSYQuanCount;
        @BindView(R.id.tv_s_w_quan_count)
        TextView mTvSWQuanCount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
