package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CategoryBean;
import com.meiduohui.groupbuying.bean.CouponBean;
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

import butterknife.ButterKnife;

public class MessageDetailsActivity extends AppCompatActivity {

    private String TAG = "MessageDetailsActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    private IndexBean.MessageInfoBean mMessageInfoBean;
    private Location mLocation;

    private List<ShopInfoBean.MInfoBean> mMInfoBeans;
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

//                    initCategoryList();
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
            mMessageInfoBean = (IndexBean.MessageInfoBean) bundle.getSerializable("MessageInfoBean");
            mLocation = (Location) bundle.getParcelable("Location");

            LogUtils.i(TAG + "initData Shop_name " + mMessageInfoBean.getShop_name());
            getShopInfoData();
        }

    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取一级分类
    private void getShopInfoData() {

        String url = HttpURL.BASE_URL + HttpURL.SHOP_SHOPINFO;
        LogUtils.i(TAG + "getShopInfoData url " + url);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
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
//                            mCategoryBeans = new Gson().fromJson(data, new TypeToken<List<CategoryBean>>() {}.getType());
//
//                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
//                            LogUtils.i(TAG + "getShopInfoData mCategoryBeans.size " + mCategoryBeans.size());
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

                map.put("m_id", mMessageInfoBean.getOrder_id());
                map.put("lon", mLocation.getLongitude()+"");
                map.put("lat", mLocation.getLatitude()+"");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getShopInfoData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }
}
