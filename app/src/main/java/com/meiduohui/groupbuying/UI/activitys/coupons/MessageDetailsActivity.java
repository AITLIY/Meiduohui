package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.MyRecyclerView;
import com.meiduohui.groupbuying.adapter.GeneralCouponListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.IndexBean;
import com.meiduohui.groupbuying.bean.ShopInfoBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_m_price)
    TextView mTvMPrice;
    @BindView(R.id.tv_intro)
    TextView mTvIntroe;
    @BindView(R.id.tv_m_old_price)
    TextView mTvMOldPrice;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;
    @BindView(R.id.tv_shop_collect)
    TextView mTvShopCollect;
    @BindView(R.id.tv_shop_cancel_collect)
    TextView mTvShopCancelCollect;
    @BindView(R.id.tv_distance)
    TextView mTvDistance;
    @BindView(R.id.tv_shop_intro)
    TextView mTvShopIntro;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.tv_sjh)
    TextView mTvSjh;
    @BindView(R.id.tv_q_title)
    TextView mTvQTitle;
    @BindView(R.id.tv_have_quan)
    TextView mTvHaveQuan;
    @BindView(R.id.tv_have_quaned)
    TextView mTvHaveQuaned;
    @BindView(R.id.tv_use_time)
    TextView mTvUseTime;
    @BindView(R.id.tv_beizhu)
    TextView mTvBeizhu;
    @BindView(R.id.rv_more_coupon_list)
    MyRecyclerView mRvMoreCouponList;
    @BindView(R.id.more_msg_tv)
    TextView mMoreMsgTv;
    @BindView(R.id.more_msg_v)
    View mMoreMsgV;
    @BindView(R.id.more_msg_rl)
    RelativeLayout mMoreMsgRl;
    @BindView(R.id.comment_tv)
    TextView mCommentTv;
    @BindView(R.id.comment_v)
    View mCommentV;
    @BindView(R.id.comment_ll)
    RelativeLayout mCommentLl;
    @BindView(R.id.rv_more_message_list)
    MyRecyclerView mRvMoreMessageList;
    @BindView(R.id.rv_comment_list)
    MyRecyclerView mRvCommentList;

    private String TAG = "MessageDetailsActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    private Location mLocation;

    private GeneralCouponListAdapter mGeneralCouponListAdapter;
    private ShopInfoBean.MInfoBean mMInfoBeans;
    private List<ShopInfoBean.MessageMoreBean> mMessageMoreBeans;

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

                    setContentData();
                    break;

                case LOAD_DATA1_FAILE:


                    break;

                case NET_ERROR:

                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
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
            IndexBean.MessageInfoBean messageInfoBean = (IndexBean.MessageInfoBean) bundle.getSerializable("MessageInfoBean");
            mLocation = (Location) bundle.getParcelable("Location");

            LogUtils.i(TAG + "initData Shop_name " + messageInfoBean.getShop_id());
            getShopInfoData(messageInfoBean);
        }

    }

    private void setContentData() {

        mTvTitle.setText(mMInfoBeans.getTitle());
        mTvIntroe.setText(mMInfoBeans.getIntro());
        mTvMPrice.setText(mMInfoBeans.getM_price());
        mTvMOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvMOldPrice.setText("原价"+ mMInfoBeans.getM_price());

        mTvShopName.setText(mMInfoBeans.getShop_name());
        mTvDistance.setText(mMInfoBeans.getJuli());
        mTvShopIntro.setText(mMInfoBeans.getShop_intro());
        mTvAddress.setText(mMInfoBeans.getAddress());
        mTvSjh.setText("电话：" + mMInfoBeans.getSjh());
        mTvQTitle.setText(mMInfoBeans.getQ_title());

        setCollectStatusView(mMInfoBeans.getShop_collect_state()==1);
        setHaveQuanView(mMInfoBeans.getHave_quan()==1);

        mTvUseTime.setText("有效时间：" + TimeUtils.LongToString(Long.parseLong(mMInfoBeans.getStart_time()),"yyyy.MM.dd")
                + " - " + TimeUtils.LongToString(Long.parseLong(mMInfoBeans.getEnd_time()),"yyyy.MM.dd"));
        mTvBeizhu.setText(mMInfoBeans.getBeizhu());

        mGeneralCouponListAdapter = new GeneralCouponListAdapter(mContext,mMInfoBeans.getS_quan_info());
        mGeneralCouponListAdapter.setOnItemClickListener(new GeneralCouponListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                
            }
        });
        mRvMoreCouponList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMoreCouponList.setAdapter(mGeneralCouponListAdapter);
    }

    private void setCollectStatusView(boolean isCollect) {

        if (!isCollect){
            mTvShopCollect.setVisibility(View.VISIBLE);
            mTvShopCancelCollect.setVisibility(View.GONE);
        } else {
            mTvShopCollect.setVisibility(View.GONE);
            mTvShopCancelCollect.setVisibility(View.VISIBLE);
        }
    }

    private void setHaveQuanView(boolean isHave) {

        if (!isHave){
            mTvHaveQuan.setVisibility(View.VISIBLE);
            mTvHaveQuaned.setVisibility(View.GONE);
        } else {
            mTvHaveQuan.setVisibility(View.GONE);
            mTvHaveQuaned.setVisibility(View.VISIBLE);
        }
    }



    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取一级分类
    private void getShopInfoData(final IndexBean.MessageInfoBean messageInfoBean) {

        String url = HttpURL.BASE_URL + HttpURL.SHOP_SHOPINFO;
        LogUtils.i(TAG + "getShopInfoData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getShopInfoData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getShopInfoData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            ShopInfoBean mShopInfoBean = new Gson().fromJson(data, ShopInfoBean.class);

                            mMInfoBeans = mShopInfoBean.getM_info();
                            mMessageMoreBeans = mShopInfoBean.getMessage_more();

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getShopInfoData mMessageMoreBeans.size " + mMessageMoreBeans.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getShopInfoData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SHOP_SHOPINFO + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getShopInfoData token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("m_id", messageInfoBean.getOrder_id());
                map.put("lon", mLocation.getLongitude() + "");
                map.put("lat", mLocation.getLatitude() + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getShopInfoData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    @OnClick({R.id.tv_shop_collect, R.id.tv_shop_cancel_collect,R.id.tv_have_quan, R.id.tv_have_quaned, R.id.iv_go_address, R.id.iv_call_shops})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_shop_collect:
                break;
            case R.id.tv_shop_cancel_collect:
                break;
            case R.id.tv_have_quan:
                break;
            case R.id.tv_have_quaned:
                break;
            case R.id.iv_go_address:
                break;
            case R.id.iv_call_shops:
                break;
        }
    }
}
