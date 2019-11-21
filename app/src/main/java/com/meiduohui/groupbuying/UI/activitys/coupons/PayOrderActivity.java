package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alipay.sdk.app.PayTask;
import com.android.tu.loadingdialog.LoadingDailog;
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
import com.meiduohui.groupbuying.bean.PayBean;
import com.meiduohui.groupbuying.bean.PayResult;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayOrderActivity extends AppCompatActivity {

    private String TAG = "PayOrderActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    private String mOrderID;                         // 生成的订单
    private String mTable;                          // 表名
    private String mNotify;                         // 异步通知地址
    private PayBean mPayBean;                                   // 微信支付信息
    private String mPayInfo;                                    // 支付宝支付信息
    private String mPayWay;                                     // 支付方式

    public static final String WXPAY = "wxpay";
    public static final String ALIPAY = "alipay";
    public static final String YUEPAY = "yuepay";
    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int SDK_PAY_FLAG = 2000;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    final
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:
                    mLoadingDailog.dismiss();
                    switch (mPayWay) {

                        case WXPAY:
                            GlobalParameterApplication.mActivity = PayOrderActivity.this;
                            wxPay();
                            break;

                        case ALIPAY:
                            aliPay();
                            break;

                        case YUEPAY:
                            ToastUtil.show(mContext,(String) msg.obj);
                            LogUtils.i(TAG + "orderToPay SUCCESS " + (String) msg.obj);
                            GlobalParameterApplication.getInstance().PaySussToActivity(PayOrderActivity.this);
                            break;
                    }
                    break;

                case LOAD_DATA1_FAILE:
                    mLoadingDailog.dismiss();
                    LogUtils.i(TAG + "orderToPay FAILE " + (String) msg.obj);
                    ToastUtil.show(mContext,(String) msg.obj);
                    break;

                case NET_ERROR:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, "网络异常,请稍后再试");
                    break;

                case SDK_PAY_FLAG:

                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    String result = "";

                    // 判断resultStatus 为9000则代表支付成功
                    LogUtils.i(TAG + "orderToPay " + resultStatus);
                    if (TextUtils.equals(resultStatus, "9000")) { //支付成功
                        result = "支付成功";
                        GlobalParameterApplication.getInstance().PaySussToActivity(PayOrderActivity.this);
                        LogUtils.i(TAG + "orderToPay 支付成功");
                    } else if ("6001".equals(resultStatus)) {
                        result = "您取消了支付";
                        LogUtils.i(TAG + "orderToPay 您取消了支付");
                    } else {   // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        result = "支付失败";
                        LogUtils.i(TAG + "orderToPay 支付失败");
                    }
                    ToastUtil.show(mContext, result);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initDailog();
        initData();

    }

    private LoadingDailog mLoadingDailog;
    private void initDailog() {
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("加载中...")
                .setCancelable(false)
                .setCancelOutside(false);
        mLoadingDailog = loadBuilder.create();
    }

    private void initData() {

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        Intent intent = getIntent();
        if (intent != null) {

            mOrderID = intent.getStringExtra("OrderID");
            mTable = intent.getStringExtra("table");
            mNotify = intent.getStringExtra("notify");
            LogUtils.i(TAG + "initData mOrderID " + mOrderID);

            if (GlobalParameterApplication.getInstance().getPayIntention().equals(CommonParameters.ADD_MONEY)) {
                findViewById(R.id.ll_wallet_pay).setVisibility(View.GONE);
            }

        }
    }

    @OnClick({R.id.iv_back, R.id.ll_wx_pay, R.id.ll_zfb_pay, R.id.ll_wallet_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                break;
            case R.id.ll_wx_pay:
                mPayWay = WXPAY;
                mLoadingDailog.show();
                orderToPay();
                break;
            case R.id.ll_zfb_pay:
                mPayWay = ALIPAY;
                mLoadingDailog.show();
                orderToPay();
                break;
            case R.id.ll_wallet_pay:
                mPayWay = YUEPAY;
                mLoadingDailog.show();
                orderToPay();
                break;
        }
    }

    // 微信付款
    private void wxPay() {
        LogUtils.i(TAG + "orderToPay wxPay()");

        PayReq req = new PayReq();//PayReq就是订单信息对象
        //给req对象赋值
        req.appId = mPayBean.getAppid();//APPID
        req.partnerId = mPayBean.getPartnerid();//    商户号
        req.prepayId = mPayBean.getPrepayid();//  预付款ID
        req.nonceStr = mPayBean.getNoncestr();//随机数
        req.timeStamp = mPayBean.getTimestamp()+"";//时间戳
        req.packageValue = "Sign=WXPay";//固定值Sign=WXPay
        req.sign = mPayBean.getSign();//签名
        GlobalParameterApplication.mWxApi.sendReq(req);
    }

    // 支付宝付款
    private void aliPay() {
        LogUtils.i(TAG + "orderToPay aliPay()");

        final String orderInfo = mPayInfo;   // 订单信息
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(PayOrderActivity.this);
                Map <String,String> result = alipay.payV2(orderInfo,true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 发起支付
    private void orderToPay() {
        final String url = HttpURL.BASE_URL + HttpURL.PAY_TOPAY;
        LogUtils.i(TAG + "orderToPay url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "orderToPay result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "orderToPay msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");

                            switch (mPayWay) {

                                case WXPAY:
                                    mPayBean = new Gson().fromJson(data, PayBean.class);
                                    LogUtils.i(TAG + "orderToPay WXPAY  mPayBean " + mPayBean.getTimestamp());
                                    break;

                                case ALIPAY:
                                    mPayInfo = data;
                                    LogUtils.i(TAG + "orderToPay ALIPAY  mPayInfo " + mPayInfo);
                                    break;

                                case YUEPAY:
                                    LogUtils.i(TAG + "orderToPay YUEPAY ");
                                    break;
                            }

                            mHandler.obtainMessage(LOAD_DATA1_SUCCESS,msg).sendToTarget();
                            return;
                        }

                        mHandler.obtainMessage(LOAD_DATA1_FAILE,msg).sendToTarget();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "orderToPay volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.PAY_TOPAY + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "orderToPay token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("order_id", mOrderID);
                map.put("pay_way", mPayWay);
                map.put("table", mTable);
                map.put("notify", mNotify);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "orderToPay json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


}
