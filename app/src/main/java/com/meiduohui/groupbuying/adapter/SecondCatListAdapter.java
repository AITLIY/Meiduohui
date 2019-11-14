package com.meiduohui.groupbuying.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class SecondCatListAdapter extends RecyclerView.Adapter<SecondCatListAdapter.ViewHolder> {
    private Context mContext;
    private List<CategoryBean.SecondInfoBean> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onCallbackClick(String id1,String id2,String catName);
    }
    private int mPosition;
    private String mID1 = "0";

    public SecondCatListAdapter(Context context, List<CategoryBean.SecondInfoBean> list) {
        mContext = context;
        mList = list;
    }

    /**
     * 新增方法
     *
     * @param position
     */
    public void setPosition(int position) {
        this.mPosition = position;
    }

    public void setID(String id) {
        this.mID1 = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_second_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.second_cat_tv.setText(mList.get(position).getName());
        Glide.with(mContext)
                .load(mList.get(position).getImg())
                .into(holder.second_cat_iv);
        holder.lay_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onCallbackClick(mID1,mList.get(position).getId(),mList.get(position).getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.lay_option)
        LinearLayout lay_option;
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