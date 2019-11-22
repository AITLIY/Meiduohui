package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.InviteShopBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class InviteShopListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<InviteShopBean> mList;

    public InviteShopListAdapter(Context context, ArrayList<InviteShopBean> list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_invite_shop, parent,false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTvShopName.setText("商家：" + mList.get(position).getShop_name());
        holder.mTvTime.setText("邀请时间：" + mList.get(position).getTime());
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tv_shop_name)
        TextView mTvShopName;
        @BindView(R.id.tv_time)
        TextView mTvTime;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
