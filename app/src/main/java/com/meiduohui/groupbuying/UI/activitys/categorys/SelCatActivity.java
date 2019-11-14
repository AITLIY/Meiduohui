package com.meiduohui.groupbuying.UI.activitys.categorys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.adapter.SelCatListAdapter;
import com.meiduohui.groupbuying.adapter.SelSecCatListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CategoryBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelCatActivity extends AppCompatActivity {

    private String TAG = "AllCategoryActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    private List<CategoryBean> mCategoryBeans;              // 所有一级分类（包含二级）
    private SelCatListAdapter mSelCatListAdapter;

    @BindView(R.id.rv_first_cat_list)
    RecyclerView mRecyclerView;

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
                    ToastUtil.show(mContext, "网络异常,请稍后再试");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sel_cat);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        getCatFirstData();
    }

    //数据
    private void initCategoryList() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setFocusableInTouchMode(false);

        mSelCatListAdapter = new SelCatListAdapter(this,mCategoryBeans);
        mSelCatListAdapter.setOnItemClickListener(new SelSecCatListAdapter.OnItemClickListener() {
            @Override
            public void onCallbackClick(String id1, String id2, String catName) {
                LogUtils.i(TAG + "initCategoryList id1 " + id1
                        + " id2 " + id2
                        + " catName " + catName);

                Intent intent = new Intent();
                intent.putExtra("cat_id1", id1);
                intent.putExtra("cat_id2", id2);
                intent.putExtra("catName", catName);
                setResult(RESULT_OK, intent);
                finish();
            }

        });
        mRecyclerView.setAdapter(mSelCatListAdapter);
        mSelCatListAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.iv_back)
    public void onBackClick(View v) {
        finish();
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取一级分类
    private void getCatFirstData() {

        String url = HttpURL.BASE_URL + HttpURL.CAT_FIRST;
        LogUtils.i(TAG + "getCatFirstData url " + url);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getCatFirstData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getCatFirstData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mCategoryBeans = new Gson().fromJson(data, new TypeToken<List<CategoryBean>>() {}.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getCatFirstData mCategoryBeans.size " + mCategoryBeans.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getCatFirstData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.CAT_FIRST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getCatFirstData token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getCatFirstData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
