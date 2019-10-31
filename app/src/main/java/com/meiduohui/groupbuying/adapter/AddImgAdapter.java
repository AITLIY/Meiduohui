package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.commons.CommonParameters;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 添加上传图片适配器
 * <p>
 * 作者： 周旭 on 2017/6/21/0021.
 * 邮箱：374952705@qq.com
 * 博客：http://www.jianshu.com/u/56db5d78044d
 */

public class AddImgAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mList;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onDelImg(int position);
    }


    public AddImgAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        //return mList.size() + 1;//因为最后多了一个添加图片的ImageView 
        int count = mList == null ? 1 : mList.size() + 1;
        if (count > CommonParameters.MAX_SELECT_PIC_NUM) {
            return mList.size();
        } else {
            return count;
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_add_img, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.mIvDelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onDelImg(position);
            }
        });

        if (position < mList.size()) { // 代表+号之前的需要正常显示图片

            holder.mRvImgComplete.setVisibility(View.VISIBLE);
            holder.mLlImgAdd.setVisibility(View.GONE);

            String picUrl = mList.get(position); // 图片路径
            Glide.with(mContext).load(picUrl).into(holder.mIvImgThumb);

        } else {            // 最后一个显示加号图片
            holder.mRvImgComplete.setVisibility(View.GONE);
            holder.mLlImgAdd.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.iv_img_thumb)
        ImageView mIvImgThumb;
        @BindView(R.id.iv_del_img)
        ImageView mIvDelImg;
        @BindView(R.id.rv_img_complete)
        RelativeLayout mRvImgComplete;
        @BindView(R.id.ll_img_add)
        LinearLayout mLlImgAdd;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
