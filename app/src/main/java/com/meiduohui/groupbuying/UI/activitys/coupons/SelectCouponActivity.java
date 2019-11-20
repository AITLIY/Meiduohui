package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.adapter.SelectCouponListAdapter;
import com.meiduohui.groupbuying.bean.NewOrderBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectCouponActivity extends AppCompatActivity {


    private String TAG = "SelectCouponActivity: ";
    private Context mContext;

    @BindView(R.id.rv_coupon_list)
    RecyclerView mRvCouponList;

    private List<NewOrderBean.QuanInfoBean> mQuanInfoBeans = new ArrayList<>();              // 优惠券搜索结果列表
    private SelectCouponListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_coupon);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
    }

    private void initData() {
        mContext = this;

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            NewOrderBean orderBean = (NewOrderBean) bundle.getSerializable("NewOrderBean");
            mQuanInfoBeans = orderBean.getQuan_info();
            LogUtils.i(TAG + "initData mQuanInfoBeans.size() " + mQuanInfoBeans.size());

            if (mQuanInfoBeans.size() > 0) {

                setViewForResult(true, null);
                mAdapter = new SelectCouponListAdapter(mContext, mQuanInfoBeans);
                mAdapter.setOnItemClickListener(new SelectCouponListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("QuanInfoBean", mQuanInfoBeans.get(position));
                        intent.putExtras(bundle);

                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                mRvCouponList.setLayoutManager(new LinearLayoutManager(mContext));
                mRvCouponList.setAdapter(mAdapter);

            } else {

                setViewForResult(false, "没有可用优惠券~");
            }

        }
    }

    // 根据获取结果显示view
    private void setViewForResult(boolean isSuccess, String msg) {

        if (isSuccess) {
            findViewById(R.id.not_data).setVisibility(View.GONE);

        } else {
            findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.not_data_tv)).setText(msg);
        }
    }


    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

}
