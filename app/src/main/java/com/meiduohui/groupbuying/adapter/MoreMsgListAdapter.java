package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.ShopInfoBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/12/25.
 */

public class MoreMsgListAdapter extends RecyclerView.Adapter<MoreMsgListAdapter.ViewHolder> {

    private Context mContext;
    private List<ShopInfoBean.MessageMoreBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ① 创建Adapter
    public MoreMsgListAdapter(Context context, List<ShopInfoBean.MessageMoreBean> list) {
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

        Glide.with(mContext)
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                //                .load(mList.get(position).getVideo())
                .load("https://manhua.qpic.cn/vertical/0/07_22_36_afe651da2ab940d0e257a1ec894bd992_1504795010150.jpg/420")
                .into(holder.mIvImg);
        holder.mTvTitle.setText(mList.get(position).getTitle());
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
