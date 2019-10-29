package com.meiduohui.groupbuying.UI.activitys.mine.wallet;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.AddMoneyBean;
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

public class AddMoneyActivity extends AppCompatActivity {

    private String TAG = "AddMoneyActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.ed_money)
    EditText mEdMoney;

    private AddMoneyBean mAddMoneyBean;
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
                    //todo
//                    startActivity(new Intent(mContext, HomepageActivity.class));
                    finish();
                    ToastUtil.show(mContext, "充值成功");
                    break;

                case LOAD_DATA1_FAILE:

                    ToastUtil.show(mContext, "充值失败");
                    break;

                case NET_ERROR:

                    ToastUtil.show(mContext, "网络异常,请稍后再试");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

    }

    @OnClick({R.id.iv_back, R.id.tv_affirm})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:

                finish();
                break;

            case R.id.tv_affirm:

                String money = mEdMoney.getText().toString();

                if (!NetworkUtils.isConnected(mContext)) {
                    ToastUtil.show(mContext, "当前无网络");
                    return;
                }

                if (TextUtils.isEmpty(money)) {

                    ToastUtil.show(mContext, "请输入充值金额");
                    return;
                }

                addMoney(money);
                break;
        }
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 充值
    private void addMoney(final String money) {

        final String url = HttpURL.BASE_URL + HttpURL.ORDER_ADDMONEY;
        LogUtils.i(TAG + "addMoney url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "addMoney result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "addMoney msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mAddMoneyBean = new Gson().fromJson(data, AddMoneyBean.class);
                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            return;
                        }

                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);

                    } catch (JSONException e) {
                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "addMoney volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_ADDMONEY + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "addMoney token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("money", money);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "addMoney json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }
}
