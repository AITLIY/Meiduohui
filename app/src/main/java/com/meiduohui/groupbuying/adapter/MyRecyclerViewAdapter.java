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
import com.meiduohui.groupbuying.interfaces.IMessageItemClink;

import java.util.List;

/**
 * Created by Administrator on 2018/12/25.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<IndexBean.MessageInfoBean> mDatas;
    private IMessageItemClink mIMessageItemClink;

    // ① 创建Adapter
    public MyRecyclerViewAdapter(Context context, List<IndexBean.MessageInfoBean> data, IMessageItemClink messageItemClink) {
        mContext = context;
        mDatas = data;
        mIMessageItemClink = messageItemClink;
    }

    //② 创建ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout msg_item_ll;
        public final ImageView iv_shop_img;
        public final TextView tv_shop_name;
        public final TextView tv_m_price;
        public final TextView tv_juli;
        public final TextView tv_quan_count;
        public final TextView tv_m_old_price;
        public final TextView tv_title;
        public final TextView tv_q_title;
        public final TextView tv_intro;
        public final ImageView iv_video;
        public final TextView tv_com;
        public final TextView tv_zf;
        public final TextView tv_zan;

        public ViewHolder(View v) {
            super(v);
            msg_item_ll = v.findViewById(R.id.msg_item_ll);
            iv_shop_img = v.findViewById(R.id.iv_shop_img);
            tv_shop_name = v.findViewById(R.id.tv_shop_name);
            tv_m_price = v.findViewById(R.id.tv_m_price);
            tv_juli = v.findViewById(R.id.tv_juli);
            tv_quan_count = v.findViewById(R.id.tv_quan_count);
            tv_m_old_price = v.findViewById(R.id.tv_m_old_price);
            tv_title = v.findViewById(R.id.tv_title);
            tv_q_title = v.findViewById(R.id.tv_q_title);
            tv_intro = v.findViewById(R.id.tv_intro);
            iv_video = v.findViewById(R.id.iv_video);
            tv_com = v.findViewById(R.id.tv_com);
            tv_zf = v.findViewById(R.id.tv_zf);
            tv_zan = v.findViewById(R.id.tv_zan);
        }
    }

    //③ 在Adapter中实现3个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_shop_name.setText(mDatas.get(position).getShop_name());
        holder.tv_m_price.setText(mDatas.get(position).getM_price());
        holder.tv_juli.setText("距离："+mDatas.get(position).getJuli());
        holder.tv_quan_count.setText("剩余券："+mDatas.get(position).getQuan_count()+"张");
        holder.tv_m_old_price.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
        holder.tv_m_old_price.setText("原价¥"+mDatas.get(position).getM_old_price());
        holder.tv_title.setText(mDatas.get(position).getTitle());
        String title = mDatas.get(position).getQ_title() == null ? " " : mDatas.get(position).getQ_title();
        holder.tv_q_title.setText(title);
        holder.tv_intro.setText(mDatas.get(position).getIntro());
        holder.tv_com.setText(mDatas.get(position).getCom());
        holder.tv_zf.setText(mDatas.get(position).getZf());
        holder.tv_zan.setText(mDatas.get(position).getZan());

        Glide.with(mContext)
                .load(mDatas.get(position).getShop_img())
                .into(holder.iv_shop_img);

        Glide.with(mContext)
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(20)))
//                .load(mDatas.get(position).getVideo())
                .load("https://manhua.qpic.cn/vertical/0/07_22_36_afe651da2ab940d0e257a1ec894bd992_1504795010150.jpg/420")
                .into(holder.iv_video);

        holder.msg_item_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMessageItemClink.onItem(mDatas.get(position));
            }
        });

        holder.iv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMessageItemClink.onMedia(mDatas.get(position));
            }
        });

        holder.tv_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMessageItemClink.onComment(mDatas.get(position));
            }
        });

        holder.tv_zf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMessageItemClink.onZF(mDatas.get(position));
            }
        });

        holder.tv_zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMessageItemClink.onZan(mDatas.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
