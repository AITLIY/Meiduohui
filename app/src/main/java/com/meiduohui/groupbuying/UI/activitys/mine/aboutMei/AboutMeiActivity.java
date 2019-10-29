package com.meiduohui.groupbuying.UI.activitys.mine.aboutMei;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.ConfigBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.PxUtils;
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

public class AboutMeiActivity extends AppCompatActivity {

    private String TAG = "AboutMeiActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private List<ConfigBean.Config> mConfigs;

    @BindView(R.id.iv_icon_mei)
    ImageView mIvIconMei;
    @BindView(R.id.tv_app_name)
    TextView mTvAppName;
    @BindView(R.id.tv_app_version)
    TextView mTvAppVersion;
    @BindView(R.id.tv_site_name)
    TextView mTvSiteName;
    @BindView(R.id.tv_site_copy)
    TextView mTvSiteCopy;

    private String mMobileNumber;

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

                    setResultData();
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
        setContentView(R.layout.activity_about_mei);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        getConfig();
    }

    @OnClick({R.id.iv_back, R.id.ll_use_help, R.id.ll_privacy_policy, R.id.ll_about_us, R.id.ll_contact_us})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.ll_use_help:
                startActivity(new Intent(this,UsingHelpActivity.class));
                break;

            case R.id.ll_privacy_policy:
                startActivity(new Intent(this,UsingHelpActivity.class));
                break;

            case R.id.ll_about_us:
                startActivity(new Intent(this,AboutUsActivity.class));
                break;

            case R.id.ll_contact_us:
                break;
        }
    }

    private void setResultData() {

        Glide.with(mContext)
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(PxUtils.dip2px(mContext,5))))
                .load(CommonParameters.APP_ICON)
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mIvIconMei);

        mTvAppVersion.setText(mConfigs.get(1).getApp_version());
        mTvSiteName.setText(mConfigs.get(3).getSite_name());
        mTvSiteCopy.setText(mConfigs.get(4).getSite_copy());
        mMobileNumber = mConfigs.get(2).getSite_mobile();

    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 个人中心首页
    private void getConfig() {

        String url = HttpURL.BASE_URL + HttpURL.SET_CONFIG;
        LogUtils.i(TAG + "getConfig url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getConfig result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getConfig msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            ConfigBean configBean = new Gson().fromJson(data, ConfigBean.class);
                            mConfigs = configBean.getConfig();

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getConfig mConfig.sizes " + mConfigs.size()
                                    + " name " + mConfigs.get(0).getApp_name()
                                    + " version " + mConfigs.get(1).getApp_version()
                                    + " mobile " + mConfigs.get(2).getSite_mobile()
                                    + " Site_name " + mConfigs.get(3).getSite_name()
                                    + " copy " + mConfigs.get(4).getSite_copy()
                            );
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getConfig volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SET_CONFIG + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getConfig token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("name", CommonParameters.APP_NAME + ","
                        + CommonParameters.APP_VERSION + ","
                        + CommonParameters.SITE_MOBILE + ","
                        + CommonParameters.SITE_NAME + ","
                        + CommonParameters.SITE_COPY);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getConfig json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
