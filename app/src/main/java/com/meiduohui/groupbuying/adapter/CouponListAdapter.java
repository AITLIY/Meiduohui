package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.CouponBean;
import com.meiduohui.groupbuying.utils.TimeUtils;

import java.text.BreakIterator;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class CouponListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<CouponBean> mList;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onUse(int position);
        void onDetail(int position);
    }

    public CouponListAdapter(Context context, ArrayList<CouponBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_coupon, parent,false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

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

        String m_id = mList.get(position).getM_id();
        String s_content;
        if (m_id.equals("0"))
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

        final String state = mList.get(position).getQ_state();

        switch (state) {

            case "0":
                holder.tv_use.setVisibility(View.VISIBLE);
                holder.tv_usend.setVisibility(View.GONE);
                holder.tv_expired.setVisibility(View.GONE);
                break;

            case "1":
                holder.tv_use.setVisibility(View.GONE);
                holder.tv_usend.setVisibility(View.VISIBLE);
                holder.tv_expired.setVisibility(View.GONE);
                break;

            case "2":
                holder.tv_use.setVisibility(View.GONE);
                holder.tv_usend.setVisibility(View.GONE);
                holder.tv_expired.setVisibility(View.VISIBLE);
                break;
        }

        holder.tv_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!state.equals("0"))
                    return;
                onItemClickListener.onUse(position);
            }
        });

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!state.equals("0"))
                    return;
                onItemClickListener.onDetail(position);
            }
        });

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.ll_item)
        LinearLayout ll_item;
        @BindView(R.id.tv_q_price)
        TextView tv_q_price;
        @BindView(R.id.tv_q_type)
        TextView tv_q_type;
        @BindView(R.id.tv_shop_name)
        TextView tv_shop_name;
        @BindView(R.id.tv_content)
        TextView tv_content;
        @BindView(R.id.tv_use_time)
        TextView tv_use_time;
        @BindView(R.id.tv_use)
        TextView tv_use;
        @BindView(R.id.tv_usend)
        TextView tv_usend;
        @BindView(R.id.tv_expired)
        TextView tv_expired;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
