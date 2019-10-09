package com.meiduohui.groupbuying.UI.fragments.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.AboutMeiActivity;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment {

    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;

    private Unbinder unbinder;

    @BindView(R.id.iv_user_photo)
    ImageView iv_user_photo;
    @BindView(R.id.ll_about_mei)
    LinearLayout ll_about_mei;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this,mView);

        init();
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void init() {

        initData();
    }


    @OnClick({R.id.iv_user_photo,R.id.ll_about_mei})
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_user_photo:

                startActivity(new Intent(getContext(), LoginActivity.class));
                break;

            case R.id.ll_about_mei:

                startActivity(new Intent(getContext(), AboutMeiActivity.class));
                break;
        }
    }


    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

    }

}
