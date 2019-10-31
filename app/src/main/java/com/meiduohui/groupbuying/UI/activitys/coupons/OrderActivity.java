package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.AddOrderBean;
import com.meiduohui.groupbuying.bean.NewOrderBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderActivity extends AppCompatActivity {

    private String TAG = "OrderActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;
    @BindView(R.id.tv_m_price)
    TextView mTvMPrice;
    @BindView(R.id.tv_quan_title)
    TextView mTvQuanTitle;
    @BindView(R.id.tv_pay_price)
    TextView mTvPayPrice;

    private String mMId;                                        // 信息id
    private NewOrderBean mNewOrderBean;                         // 下单信息
    private NewOrderBean.MessageInfoBean mMessageInfoBean;      // 优惠信息
    private NewOrderBean.QuanInfoBean mQuanInfoBean;            // 优惠券
    private AddOrderBean mAddOrderBean;                         // 生成订单

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILE = 202;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    final
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    setContent();
                    break;

                case LOAD_DATA1_FAILE:

                    break;

                case LOAD_DATA2_SUCCESS:

                    LogUtils.i(TAG + "initData AddOrderBean(). " + mAddOrderBean.getState_intro());
                    GlobalParameterApplication.getInstance().setPayIntention(CommonParameters.NEW_ORDER);
                    Intent intent = new Intent(OrderActivity.this, PayOrderActivity.class);
                    intent.putExtra("OrderID", mAddOrderBean.getOrder_id());
                    startActivity(intent);
                    ToastUtil.show(mContext, "下单成功，等待支付");
                    break;

                case LOAD_DATA2_FAILE:

                    ToastUtil.show(mContext, "下单失败");
                    break;

                case NET_ERROR:

                    ToastUtil.show(mContext, "网络异常,请稍后再试");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
    }

    private void initData() {

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        Intent intent = getIntent();
        if (intent != null) {
            mMId = (String) intent.getStringExtra("m_id");
            LogUtils.i(TAG + "initData mMId " + mMId);
            getOrderInfo();
        }
    }

    @OnClick({R.id.iv_back, R.id.ll_quan_title, R.id.tv_add_order})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:
                finish();
                break;

            case R.id.ll_quan_title:

                LogUtils.i(TAG + "initData getQuan_info().size() " + mNewOrderBean.getQuan_info().size());

                Intent intent = new Intent(this, SelectCouponActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("NewOrderBean", mNewOrderBean);
                intent.putExtras(bundle);
                startActivityForResult(intent, RECORD_REQUEST_CODE);

                break;

            case R.id.tv_add_order:

                addOrder();
                break;
        }
    }

    private static final int RECORD_REQUEST_CODE = 3000;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Bundle bundle = data.getExtras();
                mQuanInfoBean = (NewOrderBean.QuanInfoBean) bundle.getSerializable("QuanInfoBean");

                setContent2();
            }
        }
    }


    private void setContent() {

        mTvTitle.setText(mMessageInfoBean.getTitle());
        mTvShopName.setText(mMessageInfoBean.getShop_name());
        mTvMPrice.setText("¥ " + mMessageInfoBean.getM_price());
        mTvPayPrice.setText("¥ " + mMessageInfoBean.getM_price());
    }

    private void setContent2() {

        mTvQuanTitle.setText(mQuanInfoBean.getTitle());

        Double m_price = Double.parseDouble(mMessageInfoBean.getM_price());
        Double q_price = Double.parseDouble(mQuanInfoBean.getQ_price());
        Double payPrice = 0.00;

        String q_type = mQuanInfoBean.getQ_type();
        if (q_type.equals("1")) {

            if (m_price > q_price) {
                payPrice = m_price - q_price;
            }

        } else if (q_type.equals("2")) {
            payPrice = m_price*q_price;
        }

        String str =String.format("%.2f", payPrice);

        mTvPayPrice.setText("¥ " + str);

    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取订单信息
    private void getOrderInfo() {

        final String url = HttpURL.BASE_URL + HttpURL.ORDER_ORDER;
        LogUtils.i(TAG + "getOrderInfo url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getOrderInfo result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getOrderInfo msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mNewOrderBean = new Gson().fromJson(data, NewOrderBean.class);
                            mMessageInfoBean = mNewOrderBean.getMessage_info();
                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            return;
                        }

                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);

                    } catch (JSONException e) {
                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getOrderInfo volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_ORDER + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getOrderInfo token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("m_id", mMId);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getOrderInfo json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 下单
    private void addOrder() {

        final String url = HttpURL.BASE_URL + HttpURL.ORDER_ADDORDER;
        LogUtils.i(TAG + "addOrder url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "addOrder result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "addOrder msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {
                            String data = jsonResult.getString("data");
                            mAddOrderBean = new Gson().fromJson(data, AddOrderBean.class);
                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);
                            return;
                        }

                        mHandler.sendEmptyMessage(LOAD_DATA2_FAILE);

                    } catch (JSONException e) {
                        mHandler.sendEmptyMessage(LOAD_DATA2_FAILE);
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "addOrder volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_ADDORDER + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "addOrder token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("m_id", mMId);
                if (mQuanInfoBean!=null)
                    map.put("q_id", mQuanInfoBean.getQ_id());
                map.put("number", "1");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "addOrder json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
