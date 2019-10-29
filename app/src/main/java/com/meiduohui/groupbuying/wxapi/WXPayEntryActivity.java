package com.meiduohui.groupbuying.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import com.android.volley.RequestQueue;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private Context mContext;
    private RequestQueue requestQueue;


    private static final int LOAD_DATA_SUCCESS = 201;
    private static final int LOAD_DATA_FAILE = 202;
    private static final int LOAD_DATA_FAILE2 = 203;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    ToastUtil.show(mContext, "支付成功");
//                    if (GlobalParameterApplication.attach.equals(CommonParameters.LESSON_ORDER)) {
//                        startActivity(new Intent(mContext, HomepageActivity.class));
//                    } else if (GlobalParameterApplication.attach.equals(CommonParameters.VIP_ORDER)) {
//                        startActivity(new Intent(mContext, VipServiceActivity.class));
//                    }

                    finish();
                    break;

                case LOAD_DATA_FAILE:

                    ToastUtil.show(mContext, "支付失败");
                    break;

                case LOAD_DATA_FAILE2:

                    ToastUtil.show(mContext, "用户取消");
                    break;

                case NET_ERROR:

                    ToastUtil.show(mContext, "网络异常,请稍后重试");
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay_entry);

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        GlobalParameterApplication.mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        GlobalParameterApplication.mWxApi.handleIntent(intent, this);
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        LogUtils.d("微信支付 onPayFinish, errCode = " + baseResp.errCode);

        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            int errCord = baseResp.errCode;
            if (errCord == 0) {
                LogUtils.d("微信支付 支付成功！");


            } else if (errCord == -1) {
                LogUtils.d("微信支付 支付失败");
                mHandler.sendEmptyMessage(LOAD_DATA_FAILE);
                finish();
            } else if (errCord == -2) {
                LogUtils.d("微信支付 用户取消");
                mHandler.sendEmptyMessage(LOAD_DATA_FAILE2);
                finish();
            }
            //这里接收到了返回的状态码可以进行相应的操作，如果不想在这个页面操作可以把状态码存在本地然后finish掉这个页面，这样就回到了你调起支付的那个页面
            //获取到你刚刚存到本地的状态码进行相应的操作就可以了
        }
    }


}
