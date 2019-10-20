package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.bean.HistoryBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ALIY on 2018/12/16 0016.
 */

public class HistoryListAdapter extends BaseAdapter {

    private Context mContext;
    private List<HistoryBean> mList;

    public HistoryListAdapter(Context context, List<HistoryBean> list) {
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

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext)
                .load(mList.get(position).getShop_img())
                //                .error(R.drawable.icon_tab_mine_0)
                .into(holder.mCivShopImg);

        Glide.with(mContext)
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .load(mList.get(position).getM_img())
                //                .error(R.drawable.icon_tab_mine_0)
                .into(holder.mIvMImg);

        holder.mTvShopName.setText(mList.get(position).getShop_name());
        holder.mTvMTitle.setText(mList.get(position).getTitle());
        holder.mTvTime.setText(mList.get(position).getTime());
        holder.mTvMPrice.setText(mList.get(position).getM_price());
        holder.mTvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.mTvOldPrice.setText("¥ "+mList.get(position).getM_old_price());

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.civ_shop_img)
        CircleImageView mCivShopImg;
        @BindView(R.id.tv_shop_name)
        TextView mTvShopName;
        @BindView(R.id.iv_m_img)
        ImageView mIvMImg;
        @BindView(R.id.tv_m_title)
        TextView mTvMTitle;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.tv_m_price)
        TextView mTvMPrice;
        @BindView(R.id.tv_old_price)
        TextView mTvOldPrice;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
