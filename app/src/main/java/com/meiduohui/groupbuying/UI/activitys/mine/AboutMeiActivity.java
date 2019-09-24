package com.meiduohui.groupbuying.UI.activitys.mine;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;

public class AboutMeiActivity extends AppCompatActivity implements View.OnClickListener {


    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;

    private LinearLayout ll_about_mei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_mei);

        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


//        ll_about_mei = mView.findViewById(R.id.ll_about_mei);

//        ll_about_mei.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_about_mei:

                break;
        }
    }


    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

    }

}
