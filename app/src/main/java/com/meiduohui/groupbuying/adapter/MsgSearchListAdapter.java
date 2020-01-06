//package com.meiduohui.groupbuying.adapter;
//
//import android.content.Context;
//import android.graphics.Paint;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;
//import com.lidroid.xutils.util.LogUtils;
//import com.meiduohui.groupbuying.R;
//import com.meiduohui.groupbuying.UI.views.CircleImageView;
//import com.meiduohui.groupbuying.UI.views.GridSpacingItemDecoration;
//import com.meiduohui.groupbuying.UI.views.NiceImageView;
//import com.meiduohui.groupbuying.bean.IndexBean;
//import com.meiduohui.groupbuying.commons.CommonParameters;
//import com.meiduohui.groupbuying.utils.PxUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * Created by ALIY on 2018/12/19 0019.
// */
//
//public class MsgSearchListAdapter extends BaseAdapter {
//
//    private Context mContext;
//    private List<IndexBean.MessageInfoBean> mList;
//
//    private OnItemClickListener onItemClickListener;
//
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(int position);
//        void onComment(int position);
//        void onZF(int position);
//        void onZan(int position);
//    }
//
//    public MsgSearchListAdapter(Context context, List<IndexBean.MessageInfoBean> list) {
//        mContext = context;
//        mList = list;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//
//        int type;
//
//        if (!TextUtils.isEmpty(mList.get(position).getVideo())) {
//            type = 1;
//        } else {
//            type = 2;
//        }
//        LogUtils.i("MessageInfoListAdapter getType: " + type + " position " + position);
//        return type;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
//    @Override
//    public int getCount() {
//        return mList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder1 holder = null;
//        ViewHolder2 holder2 = null;
//
//        int type = getItemViewType(position);
//        LogUtils.i("MessageInfoListAdapter type: " + type);
//
//        if (type==1) {
//            if (convertView == null) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, parent, false);
//
//                holder = new ViewHolder1(convertView);
//                convertView.setTag(holder);
//
//            } else {
//                holder = (ViewHolder1) convertView.getTag();
//            }
//
//            holder.tv_shop_name.setText(mList.get(position).getShop_name());
//            holder.tv_m_price.setText(mList.get(position).getM_price());
//            holder.tv_juli.setText("距离：" + mList.get(position).getJuli());
//            holder.tv_quan_count.setText("剩余券：" + mList.get(position).getQuan_count() + "张");
//            holder.tv_m_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//            holder.tv_m_old_price.setText("原价¥ " + mList.get(position).getM_old_price());
//            holder.tv_title.setText(mList.get(position).getTitle());
//            String title = mList.get(position).getQ_title() == null ? " " : mList.get(position).getQ_title();
//            if (TextUtils.isEmpty(mList.get(position).getQ_title()))
//                holder.tv_q_title.setVisibility(View.GONE);
//            else
//                holder.tv_q_title.setVisibility(View.VISIBLE);
//
//            holder.tv_q_title.setText(title);
//            holder.tv_intro.setText(mList.get(position).getIntro());
//            holder.tv_com.setText(mList.get(position).getCom());
//            holder.tv_zf.setText(mList.get(position).getZf());
//            holder.tv_zan.setText(mList.get(position).getZan());
//
//            if (mList.get(position).getZan_info() == 0)
//                holder.iv_zan.setBackgroundResource(R.drawable.icon_tab_zan_0);
//            else
//                holder.iv_zan.setBackgroundResource(R.drawable.icon_tab_zan_1);
//
//            Glide.with(mContext)
//                    .load(mList.get(position).getShop_img())
//                    .apply(new RequestOptions().error(R.drawable.icon_tab_usericon))
//                    .into(holder.iv_shop_img);
//
//            if (!TextUtils.isEmpty(mList.get(position).getVideo()))
//                Glide.with(mContext)
//                        .load(mList.get(position).getVideo() + CommonParameters.VIDEO_END)
//                        .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
//                        .into(holder.iv_video);
//
//
//            holder.msg_item_ll.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onItemClick(position);
//                }
//            });
//
//            holder.ll_com.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onComment(position);
//                }
//            });
//
//            holder.ll_zf.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onZF(position);
//                }
//            });
//
//            holder.ll_zan.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onZan(position);
//                }
//            });
//
//        } else {
//
//            if (convertView == null) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message2, parent, false);
//
//                holder2 = new ViewHolder2(convertView);
//                convertView.setTag(holder2);
//
//            } else {
//                holder2 = (ViewHolder2) convertView.getTag();
//            }
//
//            holder2.tv_shop_name.setText(mList.get(position).getShop_name());
//            holder2.tv_m_price.setText(mList.get(position).getM_price());
//            holder2.tv_juli.setText("距离：" + mList.get(position).getJuli());
//            holder2.tv_quan_count.setText("剩余券：" + mList.get(position).getQuan_count() + "张");
//            holder2.tv_m_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//            holder2.tv_m_old_price.setText("原价¥ " + mList.get(position).getM_old_price());
//            holder2.tv_title.setText(mList.get(position).getTitle());
//            String title = mList.get(position).getQ_title() == null ? " " : mList.get(position).getQ_title();
//            if (TextUtils.isEmpty(mList.get(position).getQ_title()))
//                holder2.tv_q_title.setVisibility(View.GONE);
//            else
//                holder2.tv_q_title.setVisibility(View.VISIBLE);
//
//            holder2.tv_q_title.setText(title);
//            holder2.tv_intro.setText(mList.get(position).getIntro());
//            holder2.tv_com.setText(mList.get(position).getCom());
//            holder2.tv_zf.setText(mList.get(position).getZf());
//            holder2.tv_zan.setText(mList.get(position).getZan());
//
//            if (mList.get(position).getZan_info() == 0)
//                holder2.iv_zan.setBackgroundResource(R.drawable.icon_tab_zan_0);
//            else
//                holder2.iv_zan.setBackgroundResource(R.drawable.icon_tab_zan_1);
//
//            Glide.with(mContext)
//                    .load(mList.get(position).getShop_img())
//                    .apply(new RequestOptions().error(R.drawable.icon_tab_usericon))
//                    .into(holder2.iv_shop_img);
//
//            holder2.list.clear();
//            holder2.list.addAll(mList.get(position).getImg());
//            if (holder2.mRvAdapter == null) {
//                holder2.mRvAdapter = new ImgListAdapter(mContext, holder2.list);
//                GridLayoutManager layoutManage = new GridLayoutManager(mContext, 3);
//                holder2.mRecyclerView.setLayoutManager(layoutManage);
//                holder2.mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, PxUtils.dip2px(mContext,5), true));
//                holder2.mRecyclerView.setAdapter(holder2.mRvAdapter);
//            } else {
//                holder2.mRvAdapter.notifyDataSetChanged();
//            }
//
//            holder2.msg_item_ll.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onItemClick(position);
//                }
//            });
//
//            holder2.ll_com.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onComment(position);
//                }
//            });
//
//            holder2.ll_zf.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onZF(position);
//                }
//            });
//
//            holder2.ll_zan.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onZan(position);
//                }
//            });
//        }
//
//        return convertView;
//    }
//
//    //② 创建ViewHolder
//    static class ViewHolder1 {
//
//        @BindView(R.id.msg_item_ll)
//        LinearLayout msg_item_ll;
//        @BindView(R.id.iv_shop_img)
//        CircleImageView iv_shop_img;
//        @BindView(R.id.tv_shop_name)
//        TextView tv_shop_name;
//        @BindView(R.id.tv_m_price)
//        TextView tv_m_price;
//        @BindView(R.id.tv_juli)
//        TextView tv_juli;
//        @BindView(R.id.tv_quan_count)
//        TextView tv_quan_count;
//        @BindView(R.id.tv_m_old_price)
//        TextView tv_m_old_price;
//        @BindView(R.id.tv_title)
//        TextView tv_title;
//        @BindView(R.id.tv_q_title)
//        TextView tv_q_title;
//        @BindView(R.id.tv_intro)
//        TextView tv_intro;
//        @BindView(R.id.tv_com)
//        TextView tv_com;
//        @BindView(R.id.tv_zf)
//        TextView tv_zf;
//        @BindView(R.id.tv_zan)
//        TextView tv_zan;
//        @BindView(R.id.iv_zan)
//        ImageView iv_zan;
//        @BindView(R.id.ll_com)
//        LinearLayout ll_com;
//        @BindView(R.id.ll_zf)
//        LinearLayout ll_zf;
//        @BindView(R.id.ll_zan)
//        LinearLayout ll_zan;
//        @BindView(R.id.iv_video)
//        NiceImageView iv_video;
//
//        public ViewHolder1(View v) {
//            ButterKnife.bind(this, v);
//        }
//
//    }
//
//    //② 创建ViewHolder
//    static class ViewHolder2 {
//
//        @BindView(R.id.msg_item_ll)
//        LinearLayout msg_item_ll;
//        @BindView(R.id.iv_shop_img)
//        CircleImageView iv_shop_img;
//        @BindView(R.id.tv_shop_name)
//        TextView tv_shop_name;
//        @BindView(R.id.tv_m_price)
//        TextView tv_m_price;
//        @BindView(R.id.tv_juli)
//        TextView tv_juli;
//        @BindView(R.id.tv_quan_count)
//        TextView tv_quan_count;
//        @BindView(R.id.tv_m_old_price)
//        TextView tv_m_old_price;
//        @BindView(R.id.tv_title)
//        TextView tv_title;
//        @BindView(R.id.tv_q_title)
//        TextView tv_q_title;
//        @BindView(R.id.tv_intro)
//        TextView tv_intro;
//        @BindView(R.id.tv_com)
//        TextView tv_com;
//        @BindView(R.id.tv_zf)
//        TextView tv_zf;
//        @BindView(R.id.tv_zan)
//        TextView tv_zan;
//        @BindView(R.id.iv_zan)
//        ImageView iv_zan;
//        @BindView(R.id.ll_com)
//        LinearLayout ll_com;
//        @BindView(R.id.ll_zf)
//        LinearLayout ll_zf;
//        @BindView(R.id.ll_zan)
//        LinearLayout ll_zan;
//
//        @BindView(R.id.rv_src_img)
//        RecyclerView mRecyclerView;
//
//        private ImgListAdapter mRvAdapter;
//        private List<String> list = new ArrayList<>();
//
//        public ViewHolder2(View v) {
//            ButterKnife.bind(this, v);
//        }
//    }
//}
