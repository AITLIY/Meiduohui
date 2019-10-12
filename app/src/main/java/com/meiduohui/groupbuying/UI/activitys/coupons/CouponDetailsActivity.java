package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.jaeger.library.StatusBarUtil;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CouponBean;
import com.meiduohui.groupbuying.utils.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CouponDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tv_q_price)
    TextView mTvQPrice;
    @BindView(R.id.tv_q_type)
    TextView mTvQType;
    @BindView(R.id.tv_q_state)
    TextView mTvQState;
    @BindView(R.id.tv_use_time)
    TextView mTvUseTime;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;
    @BindView(R.id.tv_shop_name2)
    TextView mTvShopName2;
    @BindView(R.id.tv_shop_intro)
    TextView mTvShopIntro;
    @BindView(R.id.tv_collect)
    TextView mTvCollect;
    @BindView(R.id.tv_distance)
    TextView mTvDistance;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.tv_sjh)
    TextView mTvSjh;
    @BindView(R.id.beizhu)
    TextView mBeizhu;

    private String TAG = "CouponDetailsActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    private CouponBean mCouponBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_details);
        StatusBarUtil.setTranslucentForImageView(this, 0, findViewById(R.id.needOffsetView));
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        initData();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            mCouponBean = (CouponBean) bundle.getSerializable("CouponBean");

            LogUtils.i(TAG + "initData Shop_name " + mCouponBean.getShop_name());
            setContent();
        }

    }

    private void setContent() {
        mTvQPrice.setText(mCouponBean.getQ_price());

        String q_type = mCouponBean.getQ_type();
        String s_type = "优惠券";
        if (q_type.equals("1"))
            s_type = "代金券";
        else if (q_type.equals("2"))
            s_type = "折扣券";
        else if (q_type.equals("3"))
            s_type = "会员券";
        mTvQType.setText(s_type);

        String q_state = mCouponBean.getQ_state();
        String s_state = "商家通用券";
        if (q_state.equals("0"))
            s_state = "商家通用券";
        mTvQState.setText(s_state);

        mTvUseTime.setText("有效时间：" + TimeUtils.LongToString(Long.parseLong(mCouponBean.getStart_time()),"yyyy.MM.dd")
                + " - " + TimeUtils.LongToString(Long.parseLong(mCouponBean.getEnd_time()),"yyyy.MM.dd"));
        mTvShopName.setText("适用商家：" + mCouponBean.getShop_name());
        mTvDistance.setText("--");

        mTvCollect.setText("--");

        mTvShopName2.setText(mCouponBean.getShop_name());
        mTvShopIntro.setText(mCouponBean.getShop_intro());
        mTvAddress.setText(mCouponBean.getAddress());
        mTvSjh.setText("电话：" + mCouponBean.getSjh());
        mBeizhu.setText(mCouponBean.getBeizhu());

    }

    @OnClick({R.id.iv_back, R.id.tv_right_away_used, R.id.tv_collect, R.id.iv_go_address, R.id.iv_call_shops})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                break;
            case R.id.tv_right_away_used:
                break;
            case R.id.tv_collect:
                break;
            case R.id.iv_go_address:
                break;
            case R.id.iv_call_shops:
                break;
        }
    }
}
