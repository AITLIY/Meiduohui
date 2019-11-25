package com.meiduohui.groupbuying.UI.activitys.mine.aboutMei;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.webkit.WebView;

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
import com.meiduohui.groupbuying.bean.ArticleBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UsingHelpActivity extends AppCompatActivity {

    private String TAG = "AboutMeiActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    @BindView(R.id.webView)
    WebView mWebView;

    private ArticleBean.ArticleInfoBean mArticleInfoBean;

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

                   setContent();
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
        setContentView(R.layout.activity_using_help);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        getArticle();
    }

    @OnClick(R.id.iv_back)
    public void onClick() {
        finish();
    }

    private void setContent() {
        mWebView.loadDataWithBaseURL(null,mArticleInfoBean.getContent(),"text/html","uft-8",null);
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 文章详情
    private void getArticle() {

        String url = HttpURL.BASE_URL + HttpURL.SET_ARTICLE;
        LogUtils.i(TAG + "getArticle url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getArticle result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getArticle msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            ArticleBean articleBean = new Gson().fromJson(data, ArticleBean.class);
                            mArticleInfoBean = articleBean.getArticle_info();

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getArticle " + mArticleInfoBean.getIntro());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getArticle volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SET_ARTICLE + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getArticle token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("title", CommonParameters.USING_HELP);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getArticle json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
