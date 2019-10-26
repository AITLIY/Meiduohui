package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.MessageInfoBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.utils.PxUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/12/25.
 */

public class MoreMsgListAdapter extends RecyclerView.Adapter<MoreMsgListAdapter.ViewHolder> {

    private Context mContext;
    private List<MessageInfoBean.MessageMoreBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ① 创建Adapter
    public MoreMsgListAdapter(Context context, List<MessageInfoBean.MessageMoreBean> list) {
        mContext = context;
        mList = list;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.mTvTitle.setText(mList.get(position).getTitle());

        if (TextUtils.isEmpty(mList.get(position).getQ_title()))
            holder.mTvQTitle.setVisibility(View.GONE);

        holder.mTvQTitle.setText(mList.get(position).getQ_title());
        holder.mTvIntro.setText(mList.get(position).getIntro());
        holder.mTvMPrice.setText(mList.get(position).getM_price());
        holder.mTvMOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.mTvMOldPrice.setText("¥ "+mList.get(position).getM_old_price());
        holder.mTvQuanCount.setText("剩余券：" + mList.get(position).getQuan_count()+"张");
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
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(PxUtils.dip2px(mContext,5))))
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

        @BindView(R.id.iv_img)
        ImageView mIvImg;
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
