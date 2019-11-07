package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.UI.views.NiceImageView;
import com.meiduohui.groupbuying.bean.HistoryBean;
import com.meiduohui.groupbuying.commons.CommonParameters;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ALIY on 2018/12/16 0016.
 */

public class HistoryListAdapter extends BaseAdapter {

    private Context mContext;
    private List<HistoryBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClic(int position);
    }

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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
                .load(url)
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(holder.mIvMImg);

        holder.mTvShopName.setText(mList.get(position).getShop_name());
        holder.mTvMTitle.setText(mList.get(position).getTitle());
        holder.mTvTime.setText(mList.get(position).getTime());
        holder.mTvMPrice.setText(mList.get(position).getM_price());
        holder.mTvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.mTvOldPrice.setText("¥ "+mList.get(position).getM_old_price());

        holder.mLlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClic(position);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.ll_item)
        LinearLayout mLlItem;
        @BindView(R.id.civ_shop_img)
        CircleImageView mCivShopImg;
        @BindView(R.id.tv_shop_name)
        TextView mTvShopName;
        @BindView(R.id.iv_m_img)
        NiceImageView mIvMImg;
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
