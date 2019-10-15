package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.bean.CommentBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/12/25.
 */

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private Context mContext;
    private List<CommentBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ① 创建Adapter
    public CommentListAdapter(Context context, List<CommentBean> list) {
        mContext = context;
        mList = list;
    }

    //③ 在Adapter中实现3个方法
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Glide.with(mContext)
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .load(mList.get(position).getMem_img())
//                .error(R.drawable.icon_tab_mine_0)
                .into(holder.mCivMemImg);
        holder.mTvMemName.setText(mList.get(position).getMem_name());
        holder.mTvTime.setText(mList.get(position).getTime());
        holder.mTvComContent.setText(mList.get(position).getCom_content());

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

        @BindView(R.id.civ_mem_img)
        CircleImageView mCivMemImg;
        @BindView(R.id.tv_mem_name)
        TextView mTvMemName;
        @BindView(R.id.tv_time)
        TextView mTvTime;
        @BindView(R.id.tv_com_content)
        TextView mTvComContent;
        @BindView(R.id.rl_item)
        RelativeLayout mRlItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
