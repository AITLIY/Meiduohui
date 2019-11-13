package com.meiduohui.groupbuying.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.GridSpacingItemDecoration;
import com.meiduohui.groupbuying.bean.CategoryBean;
import com.meiduohui.groupbuying.utils.PxUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class AllCatListAdapter extends RecyclerView.Adapter<AllCatListAdapter.ViewHolder> {
    //新增itemType
    public static final int ITEM_TYPE = 100;

    private Context mContext;
    private List<CategoryBean> mList;
    private SecondCatListAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(SecondCatListAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AllCatListAdapter(Context context, List<CategoryBean> list) {
        mContext = context;
        mList = list;
    }

    //重写改方法，设置ItemViewType
    @Override
    public int getItemViewType(int position) {
        //返回值与使用时设置的值需保持一致
        return ITEM_TYPE;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_catrgory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_title.setText(mList.get(position).getName());
        Glide.with(mContext)
                .load(mList.get(position).getImg())
                .into(holder.iv_img);
        /*
         1.把内部RecyclerView的adapter和集合数据通过holder缓存
         2.使内部RecyclerView的adapter提供设置position的方法
         */
        holder.list.clear();
        holder.list.addAll(mList.get(position).getSecond_info());
        if (holder.mRvAdapter == null) {
            holder.mRvAdapter = new SecondCatListAdapter(mContext, holder.list);
            holder.mRvAdapter.setOnItemClickListener(onItemClickListener);
            GridLayoutManager layoutManage = new GridLayoutManager(mContext, 4);
            holder.mRecyclerView.setLayoutManager(layoutManage);
            holder.mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, PxUtils.dip2px(mContext,15), true));
            holder.mRecyclerView.setAdapter(holder.mRvAdapter);
        } else {

            holder.mRvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_img)
        ImageView iv_img;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.rv_second_cat_list)
        RecyclerView mRecyclerView;

        private SecondCatListAdapter mRvAdapter;
        private List<CategoryBean.SecondInfoBean> list = new ArrayList<>();

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}