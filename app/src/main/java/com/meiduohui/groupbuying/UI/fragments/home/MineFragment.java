package com.meiduohui.groupbuying.UI.fragments.home;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.HomepageActivity;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.ApplyShopActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.CollectListActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.CouponListActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.HistoryListActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.ShopOrderListActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.VipOrderListActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.aboutMei.AboutMeiActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.setting.SettingActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.setting.ShopInfoActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.setting.VipInfoActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.wallet.MyWalletActivity;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.bean.UserInfoBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends Fragment {

    private String TAG = "MineFragment: ";
    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;
    private boolean mIsShop;
    private Unbinder unbinder;

    @BindView(R.id.civ_user_img)
    CircleImageView mCivUserImg;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_id)
    TextView mTvId;
    @BindView(R.id.tv_money)
    TextView mTvMoney;
    @BindView(R.id.tv_order_count)
    TextView mTvOrderCount;
    @BindView(R.id.tv_quan_count)
    TextView mTvQuanCount;
    @BindView(R.id.tv_history_count)
    TextView mTvHistoryCount;
    @BindView(R.id.ll_shop_item)
    LinearLayout mLlShopItem;
    @BindView(R.id.ll_shop_apply)
    LinearLayout mLlShopApply;
    @BindView(R.id.PullToRefreshScroll_View)
    PullToRefreshScrollView mPullToRefreshScrollView;

    private UserInfoBean mUserInfoBean;
    private UserInfoBean.MemInfoBean mMemInfoBean;
    private UserInfoBean.ShopInfoBean mShopInfoBean;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    setResultData();
                    break;

                case LOAD_DATA1_FAILE:

                    break;

                case NET_ERROR:

                    break;
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, mView);

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
        initPullToRefresh();
    }

    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();
        mIsShop = GlobalParameterApplication.getInstance().getUserIsShop();

        if (mUserBean != null) {

            if (mIsShop) {
                mLlShopItem.setVisibility(View.VISIBLE);
                mLlShopApply.setVisibility(View.GONE);
            }

            getMemInfoData();
        }
    }

    private void initPullToRefresh() {

        // 1.设置模式
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        // 2.1 通过调用getLoadingLayoutProxy方法，设置下拉刷新状况布局中显示的文字 ，第一个参数为true,代表下拉刷新
        ILoadingLayout headLables = mPullToRefreshScrollView.getLoadingLayoutProxy(true, false);
        headLables.setPullLabel("下拉刷新");
        headLables.setRefreshingLabel("正在刷新...");
        headLables.setReleaseLabel("放开刷新");

        // 2.2 设置上拉加载底部视图中显示的文字，第一个参数为false,代表上拉加载更多
        ILoadingLayout footerLables = mPullToRefreshScrollView.getLoadingLayoutProxy(false, true);
        footerLables.setPullLabel("上拉加载");
        footerLables.setRefreshingLabel("正在载入...");
        footerLables.setReleaseLabel("放开加载更多");

        // 3.设置监听事件
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                addtoTop();         // 请求网络数据
                refreshComplete();  // 数据加载完成后，关闭header,footer
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

                refreshComplete();  // 数据加载完成后，关闭header,footer
            }
        });

    }

    // 下拉刷新的方法:
    public void addtoTop() {

        getMemInfoData();// 下拉刷新
    }


    // 刷新完成时关闭
    public void refreshComplete() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshScrollView.onRefreshComplete();
            }
        }, 1000);
    }

    @OnClick({R.id.civ_user_img, R.id.ll_wallet, R.id.ll_orderList, R.id.ll_coupon, R.id.ll_historyList, R.id.ll_wallet1, R.id.ll_orderList1, R.id.ll_coupon1, R.id.ll_collectList, R.id.ll_historyList1, R.id.ll_shop_apply, R.id.ll_about_mei, R.id.ll_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.civ_user_img:

                if (GlobalParameterApplication.getInstance().getLoginStatus()) {

                    Intent intent;

                    if (mIsShop)
                        intent = new Intent(mContext, ShopInfoActivity.class);
                    else
                        intent = new Intent(mContext, VipInfoActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("UserInfoBean", mUserInfoBean);
                    intent.putExtras(bundle);
                    startActivity(intent);

                } else {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
                break;

            case R.id.ll_wallet:

                startActivity(new Intent(getContext(), MyWalletActivity.class));
                break;

            case R.id.ll_orderList:

                startActivity(new Intent(getContext(), VipOrderListActivity.class));
                break;

            case R.id.ll_coupon:

                ((HomepageActivity) getActivity()).goToCoupon();
                break;

            case R.id.ll_historyList:

                startActivity(new Intent(getContext(), HistoryListActivity.class));
                break;

            case R.id.ll_wallet1:

                startActivity(new Intent(getContext(), MyWalletActivity.class));
                break;

            case R.id.ll_orderList1:

                startActivity(new Intent(getContext(), ShopOrderListActivity.class));
                break;

            case R.id.ll_coupon1:

                startActivity(new Intent(getContext(), CouponListActivity.class));
                break;

            case R.id.ll_collectList:

                startActivity(new Intent(getContext(), CollectListActivity.class));
                break;

            case R.id.ll_historyList1:

                startActivity(new Intent(getContext(), HistoryListActivity.class));
                break;

            case R.id.ll_shop_apply:

                startActivity(new Intent(getContext(), ApplyShopActivity.class));
                break;

            case R.id.ll_about_mei:
                startActivity(new Intent(getContext(), AboutMeiActivity.class));
                break;

            case R.id.ll_setting:

                Intent intent = new Intent(mContext, SettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("UserInfoBean", mUserInfoBean);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    private void setResultData() {

        String imgUrl = "";

        if (!mIsShop){
            imgUrl = mMemInfoBean.getImg();
            mTvName.setText(mMemInfoBean.getName());
            mTvId.setText("账号：" + mMemInfoBean.getId());
        } else {
            imgUrl = mShopInfoBean.getImg();
            mTvName.setText(mShopInfoBean.getName());
            mTvId.setText("账号：" + mShopInfoBean.getId());
        }

        Glide.with(mContext)
                .load(imgUrl)
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mCivUserImg);

        mTvMoney.setText(mMemInfoBean.getMoney());
        mTvOrderCount.setText(mMemInfoBean.getOrder_count()+"");
        mTvQuanCount.setText(mMemInfoBean.getQuan_count()+"");
        mTvHistoryCount.setText(mMemInfoBean.getHistory_count()+"");

    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 个人中心首页
    private void getMemInfoData() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_MEMINFO;
        LogUtils.i(TAG + "getMemInfoData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getMemInfoData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getMemInfoData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mUserInfoBean = new Gson().fromJson(data, UserInfoBean.class);

                            mMemInfoBean = mUserInfoBean.getMem_info();
                            if (mIsShop)
                                mShopInfoBean = mUserInfoBean.getShop_info();

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "mMemInfoBean id " + mMemInfoBean.getId() + " shop_id " + mMemInfoBean.getShop_id());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getMemInfoData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_MEMINFO + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getMemInfoData token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getMemInfoData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
