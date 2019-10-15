package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.IndexBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/12/25.
 */

public class MessageInfoListAdapter extends RecyclerView.Adapter<MessageInfoListAdapter.ViewHolder> {

    private Context mContext;
    private List<IndexBean.MessageInfoBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onComment(int position);
        void onZF(int position);
        void onZan(int position);
    }

    // ① 创建Adapter
    public MessageInfoListAdapter(Context context, List<IndexBean.MessageInfoBean> list) {
        mContext = context;
        mList = list;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_shop_name.setText(mList.get(position).getShop_name());
        holder.tv_m_price.setText(mList.get(position).getM_price());
        holder.tv_juli.setText("距离："+mList.get(position).getJuli());
        holder.tv_quan_count.setText("剩余券："+mList.get(position).getQuan_count()+"张");
        holder.tv_m_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tv_m_old_price.setText("原价¥ "+mList.get(position).getM_old_price());
        holder.tv_title.setText(mList.get(position).getTitle());
        String title = mList.get(position).getQ_title() == null ? " " : mList.get(position).getQ_title();
        holder.tv_q_title.setText(title);
        holder.tv_intro.setText(mList.get(position).getIntro());
        holder.tv_com.setText(mList.get(position).getCom());
        holder.tv_zf.setText(mList.get(position).getZf());
        holder.tv_zan.setText(mList.get(position).getZan());

        Glide.with(mContext)
                .load(mList.get(position).getShop_img())
                .into(holder.iv_shop_img);

        Glide.with(mContext)
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(20)))
//                .load(mList.get(position).getVideo())
                .load("https://manhua.qpic.cn/vertical/0/07_22_36_afe651da2ab940d0e257a1ec894bd992_1504795010150.jpg/420")
                .into(holder.iv_video);

        holder.msg_item_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });

        holder.tv_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onComment(position);
            }
        });

        holder.tv_zf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onZF(position);;
            }
        });

        holder.tv_zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onZan(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    //② 创建ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_ll)
        LinearLayout msg_item_ll;
        @BindView(R.id.iv_shop_img)
        ImageView iv_shop_img;
        @BindView(R.id.tv_shop_name)
        TextView tv_shop_name;
        @BindView(R.id.tv_m_price)
        TextView tv_m_price;
        @BindView(R.id.tv_juli)
        TextView tv_juli;
        @BindView(R.id.tv_quan_count)
        TextView tv_quan_count;
        @BindView(R.id.tv_m_old_price)
        TextView tv_m_old_price;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_q_title)
        TextView tv_q_title;
        @BindView(R.id.tv_intro)
        TextView tv_intro;
        @BindView(R.id.tv_com)
        TextView tv_com;
        @BindView(R.id.tv_zf)
        TextView tv_zf;
        @BindView(R.id.tv_zan)
        TextView tv_zan;
        @BindView(R.id.iv_video)
        ImageView iv_video;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }
}
