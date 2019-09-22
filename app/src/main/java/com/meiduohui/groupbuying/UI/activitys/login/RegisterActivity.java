package com.meiduohui.groupbuying.UI.activitys.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.HomepageActivity;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.utils.ToastUtil;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private RequestQueue requestQueue;
    private EditText username_ed,password_ed;
    private LinearLayout login_ll,weixin_login_ll;
    private TextView forget_password,register_new_user;
    private LinearLayout ll_goto_login;

    private static final int LOAD_DATA_SUCCESS = 101;
    private static final int LOAD_DATA_FAILE1 = 102;
    private static final int LOAD_DATA_FAILE2 = 103;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA_SUCCESS:

                    ToastUtil.show(mContext, "登录成功");
                    GlobalParameterApplication.getInstance().setLoginStatus(true);
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
        setContentView(R.layout.activity_register);
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        init();
    }

    private void init() {
        initView();
        initListner();
    }


    private void initView() {

        //        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                finish();
        //            }
        //        });

        ll_goto_login = findViewById(R.id.ll_goto_login);

    }

    private void initListner() {

        ll_goto_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //            case R.id.ll_goto_login:
            //
            //                if (!NetworkUtils.isConnected(mContext)){
            //                    ToastUtil.show(mContext,"当前无网络");
            //                    return;
            //                }
            //
            //                String userName = username_ed.getText().toString();
            //                String passWord = password_ed.getText().toString();
            //
            //                if ("".equals(userName)||"".equals(passWord)) {
            //
            //                    ToastUtil.show(mContext,"用户账号或密码不能为空");
            //                    return;
            //                }
            //
            //                if (passWord.length() < 6 || passWord.length() > 12) {
            //
            //                    ToastUtil.show(mContext,"密码长度应为6~12位");
            //                    return;
            //                }
            //
            //                userLogin(userName, passWord);
            //
            //                break;

            //            case R.id.weixin_login_ll:
            //
            //                wxLogin();
            //                break;
            //
            //            case R.id.forget_password:
            //
            //                startActivity(new Intent(mContext, ForgetPasswordActivity1.class));
            //                break;
            //
            case R.id.ll_goto_login:

                startActivity(new Intent(mContext, LoginActivity.class));
                break;
        }
    }
}
