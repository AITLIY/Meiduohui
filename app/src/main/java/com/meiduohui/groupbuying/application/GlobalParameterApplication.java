package com.meiduohui.groupbuying.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.location.Location;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.UI.activitys.HomepageActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.VipOrderListActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.wallet.MyWalletActivity;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.utils.SpUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by ALIY on 2018/12/9 0009.
 * 全局信息设置
 */

public class GlobalParameterApplication extends Application {

    private static GlobalParameterApplication instance;  // Application实例
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

    public void setUserInfo(UserBean userInfo) {        // 存储本地的用户对象
        SpUtils.putObject(getApplicationContext(), userInfo);
    }

    public UserBean getUserInfo() {                     // 获取本地的用户对象
        return SpUtils.getObject(getApplicationContext(), UserBean.class);
    }

    public void clearUserInfo() {                       // 清空本地存储的用户信息
        SpUtils.clear(getApplicationContext());
    }

    public void setLoginStatus(boolean status) {        // 设置用户登录状态

        if (status)
            SpUtils.put(getApplicationContext(), CommonParameters.LOGINSTATUS, status);
        else{
            clearUserInfo();
            SpUtils.put(getApplicationContext(), CommonParameters.LOGINSTATUS, status);
        }

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

    public static boolean isNeedJump = false;
    public static String jumpShopId;
    private boolean isNeedRefresh = false;

    public boolean isNeedRefresh() {                                // 获取首页刷新状态
        return isNeedRefresh;
    }

    public void setNeedRefresh(boolean needRefresh) {               // 设置首页是否需要刷新
        isNeedRefresh = needRefresh;
    }

    // 刷新首页
    public void refeshHomeActivity(Activity activity){
        setNeedRefresh(true);
        activity.startActivity(new Intent(this, HomepageActivity.class));
        activity.finish();
    }

    public static String shareIntention;
    public static boolean isShareSussess;
    private String payIntention = "";

    public String getPayIntention() {                               // 获取支付意图
        return payIntention;
    }

    public void setPayIntention(String payIntention) {              // 设置支付意图
        this.payIntention = payIntention;
    }

    public void PaySussToActivity(Activity activity){               // 支付成功后要跳转的页面

        switch (payIntention) {

            case CommonParameters.NEW_ORDER:
                activity.startActivity(new Intent(this, VipOrderListActivity.class));
                break;

            case CommonParameters.UNPAY_ORDER:
                activity.startActivity(new Intent(this, VipOrderListActivity.class));
                break;

            case CommonParameters.ADD_MONEY:
                activity.startActivity(new Intent(this, MyWalletActivity.class));
                break;

            case CommonParameters.PUBLISH_MSG:
                activity.startActivity(new Intent(this, HomepageActivity.class));
                break;
        }
        activity.finish();
    }

    public static Location mLocation;        // 地址

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registToWX();
    }

    public static IWXAPI mWxApi;

    private void registToWX() {
        LogUtils.d("微信登录 : registToWX()");
        //AppConst.WEIXIN.APP_ID是指你应用在微信开放平台上的AppID，记得替换。
        mWxApi = WXAPIFactory.createWXAPI(this, CommonParameters.APPID, false);
        // 将该app注册到微信
        mWxApi.registerApp(CommonParameters.APPID);
    }

}
