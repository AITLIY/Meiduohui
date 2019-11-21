package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.UI.views.GridSpacingItemDecoration;
import com.meiduohui.groupbuying.UI.views.NiceImageView;
import com.meiduohui.groupbuying.bean.IndexBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.utils.PxUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/12/25.
 */

public class MessageInfoListAdapter extends RecyclerView.Adapter {

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
            return new ViewHolder1(view);
        } else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message2, parent, false);
            return new ViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder0, final int position) {

        if (holder0.getItemViewType() == 1) {

            ViewHolder1 holder = (ViewHolder1) holder0;

            holder.tv_shop_name.setText(mList.get(position).getShop_name());
            holder.tv_m_price.setText(mList.get(position).getM_price());
            holder.tv_juli.setText("距离：" + mList.get(position).getJuli());
            if (mList.get(position).getQuan_count() > 0) {
                holder.tv_quan_count.setVisibility(View.VISIBLE);
                holder.tv_quan_count.setText("剩余券：" + mList.get(position).getQuan_count() + "张");
            }else {
                holder.tv_quan_count.setVisibility(View.GONE);
            }
            holder.tv_m_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tv_m_old_price.setText("原价¥ " + mList.get(position).getM_old_price());
            holder.tv_title.setText(mList.get(position).getTitle());
            String title = mList.get(position).getQ_title() == null ? " " : mList.get(position).getQ_title();
            if (TextUtils.isEmpty(mList.get(position).getQ_title()))
                holder.tv_q_title.setVisibility(View.GONE);
            holder.tv_q_title.setText(title);
            holder.tv_intro.setText(mList.get(position).getIntro());
            holder.tv_com.setText(mList.get(position).getCom());
            holder.tv_zf.setText(mList.get(position).getZf());
            holder.tv_zan.setText(mList.get(position).getZan());

            if (mList.get(position).getZan_info() == 0)
                holder.iv_zan.setBackgroundResource(R.drawable.icon_tab_zan_0);
            else
                holder.iv_zan.setBackgroundResource(R.drawable.icon_tab_zan_1);

            Glide.with(mContext)
                    .load(mList.get(position).getShop_img())
                    .apply(new RequestOptions().error(R.drawable.icon_tab_usericon))
                    .into(holder.iv_shop_img);

            if (!TextUtils.isEmpty(mList.get(position).getVideo())) {
                Glide.with(mContext)
                        .load(mList.get(position).getVideo() + CommonParameters.VIDEO_END)
                        .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                        .into(holder.iv_video);

                holder.rv_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String extension = MimeTypeMap.getFileExtensionFromUrl(mList.get(position).getVideo());
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
                        mediaIntent.setDataAndType(Uri.parse(mList.get(position).getVideo()), mimeType);
                        mContext.startActivity(mediaIntent);
                    }
                });
            }

            holder.msg_item_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });

            holder.ll_com.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onComment(position);
                }
            });

            holder.ll_zf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onZF(position);
                }
            });

            holder.ll_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onZan(position);
                }
            });

        } else {

            ViewHolder2 holder = (ViewHolder2) holder0;

            holder.tv_shop_name.setText(mList.get(position).getShop_name());
            holder.tv_m_price.setText(mList.get(position).getM_price());
            holder.tv_juli.setText("距离：" + mList.get(position).getJuli());
            if (mList.get(position).getQuan_count() > 0) {
                holder.tv_quan_count.setVisibility(View.VISIBLE);
                holder.tv_quan_count.setText("剩余券：" + mList.get(position).getQuan_count() + "张");
            }else {
                holder.tv_quan_count.setVisibility(View.GONE);
            }

            holder.tv_m_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tv_m_old_price.setText("原价¥ " + mList.get(position).getM_old_price());
            holder.tv_title.setText(mList.get(position).getTitle());
            String title = mList.get(position).getQ_title() == null ? " " : mList.get(position).getQ_title();
            if (TextUtils.isEmpty(mList.get(position).getQ_title()))
                holder.tv_q_title.setVisibility(View.GONE);
            holder.tv_q_title.setText(title);
            holder.tv_intro.setText(mList.get(position).getIntro());
            holder.tv_com.setText(mList.get(position).getCom());
            holder.tv_zf.setText(mList.get(position).getZf());
            holder.tv_zan.setText(mList.get(position).getZan());

            if (mList.get(position).getZan_info() == 0)
                holder.iv_zan.setBackgroundResource(R.drawable.icon_tab_zan_0);
            else
                holder.iv_zan.setBackgroundResource(R.drawable.icon_tab_zan_1);

            Glide.with(mContext)
                    .load(mList.get(position).getShop_img())
                    .apply(new RequestOptions().error(R.drawable.icon_tab_usericon))
                    .into(holder.iv_shop_img);

            holder.list.clear();
            holder.list.addAll(mList.get(position).getImg());
            if (holder.mRvAdapter == null) {
                holder.mRvAdapter = new ImgListAdapter(mContext, holder.list);
                GridLayoutManager layoutManage = new GridLayoutManager(mContext, 3);
                holder.mRecyclerView.setLayoutManager(layoutManage);
                holder.mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, PxUtils.dip2px(mContext,5), true));
                holder.mRecyclerView.setAdapter(holder.mRvAdapter);
            } else {
                holder.mRvAdapter.notifyDataSetChanged();
            }

            holder.msg_item_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(position);
                }
            });

            holder.ll_com.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onComment(position);
                }
            });

            holder.ll_zf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onZF(position);
                }
            });

            holder.ll_zan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onZan(position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {

        int type;

        if (!TextUtils.isEmpty(mList.get(position).getVideo()))
            type = 1;
        else
            type = 2;

        LogUtils.i("MessageInfoListAdapter getType: " + type + " position " + position);
        return type;
    }

    //② 创建ViewHolder
    static class ViewHolder1 extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_ll)
        LinearLayout msg_item_ll;
        @BindView(R.id.iv_shop_img)
        CircleImageView iv_shop_img;
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
        @BindView(R.id.iv_zan)
        ImageView iv_zan;
        @BindView(R.id.ll_com)
        LinearLayout ll_com;
        @BindView(R.id.ll_zf)
        LinearLayout ll_zf;
        @BindView(R.id.ll_zan)
        LinearLayout ll_zan;
        @BindView(R.id.iv_video)
        NiceImageView iv_video;
        @BindView(R.id.rv_video)
        RelativeLayout rv_video;

        public ViewHolder1(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }

    //② 创建ViewHolder
    static class ViewHolder2 extends RecyclerView.ViewHolder {

        @BindView(R.id.msg_item_ll)
        LinearLayout msg_item_ll;
        @BindView(R.id.iv_shop_img)
        CircleImageView iv_shop_img;
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
        @BindView(R.id.iv_zan)
        ImageView iv_zan;
        @BindView(R.id.ll_com)
        LinearLayout ll_com;
        @BindView(R.id.ll_zf)
        LinearLayout ll_zf;
        @BindView(R.id.ll_zan)
        LinearLayout ll_zan;

        @BindView(R.id.rv_src_img)
        RecyclerView mRecyclerView;

        private ImgListAdapter mRvAdapter;
        private List<String> list = new ArrayList<>();

        public ViewHolder2(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }

}
