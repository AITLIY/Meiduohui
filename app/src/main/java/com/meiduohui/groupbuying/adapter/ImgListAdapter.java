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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.NiceImageView;
import com.meiduohui.groupbuying.bean.CategoryBean;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(mContext)
                .load(mList.get(position))
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(holder.iv_img);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_img)
        NiceImageView iv_img;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}