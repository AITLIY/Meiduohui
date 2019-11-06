package com.meiduohui.groupbuying.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.PlusImageActivity;
import com.meiduohui.groupbuying.UI.views.NiceImageView;
import com.meiduohui.groupbuying.commons.CommonParameters;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author qdafengzi
 */
public class ImgListAdapter extends RecyclerView.Adapter<ImgListAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mList;

    public ImgListAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_img, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Glide.with(mContext)
                .load(mList.get(position))
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(holder.iv_img);

        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPluImg(position,mList);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    // 查看大图
    private void viewPluImg(int position,List<String> urls) {
        Intent intent = new Intent(mContext, PlusImageActivity.class);
        intent.putStringArrayListExtra(CommonParameters.IMG_LIST, (ArrayList<String>) urls);
        intent.putExtra(CommonParameters.POSITION, position);
        mContext.startActivity(intent);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_item)
        LinearLayout ll_item;
        @BindView(R.id.iv_img)
        NiceImageView iv_img;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}