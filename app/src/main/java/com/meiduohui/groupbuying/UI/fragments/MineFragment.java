package com.meiduohui.groupbuying.UI.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment implements View.OnClickListener {



    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;
    private ImageView iv_user_photo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine, container, false);

        init();
        return mView;

    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {

        iv_user_photo = mView.findViewById(R.id.iv_user_photo);

        iv_user_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_user_photo:

                startActivity(new Intent(getContext(), LoginActivity.class));
                break;

            case R.id.iv_scan_code:

                break;
        }
    }


    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

    }

}
