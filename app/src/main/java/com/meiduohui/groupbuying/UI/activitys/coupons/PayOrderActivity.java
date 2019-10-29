package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.tencent.mm.opensdk.modelpay.PayReq;

public class PayOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order);
    }

    private void wxPay() {

//        PayReq req = new PayReq();//PayReq就是订单信息对象
//        //给req对象赋值
//        req.appId = mWxPayBean.getAppid();//APPID
//        req.partnerId = mWxPayBean.getPartnerid();//    商户号
//        req.prepayId = mWxPayBean.getPrepayid();//  预付款ID
//        req.nonceStr = mWxPayBean.getNoncestr();//随机数
//        req.timeStamp = mWxPayBean.getTimestamp()+"";//时间戳
//        req.packageValue = "Sign=WXPay";//固定值Sign=WXPay
//        req.sign = mWxPayBean.getSign();//签名
//        GlobalParameterApplication.mWxApi.sendReq(req);
//        finish();
    }
}
