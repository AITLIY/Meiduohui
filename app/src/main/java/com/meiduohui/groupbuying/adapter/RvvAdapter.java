package com.meiduohui.groupbuying.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.CategoryBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author qdafengzi
 */
public class RvvAdapter extends RecyclerView.Adapter<RvvAdapter.ViewHolder> {
    private Context mContext;
    private List<CategoryBean.SecondInfoBean> mList;

    private int mPosition;

    public RvvAdapter(Context context, List<CategoryBean.SecondInfoBean> list, int position) {
        mContext = context;
        mList = list;
        mPosition = position;
    }

    /**
     * 新增方法
     *
     * @param position 位置
     */
    public void setPosition(int position) {
        this.mPosition = position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_second_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.second_cat_tv.setText(mList.get(position).getName());
        Glide.with(mContext)
                .load(mList.get(position).getImg())
                .into(holder.second_cat_iv);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.second_cat_iv)
        ImageView second_cat_iv;
        @BindView(R.id.second_cat_tv)
        TextView second_cat_tv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}