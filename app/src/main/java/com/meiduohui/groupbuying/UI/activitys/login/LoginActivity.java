package com.meiduohui.groupbuying.UI.activitys.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.NetworkUtils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtils;
import com.meiduohui.groupbuying.utils.UnicodeUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private String TAG = "LoginActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    @BindView(R.id.mobile_ed)
    EditText mobile_ed;
    @BindView(R.id.password_ed)
    EditText password_ed;

    private final int LOAD_DATA_SUCCESS = 101;
    private final int LOAD_DATA_FAILED1 = 102;
    private final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, "登录成功");
                    GlobalParameterApplication.getInstance().setLoginStatus(true);
                    GlobalParameterApplication.getInstance().refeshHomeActivity(LoginActivity.this);
                    break;

                case LOAD_DATA_FAILED1:

                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case NET_ERROR:

                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, "网络异常,请稍后重试");
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//黑色
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//白色
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.white), true);
        ButterKnife.bind(this);

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
    }

    @OnClick({R.id.iv_back,R.id.login_tv,R.id.forget_password_tv,R.id.register_tv,R.id.ll_wx_login})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:
                finish();

            case R.id.login_tv:

                String mobile = mobile_ed.getText().toString();
                String password = password_ed.getText().toString();

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtils.show(mContext,"网络异常,请稍后重试");
                    return;
                }

                if (TextUtils.isEmpty(mobile)) {

                    ToastUtils.show(mContext,"手机号不能为空");
                    return;
                } else if (mobile.length()!=11) {

                    ToastUtils.show(mContext, "请输入正确手机号码");
                    return;
                } else if (TextUtils.isEmpty(password)) {

                    ToastUtils.show(mContext,"密码不能为空");
                    return;
                }

                mLoadingDailog.show();
                getLogin(mobile,password);
                break;

            case R.id.register_tv:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;

            case R.id.forget_password_tv:
                startActivity(new Intent(mContext, ForgetPwdActivity.class));
                break;

            case R.id.ll_wx_login:
                wxLogin();
                break;

        }

    }

    public void wxLogin() {

        LogUtils.d("微信登录 : wxLogin()");
        if (!GlobalParameterApplication.mWxApi.isWXAppInstalled()) {
            ToastUtils.show(mContext,"您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login";
        GlobalParameterApplication.mWxApi.sendReq(req);
    }


    //--------------------------------------请求服务器数据--------------------------------------------

    // 登录
    private void getLogin(final String mobile,final String password) {

        final String url = HttpURL.BASE_URL + HttpURL.LOGIN_LOGIN;
        LogUtils.i(TAG + "getLogin url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getLogin result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getLogin msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            UserBean userInfo = new Gson().fromJson(data, UserBean.class);
                            GlobalParameterApplication.getInstance().setUserInfo(userInfo);

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS);
                            return;
                        }

                        mHandler.obtainMessage(LOAD_DATA_FAILED1,msg).sendToTarget();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getLogin volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.LOGIN_LOGIN + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getLogin token " + token);
                String md5_token = MD5Utils.md5(token);
                String pass = MD5Utils.md5(password);

                map.put("mobile", mobile);
                map.put("pass", pass);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getLogin json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
