package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.IndexBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ALIY on 2018/12/16 0016.
 */

public class FirstCatInfoBeanAdapter extends BaseAdapter {

    private Context mContext;
    private List<IndexBean.CatInfoBean> mList;

    public FirstCatInfoBeanAdapter(Context context, List<IndexBean.CatInfoBean> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_first_category_home, parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if ("".equals(mList.get(position).getImg())) {
            Glide.with(mContext)
                    .load(mList.get(position).getImg2())
                    .into(holder.categoryIco);
        } else {
            Glide.with(mContext)
                    .load(mList.get(position).getImg())
                    .into(holder.categoryIco);
        }

        holder.categoryText.setText(mList.get(position).getName());


        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.category_img)
        ImageView categoryIco;
        @BindView(R.id.category_tv)
        TextView categoryText;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
