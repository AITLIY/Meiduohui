package com.meiduohui.groupbuying.UI.activitys.mine.aboutMei;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.meiduohui.groupbuying.utils.ToastUtils;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutMeiActivity extends AppCompatActivity {

    private String TAG = "AboutMeiActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private ConfigBean mConfigBean = new ConfigBean();

    @BindView(R.id.iv_is_need_updata)
    ImageView mIvIsNeedUpdata;
    @BindView(R.id.tv_updata_text)
    TextView mTvUpdataText;
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
    private static final int LOAD_DATA1_FAILED = 102;
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

                case LOAD_DATA1_FAILED:

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

    @OnClick({R.id.iv_back, R.id.ll_check_updata, R.id.ll_use_help, R.id.ll_privacy_policy, R.id.ll_about_us, R.id.ll_contact_us})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.ll_check_updata:

                if (mIsNeed) {

                    Intent intent= new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(mConfigBean.getAndroid_download_url());
                    intent.setData(content_url);
                    startActivity(intent);
                } else {
                    return;
                }
                break;

            case R.id.ll_use_help:
                startActivity(new Intent(this, UsingHelpActivity.class));
                break;

            case R.id.ll_privacy_policy:
                startActivity(new Intent(this, PrivacyPolicyActivity.class));
                break;

            case R.id.ll_about_us:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;

            case R.id.ll_contact_us:
                showCallSelect();
                break;
        }
    }

    private PopupWindow popupWindow;

    public void showCallSelect() {

        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.alpha = 0.6f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        window.setAttributes(wl);

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_call, null);

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                Window window = getWindow();
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                window.setAttributes(wl);
            }
        });

        TextView call = view.findViewById(R.id.tv_call_number);

        call.setText("拨打：" + mMobileNumber);

        // 打电话
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCall();
                popupWindow.dismiss();

            }
        });

        // 取消
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, -view.getHeight());
    }


    private static final int CALL_PHONE = 1000;

    // 打电话
    private void getCall() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mMobileNumber)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LogUtils.i(TAG + " onRequestPermissionsResult SUCCESS");

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mMobileNumber)));

                } else {
                    LogUtils.i(TAG + " onRequestPermissionsResult FAILED");
                    ToastUtils.show(mContext, "您已取消授权，无法打电话");
                }
                break;

        }
    }

    private boolean mIsNeed;

    private void setResultData() {

        Glide.with(mContext)
                .applyDefaultRequestOptions(RequestOptions.bitmapTransform(new RoundedCorners(PxUtils.dip2px(mContext, 15))))
                .load(CommonParameters.APP_ICON)
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mIvIconMei);

        mIsNeed = compareVersion(mConfigBean.getAndroid_version(),getVersionName());

        if (mIsNeed) {

            mIvIsNeedUpdata.setVisibility(View.VISIBLE);
            mTvUpdataText.setText("发现新版本");
        } else {

            mIvIsNeedUpdata.setVisibility(View.GONE);
            mTvUpdataText.setText("已是最新版本");
        }

        mTvAppVersion.setText("v" + getVersionName());
        mTvSiteName.setText(mConfigBean.getSite_name());
        mTvSiteCopy.setText(mConfigBean.getSite_copy());
        mMobileNumber = mConfigBean.getSite_mobile();

    }

    private boolean compareVersion(String version1, String version2) {
        String[] numbers1 = version1.split("\\.");
        String[] numbers2 = version2.split("\\.");

        LogUtils.d(TAG + "getConfig compareVersion version1 " + version1
                + " version2  " + version2);
        LogUtils.d(TAG + "getConfig compareVersion numbers1.length " + numbers1.length
                + " numbers2.length  " + numbers2.length);

        if (numbers1.length > 2 && numbers2.length > 2) {

            if (Integer.parseInt(numbers1[0]) > Integer.parseInt(numbers2[0]))
                return true;

            else if (Integer.parseInt(numbers1[0]) == Integer.parseInt(numbers2[0])) {

                if (Integer.parseInt(numbers1[1]) > Integer.parseInt(numbers2[1]))
                    return true;

                else if (Integer.parseInt(numbers1[1]) == Integer.parseInt(numbers2[1])) {

                    if (Integer.parseInt(numbers1[2]) > Integer.parseInt(numbers2[2]))
                        return true;
                }
            }
        }
        return false;
    }

    public String getVersionName() {
        //Package manager
        PackageManager packageManager = getPackageManager();
        //getPackageName()
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            LogUtils.e(TAG + "getConfig NameNotFoundException " + e.toString());
            return "";
        }
        return packInfo.versionName;
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
                            mConfigBean = new Gson().fromJson(data, ConfigBean.class);

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
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
                        + CommonParameters.ANDROID_VERSION + ","
                        + CommonParameters.ANDROID_DOWNLOAD_URL + ","
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
