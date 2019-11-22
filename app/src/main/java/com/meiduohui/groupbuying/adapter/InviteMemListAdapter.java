package com.meiduohui.groupbuying.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.bean.InviteMemBean;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by ALIY on 2018/12/19 0019.
 */

public class InviteMemListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<InviteMemBean> mList;

    public InviteMemListAdapter(Context context, ArrayList<InviteMemBean> list) {
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
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_coupon, parent,false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }

    static class ViewHolder {

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
