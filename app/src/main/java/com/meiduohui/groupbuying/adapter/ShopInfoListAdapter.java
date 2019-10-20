package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.bean.ShopInfoBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ALIY on 2018/12/16 0016.
 */

public class ShopInfoListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ShopInfoBean> mList;

    public ShopInfoListAdapter(Context context, List<ShopInfoBean> list) {
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_collect_shop, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        Glide.with(mContext)
                .load(mList.get(position).getShop_img())
                .into(holder.mCivShopImg);
        holder.mTvShopName.setText(mList.get(position).getShop_name());
        holder.mTvTime.setText(mList.get(position).getTime());


        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.civ_shop_img)
        CircleImageView mCivShopImg;
        @BindView(R.id.tv_shop_name)
        TextView mTvShopName;
        @BindView(R.id.tv_time)
        TextView mTvTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
