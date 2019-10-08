package com.meiduohui.groupbuying.UI.fragments.home;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class CouponFragment extends Fragment {

    private String TAG = "HomeFragment: ";
    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;

    @BindView(R.id.unused_ll)                                     // 未使用
    RelativeLayout unused_ll;
    @BindView(R.id.unused_tv)
    TextView unused_tv;
    @BindView(R.id.unused_v)
    View unused_v;

    @BindView(R.id.used_ll)                                       // 已使用
    RelativeLayout used_ll;
    @BindView(R.id.used_tv)
    TextView used_tv;
    @BindView(R.id.used_v)
    View used_v;

    @BindView(R.id.expired_ll)                                    // 已过期
    RelativeLayout expired_ll;
    @BindView(R.id.expired_tv)
    TextView expired_tv;
    @BindView(R.id.expired_v)
    View expired_v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coupon, container, false);
        ButterKnife.bind(this,mView);

        init();
        return mView;
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {

        unused_ll.setOnClickListener(new CouponCatbarClink());
        used_ll.setOnClickListener(new CouponCatbarClink());
        expired_ll.setOnClickListener(new CouponCatbarClink());

//        initPullToRefresh();
    }

    class CouponCatbarClink implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.unused_ll:

//                    mIsFJ = false;
//
//                    if (mMoreTuiMessageInfos != null)
//                        updataListView(mMoreTuiMessageInfos); //  推荐请求
//                    else {
//                        updateData();     //  推荐请求
//                    }

                    break;

                case R.id.used_ll:

//                    mIsFJ = true;
//                    LogUtils.i(TAG + " mMoreFJMessageInfos" + (mMoreFJMessageInfos == null));
//                    if (mMoreFJMessageInfos != null)
//                        updataListView(mMoreFJMessageInfos); //  附近请求
//                    else {
//
//                        mIsMore = false;
//                        mIsFJ = true;
//                        getIndexData();     // 附近请求
//                    }

                    break;

                case R.id.expired_ll:

//                    mIsFJ = true;
//                    LogUtils.i(TAG + " mMoreFJMessageInfos" + (mMoreFJMessageInfos == null));
//                    if (mMoreFJMessageInfos != null)
//                        updataListView(mMoreFJMessageInfos); //  附近请求
//                    else {
//
//                        mIsMore = false;
//                        mIsFJ = true;
//                        getIndexData();     // 附近请求
//                    }

                    break;
            }

            changeTabItemStyle(v);
        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        unused_tv.setTextColor(view.getId() == R.id.unused_ll ? getResources().getColor(R.color.black) : getResources().getColor(R.color.m_old_price));
        used_tv.setTextColor(view.getId() == R.id.used_ll ? getResources().getColor(R.color.black) : getResources().getColor(R.color.m_old_price));
        expired_tv.setTextColor(view.getId() == R.id.expired_ll ? getResources().getColor(R.color.black) : getResources().getColor(R.color.m_old_price));

        unused_v.setVisibility(view.getId() ==  R.id.unused_ll ? View.VISIBLE:View.GONE);
        used_v.setVisibility(view.getId() == R.id.used_ll ? View.VISIBLE:View.GONE);
        expired_v.setVisibility(view.getId() == R.id.expired_ll ? View.VISIBLE:View.GONE);
    }



    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
    }


}
