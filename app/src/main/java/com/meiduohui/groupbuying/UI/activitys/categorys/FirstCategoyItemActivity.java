package com.meiduohui.groupbuying.UI.activitys.categorys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.GridSpacingItemDecoration;
import com.meiduohui.groupbuying.adapter.SecondCatItemAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CategoryBean;
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

public class FirstCategoyItemActivity extends AppCompatActivity {

    private String TAG = "FirstCategoyItemActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    private List<CategoryBean.SecondInfoBean> mSecondInfoBeans;      // 所有二级级分类
    private SecondCatItemAdapter mRvAdapter;

    @BindView(R.id.iv_img)
    ImageView iv_img;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.rv_item)
    RecyclerView rv_item;

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

                    initCategoryList();
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
        setContentView(R.layout.activity_first_categoy_item);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        Intent intent = getIntent();
        if (intent != null) {
            String firstID = intent.getStringExtra("ID");
            LogUtils.i(TAG + "initData firstID " + firstID);
            if (!TextUtils.isEmpty(firstID))
                getCatSesondData(firstID);
        }

    }

    //数据
    private void initCategoryList() {

        mRvAdapter = new SecondCatItemAdapter(mContext, mSecondInfoBeans, 0);
        GridLayoutManager layoutManage = new GridLayoutManager(mContext, 4);
        rv_item.setLayoutManager(layoutManage);
        rv_item.addItemDecoration(new GridSpacingItemDecoration(4, PxUtils.dip2px(mContext,15), true));
        rv_item.setAdapter(mRvAdapter);
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取二级分类
    private void getCatSesondData(final String id) {

        String url = HttpURL.BASE_URL + HttpURL.CAT_SECOND;
        LogUtils.i(TAG + "getCatSesondData url " + url);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getCatSesondData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getCatSesondData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mSecondInfoBeans = new Gson().fromJson(data, new TypeToken<List<CategoryBean.SecondInfoBean>>() {}.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getCatSesondData mCategoryBeans.size " + mSecondInfoBeans.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getCatSesondData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.CAT_SECOND + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getCatSesondData token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("pid", id);
                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getCatSesondData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


}
