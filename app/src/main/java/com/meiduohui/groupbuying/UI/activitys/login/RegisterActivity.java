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
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.githang.statusbar.StatusBarCompat;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "RegisterActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    @BindView(R.id.invite_code_ed)
    EditText invite_code_ed;
    @BindView(R.id.username_ed)
    EditText username_ed;
    @BindView(R.id.phone_number_ed)
    EditText phone_number_ed;
    @BindView(R.id.password_ed)
    EditText password_ed;
    @BindView(R.id.affirm_password_ed)
    EditText affirm_password_ed;
    @BindView(R.id.captcha_ed)
    EditText captcha_ed;

    @BindView(R.id.get_captcha_tv)
    TextView get_captcha_tv;

    private String CAPTCHA_ID = "";

    private final int LOAD_DATA_SUCCESS1 = 101;
    private final int LOAD_DATA_FAILED1 = 102;
    private final int LOAD_DATA_SUCCESS2 = 201;
    private final int LOAD_DATA_FAILED2 = 202;
    private final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS1:

                    mLoadingDailog.dismiss();
                    time = 60;
                    mHandler.postDelayed(myTask, 0);
                    break;

                case LOAD_DATA_FAILED1:

                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, "获取失败");
                    break;

                case LOAD_DATA_SUCCESS2:

                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, "注册成功");
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    break;

                case LOAD_DATA_FAILED2:

                    mLoadingDailog.dismiss();
                    String text = (String) msg.obj;
                    ToastUtil.show(mContext, text);
                    break;

                case NET_ERROR:

                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, "网络异常,请稍后重试");
                    break;

            }
        }
    };

    private long time;
    private Runnable myTask = new Runnable() {

        @Override
        public void run() {
            get_captcha_tv.setText("重新发送("+time+")");        // 提示剩余时间
            get_captcha_tv.setEnabled(false);                    //禁止再次点击发送验证码

            time--;                                                    //默认最大为60每隔一秒发送一个
            if (time >= 0) {
                mHandler.postDelayed(this, 1000);
            } else {
                get_captcha_tv.setText(R.string.get_captcha);
                get_captcha_tv.setEnabled(true);
                mHandler.removeCallbacksAndMessages(null);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.white), true);

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

        Intent intent = getIntent();
        if (intent!=null) {
            String mobile = intent.getStringExtra("mobile");
            invite_code_ed.setText(mobile);
        }

    }

    @OnClick({R.id.iv_back,R.id.get_captcha_tv,R.id.register_tv,R.id.ll_goto_login})
    public void onClick(View v) {

        String invite = invite_code_ed.getText().toString();
        String name = username_ed.getText().toString();
        String mobile = phone_number_ed.getText().toString();
        String password = password_ed.getText().toString();
        String affirmPwd = affirm_password_ed.getText().toString();
        String captcha = captcha_ed.getText().toString();

        switch (v.getId()) {

            case R.id.iv_back:
                finish();
                break;

            case R.id.get_captcha_tv:

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                if (TextUtils.isEmpty(mobile)) {

                    ToastUtil.show(mContext,"手机号不能为空");
                    return;
                }else if (mobile.length()!=11) {

                    ToastUtil.show(mContext, "请输入正确手机号码");
                    return;
                }

                mLoadingDailog.show();
                getCaptcha(mobile);
                break;

            case R.id.register_tv:

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                if (TextUtils.isEmpty(name)) {

                    ToastUtil.show(mContext,"昵称不能为空");
                    return;
                } else if (TextUtils.isEmpty(mobile)) {

                    ToastUtil.show(mContext,"手机号不能为空");
                    return;
                } else if (mobile.length()!=11) {

                    ToastUtil.show(mContext,"请输入正确手机号码");
                    return;
                }else if (TextUtils.isEmpty(password)) {

                    ToastUtil.show(mContext,"登录密码不能为空");
                    return;
                }else if (password.length() < 6) {

                    ToastUtil.show(mContext,"密码需大于6位数");
                    return;
                } else if (!password.equals(affirmPwd)) {

                    ToastUtil.show(mContext,"两次输入的密码不一致");
                    return;
                }else if (TextUtils.isEmpty(captcha)) {

                    ToastUtil.show(mContext,"验证码不能为空");
                    return;
                } else if (captcha.length()!=4) {

                    ToastUtil.show(mContext,"请输入正确验证码");
                    return;
                }

                mLoadingDailog.show();
                register(invite,name,mobile,password,affirmPwd,captcha);
                break;

            case R.id.ll_goto_login:

                finish();
                break;
        }
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 1.获取手机验证码
    private void getCaptcha(final String mobile) {

        final String url = HttpURL.BASE_URL + HttpURL.LOGIN_GETCAPTCHA;
        LogUtils.i(TAG + "getCaptcha url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getCaptcha result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getCaptcha msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            JSONObject jsonData = new JSONObject(data);
                            CAPTCHA_ID = jsonData.getString("id");

                            LogUtils.i(TAG + "getCaptcha captcha_id " + CAPTCHA_ID);

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS1);
                            return;
                        }
                        mHandler.sendEmptyMessage(LOAD_DATA_FAILED1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getCaptcha volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.LOGIN_GETCAPTCHA + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getCaptcha token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mobile", mobile);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getCaptcha json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 2.注册用户
    private void register(final String invite,final String name,final String mobile,final String password,final String affirmPwd,final String captcha) {

        final String url = HttpURL.BASE_URL + HttpURL.LOGIN_REGISTER;
        LogUtils.i(TAG + "getRegister url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getRegister result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getRegister msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            mHandler.sendEmptyMessage(LOAD_DATA_SUCCESS2);
                            return;
                        }

                        mHandler.obtainMessage(LOAD_DATA_FAILED2,msg).sendToTarget();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getRegister volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.LOGIN_REGISTER + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getRegister token " + token);
                String md5_token = MD5Utils.md5(token);
                String pass = MD5Utils.md5(password);
                String rel_pass = MD5Utils.md5(affirmPwd);

                map.put("invite", invite);
                map.put("name", name);
                map.put("mobile", mobile);
                map.put("pass", pass);
                map.put("rel_pass", rel_pass);
                map.put("captcha_id", CAPTCHA_ID);
                map.put("captcha", captcha);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getRegister json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
