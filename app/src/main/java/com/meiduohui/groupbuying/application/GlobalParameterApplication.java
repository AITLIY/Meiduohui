package com.meiduohui.groupbuying.application;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.utils.SpUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;

/**
 * Created by ALIY on 2018/12/9 0009.
 * 全局信息设置
 */

public class GlobalParameterApplication extends Application {

    private static GlobalParameterApplication instance;  //Application实例
    public static GlobalParameterApplication getInstance() {
        return instance;
    }

    private RequestQueue mRequestQueue;                 // Volley网络请求队列
    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
            mRequestQueue.getCache().clear();
        }
        return mRequestQueue;
    }

    public void setUserInfo(UserBean userInfo) {        //存储本地的用户对象
        SpUtils.putObject(getApplicationContext(), userInfo);
    }

    public UserBean getUserInfo() {                     //获取本地的用户对象
        return SpUtils.getObject(getApplicationContext(), UserBean.class);
    }

    public void clearUserInfo() {                       // 清空本地存储的用户信息
        SpUtils.clear(getApplicationContext());
    }

    public void setLoginStatus(boolean status) {        // 设置用户登录状态

        if (status)
            SpUtils.put(getApplicationContext(), CommonParameters.LOGINSTATUS, status);
        else
            SpUtils.put(getApplicationContext(), CommonParameters.LOGINSTATUS, status);
    }

    public boolean getLoginStatus() {                   // 获取用户登录状态
        return SpUtils.getSpBoolean(getApplicationContext(), CommonParameters.LOGINSTATUS, false);
    }

    public boolean getUserIsShop() {                    // 获取用户类型（是否是商户）

        boolean isShop = false;

        UserBean user = getUserInfo();

        if (user!=null && user.getShop_id().equals("1"))
            isShop = true;

        return isShop;
    }

    public static boolean isNeedRefresh = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        registToWX();
    }

    public static IWXAPI mWxApi;

//    private void registToWX() {
//        LogUtils.d("微信登录 : registToWX()");
//        //AppConst.WEIXIN.APP_ID是指你应用在微信开放平台上的AppID，记得替换。
//        mWxApi = WXAPIFactory.createWXAPI(this, CommonParameters.APPID, false);
//        // 将该app注册到微信
//        mWxApi.registerApp("wx3f889385b49ca1b8");
//    }

}
