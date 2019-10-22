package com.meiduohui.groupbuying.UI.activitys.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.RequestQueue;
import com.githang.statusbar.StatusBarCompat;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;

import butterknife.ButterKnife;
import butterknife.OnClick;

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
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar2), true);

        init();
    }

    private void init() {

        initData();
    }

    @OnClick({R.id.iv_back})
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_back:

                finish();
                break;
        }
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

    }

}
