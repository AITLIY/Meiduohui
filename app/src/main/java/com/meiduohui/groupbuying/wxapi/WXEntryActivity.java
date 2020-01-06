package com.meiduohui.groupbuying.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtils;
import com.meiduohui.groupbuying.utils.UnicodeUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private Context mContext;
    private RequestQueue requestQueue;
//    private WXUserBean mWXUserBean;
    private String TAG = "WXEntryActivity: ";

    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    private static final int LOAD_DATA_SUCCESS1 = 101;
    private static final int LOAD_DATA_FAILE1 = 102;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS1:

                    ToastUtils.show(mContext, "登录成功");
                    GlobalParameterApplication.getInstance().setLoginStatus(true);
                    GlobalParameterApplication.getInstance().refeshHomeActivity(WXEntryActivity.this);
                    break;

                case LOAD_DATA_FAILE1:

                    ToastUtils.show(mContext, "登录失败");
                    finish();
                    break;

                case NET_ERROR:

                    ToastUtils.show(mContext, "网络异常,请稍后重试");
                    finish();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        //如果没回调onResp，八成是这句没有写
        GlobalParameterApplication.mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        GlobalParameterApplication.mWxApi.handleIntent(intent, this);//必须调用此句话
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        LogUtils.d("微信登录 : onReq()");
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    // app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp resp) {
        LogUtils.d("微信登录 : onResp()");
//        LogUtils.d(resp.errStr);
        LogUtils.d("微信登录 : 错误码 : " + resp.errCode + "");
        switch (resp.errCode) {

            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == resp.getType()) {
                    ToastUtils.show(mContext,"分享失败");
                    finish();
                } else {
                    ToastUtils.show(mContext,"登录失败");
                    finish();
                }
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        //拿到了微信返回的code,立马再去请求access_token
                        String code = ((SendAuth.Resp) resp).code;
                        LogUtils.d("微信登录 : code = " + code);
                        //就在这个地方，用网络库什么的或者自己封的网络api，发请求去咯，注意是get请求
                        getWxLogin(code);
                        break;

                    case RETURN_MSG_TYPE_SHARE:
                        ToastUtils.show(mContext,"分享成功");
                        switch (GlobalParameterApplication.shareIntention) {

                            case CommonParameters.MAKE_MONEY:

                                break;

                            case CommonParameters.SHARE_SHOPS:
                            case CommonParameters.SHARE_MESSAGE:
                                GlobalParameterApplication.isShareSussess = true;
                                break;
                        }

                        finish();
                        break;
                }
                break;
        }
    }


    //--------------------------------------请求服务器数据-------------------------------------------


    // 微信登录
    private void getWxLogin(final String code) {

        String url = HttpURL.BASE_URL + HttpURL.LOGIN_WXLOGIN;
        LogUtils.i(TAG + "getWxLogin url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getWxLogin result " + s);

                    try {

                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getWxLogin msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            UserBean userInfo = new Gson().fromJson(data, UserBean.class);

                            GlobalParameterApplication.getInstance().setUserInfo(userInfo);

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS1);

                        }  else {

                            mHandler.sendEmptyMessage(LOAD_DATA_FAILE1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE1);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getWxLogin volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.LOGIN_WXLOGIN + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getWxLogin token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("code", code);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getWxLogin json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}

