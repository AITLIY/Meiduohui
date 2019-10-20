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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.HomepageActivity;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.NetworkUtils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

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
    private final int LOAD_DATA_FAILE1 = 102;
    private final int LOAD_DATA_FAILE2 = 103;
    private final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    ToastUtil.show(mContext, "登录成功");
                    GlobalParameterApplication.getInstance().setLoginStatus(true);
                    GlobalParameterApplication.getInstance().refeshHomeActivity(LoginActivity.this);
                    startActivity(new Intent(mContext, HomepageActivity.class));
                    finish();
                    break;

                case LOAD_DATA_FAILE1:

                    String text = (String) msg.obj;
                    LogUtils.i("LoginActivity: text " + text);
                    ToastUtil.show(mContext, text);
                    break;

                case LOAD_DATA_FAILE2:

                    ToastUtil.show(mContext, "登录失败");
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
        setContentView(R.layout.activity_login);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//黑色
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);//白色
        StatusBarUtil.setTranslucentForImageView(this, 50, findViewById(R.id.needOffsetView));
        ButterKnife.bind(this);

        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        init();
    }

    private void init() {
        initView();
    }


    private void initView() {

//        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

    }

    @OnClick({R.id.login_tv,R.id.forget_password_tv,R.id.ll_goto_register})
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_tv:

                String mobile = mobile_ed.getText().toString();
                String password = password_ed.getText().toString();

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                if (TextUtils.isEmpty(mobile)) {

                    ToastUtil.show(mContext,"手机号不能为空");
                    return;
                } else if (mobile.length()!=11) {

                    ToastUtil.show(mContext, "请输入正确手机号码");
                    return;
                } else if (TextUtils.isEmpty(password)) {

                    ToastUtil.show(mContext,"密码不能为空");
                    return;
                }

                getLogin(mobile,password);
                break;

            case R.id.forget_password_tv:

                break;

            case R.id.ll_goto_register:

                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
        }
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

                        mHandler.obtainMessage(LOAD_DATA_FAILE1,msg).sendToTarget();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILE2);
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
