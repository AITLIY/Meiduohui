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
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.HomepageActivity;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.AboutMeiActivity;
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

    private Unbinder unbinder;

    private UserBean mUserBean;
    private boolean mIsShop;
    private UserInfoBean.MemInfoBean mMemInfoBean;
    private UserInfoBean.ShopInfoBean mShopInfoBean;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILE = 202;
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

                case LOAD_DATA2_SUCCESS:


                    break;

                case LOAD_DATA2_FAILE:


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
    }

    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();
        mIsShop = GlobalParameterApplication.getInstance().getUserIsShop();

        if (mUserBean != null) {

            if (mIsShop)
                mLlShopItem.setVisibility(View.VISIBLE);

            getMemInfoData();
        }
    }

    @OnClick({R.id.civ_user_img, R.id.iv_user_info, R.id.ll_wallet, R.id.ll_orderList, R.id.ll_coupon, R.id.ll_historyList, R.id.ll_wallet1, R.id.ll_orderList1, R.id.ll_collectList, R.id.ll_historyList1, R.id.ll_shop_apply, R.id.ll_about_mei, R.id.ll_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.civ_user_img:

                if (GlobalParameterApplication.getInstance().getLoginStatus()) {


                } else {

                    startActivity(new Intent(getContext(), LoginActivity.class));
                }
                break;
            case R.id.iv_user_info:

                break;
            case R.id.ll_wallet:
                break;
            case R.id.ll_orderList:
                break;
            case R.id.ll_coupon:
                break;
            case R.id.ll_historyList:
                break;
            case R.id.ll_wallet1:
                break;
            case R.id.ll_orderList1:
                break;
            case R.id.ll_collectList:
                break;
            case R.id.ll_historyList1:
                break;
            case R.id.ll_shop_apply:
                break;
            case R.id.ll_about_mei:
                startActivity(new Intent(getContext(), AboutMeiActivity.class));
                break;
            case R.id.ll_setting:
                ((HomepageActivity) getActivity()).refreshDate();
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
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .load(imgUrl)
                //                .error(R.drawable.icon_tab_mine_0)
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
                            UserInfoBean userInfoBean = new Gson().fromJson(data, UserInfoBean.class);

                            mMemInfoBean = userInfoBean.getMem_info();
                            if (mIsShop)
                                mShopInfoBean = userInfoBean.getShop_info();

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
