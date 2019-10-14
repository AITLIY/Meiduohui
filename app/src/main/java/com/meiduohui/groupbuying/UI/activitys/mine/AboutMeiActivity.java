package com.meiduohui.groupbuying.UI.activitys.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.RequestQueue;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;

import butterknife.ButterKnife;

public class AboutMeiActivity extends AppCompatActivity {

    private Context mContext;
    private RequestQueue requestQueue;

//    @BindView(R.id.ll_about_mei)
//    LinearLayout ll_about_mei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_mei);
        ButterKnife.bind(this);

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

    }

//    @OnClick({R.id.ll_about_mei})
//    public void onClick(View v) {
//
//        switch (v.getId()) {
//
//            case R.id.ll_about_mei:
//
//                break;
//        }
//    }


    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

    }

}
