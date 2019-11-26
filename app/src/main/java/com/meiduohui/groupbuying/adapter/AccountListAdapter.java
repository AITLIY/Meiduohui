package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.RecordBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class AccountListAdapter extends BaseAdapter {

    private Context mContext;
    private List<RecordBean.RecordListBean> mList;

    public AccountListAdapter(Context context, List<RecordBean.RecordListBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_record, parent,false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTvMoneyReason.setText(mList.get(position).getMoney_reason());

        if (mList.get(position).getMoney_type().equals("+")){
            holder.mTvMoneyChange.setTextColor(mContext.getResources().getColor(R.color.orange2));
        } else
            holder.mTvMoneyChange.setTextColor(mContext.getResources().getColor(R.color.black));

        holder.mTvMoneyChange.setText(mList.get(position).getMoney_type() + mList.get(position).getMoney_change());
        holder.mTvMoneyTime.setText(mList.get(position).getMoney_time());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_money_reason)
        TextView mTvMoneyReason;
        @BindView(R.id.tv_money_change)
        TextView mTvMoneyChange;
        @BindView(R.id.tv_money_time)
        TextView mTvMoneyTime;
        @BindView(R.id.ll_item)
        LinearLayout mLlItem;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
