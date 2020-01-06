package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.NiceImageView;
import com.meiduohui.groupbuying.bean.PublishBean;
import com.meiduohui.groupbuying.commons.CommonParameters;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublishListAdapter extends RecyclerView.Adapter<PublishListAdapter.ViewHolder> {

    private Context mContext;
    private List<PublishBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ① 创建Adapter
    public PublishListAdapter(Context context, List<PublishBean> list) {
        mContext = context;
        mList = list;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public PublishListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_message, parent, false);
        return new PublishListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PublishListAdapter.ViewHolder holder, final int position) {

        holder.mTvTitle.setText(mList.get(position).getTitle());

        if (mList.get(position).getState().equals("1")) {

            if (mList.get(position).getQuan_count() > 0) {
                holder.mTvQTitle.setVisibility(View.VISIBLE);
                holder.mTvQuanCount.setText("剩余券：" + mList.get(position).getQuan_count() + "张");
            }else {
                holder.mTvQTitle.setVisibility(View.GONE);
            }
            holder.tv_stale_dated.setVisibility(View.GONE);

        } else {
            holder.mTvQTitle.setVisibility(View.GONE);
            holder.tv_stale_dated.setVisibility(View.VISIBLE);
        }


        holder.mTvQTitle.setText(mList.get(position).getQ_title());
        holder.mTvIntro.setText(mList.get(position).getIntro());
        holder.mTvMPrice.setText(mList.get(position).getM_price());
        holder.mTvMOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.mTvMOldPrice.setText("¥ "+mList.get(position).getM_old_price());
        holder.mRlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });

        String url = mList.get(position).getVideo();

        if (!TextUtils.isEmpty(url)) {
            url = url + CommonParameters.VIDEO_END;
        } else {
            List<String> urls = mList.get(position).getImg();
            url = urls.get(0);
        }

        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(holder.mIvImg);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //② 创建ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_stale_dated)
        TextView tv_stale_dated;
        @BindView(R.id.iv_img)
        NiceImageView mIvImg;
        @BindView(R.id.rl_item)
        RelativeLayout mRlItem;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.tv_q_title)
        TextView mTvQTitle;
        @BindView(R.id.tv_intro)
        TextView mTvIntro;
        @BindView(R.id.tv_m_price)
        TextView mTvMPrice;
        @BindView(R.id.tv_m_old_price)
        TextView mTvMOldPrice;
        @BindView(R.id.tv_quan_count)
        TextView mTvQuanCount;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
