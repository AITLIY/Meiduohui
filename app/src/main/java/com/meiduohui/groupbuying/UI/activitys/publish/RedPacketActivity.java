package com.meiduohui.groupbuying.UI.activitys.publish;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
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

public class RedPacketActivity extends AppCompatActivity {

    private String TAG = "RedPacketActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.ed_total)
    EditText mEdTotal;
    @BindView(R.id.ed_max)
    EditText mEdMax;
    @BindView(R.id.ed_min)
    EditText mEdMin;
    @BindView(R.id.ed_number)
    EditText mEdNumber;

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
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, (String) msg.obj);
                    finish();
                    break;

                case LOAD_DATA1_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, (String) msg.obj);
                    break;

                case NET_ERROR:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, "网络异常,请稍后重试");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_packet);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

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
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();
    }

    @OnClick({R.id.iv_back, R.id.tv_publish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_publish:

                String total = mEdTotal.getText().toString();
                String max = mEdMax.getText().toString();
                String min = mEdMin.getText().toString();
                String number = mEdNumber.getText().toString();

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                if (TextUtils.isEmpty(total)) {

                    ToastUtil.show(mContext,"请输入红包总额");
                    return;
                } else if (TextUtils.isEmpty(max)) {

                    ToastUtil.show(mContext,"请输入用户最大领取金额");
                    return;
                } else if (TextUtils.isEmpty(min)) {

                    ToastUtil.show(mContext,"请输入用户最小领取金额");
                    return;
                } else if (TextUtils.isEmpty(number)) {

                    ToastUtil.show(mContext,"请输入红包个数");
                    return;
                }

                mLoadingDailog.show();
                sendRed(total,max,min,number);
                break;
        }
    }


    //--------------------------------------请求服务器数据--------------------------------------------

    // 发布红包
    private void sendRed(final String total, final String max, final String min, final String number) {

        final String url = HttpURL.BASE_URL + HttpURL.SHOP_SENDRED;
        LogUtils.i(TAG + "sendRed url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "sendRed result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "sendRed msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(LOAD_DATA1_SUCCESS,msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(LOAD_DATA1_FAILED,msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "sendRed volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SHOP_SENDRED + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "sendRed token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("title", "商家红包");
                map.put("total", total);
                map.put("max", max);
                map.put("min", min);
                map.put("number", number);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "sendRed json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


}
