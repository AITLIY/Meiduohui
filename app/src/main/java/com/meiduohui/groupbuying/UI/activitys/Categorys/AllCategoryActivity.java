package com.meiduohui.groupbuying.UI.activitys.Categorys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CategoryBean;
import com.meiduohui.groupbuying.bean.IndexBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllCategoryActivity extends AppCompatActivity {

    private String TAG = "AllCategoryActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    private List<CategoryBean.SecondInfoBean> mSecondInfoBeans;
    private List<CategoryBean> mCategoryBeans;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

//            switch (msg.what) {
//
//                case LOAD_DATA1_SUCCESS:
//
//                    if (mSearchType==SEARCH_LESSON_PARAMETER) {
//
//                        if (mLessonSearches.size()>0){
//                            setViewForResult(true,"");
//
//                        } else {
//                            setViewForResult(false,"没有您要找的课程信息~");
//                        }
//                    }
//                    break;
//
//                case LOAD_DATA1_FAILE:
//
//                    lesson_item_list.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            lesson_item_list.onRefreshComplete();
//                            setViewForResult(false,"查询数据失败~");
//                        }
//                    }, 1000);
//                    break;
//
//                case NET_ERROR:
//
//                    lesson_item_list.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            lesson_item_list.onRefreshComplete();
//                            setViewForResult(false,"网络异常,请稍后重试~");
//                        }
//                    }, 1000);
//                    break;
//            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category);

        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {

    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        getCatFirstData();
    }

    // 更新分类
//    private void initLessonCategoryList(List<LessonCategory> LessonCategorys) {
//        LogUtils.i("AllClassFragment: initLessonCategoryList size"  + LessonCategorys.size());
//        adapter2 = new LessonCategoryAdapter2(mContext, LessonCategorys,this);
//
//        final StaggeredGridLayoutManager staggeredGridLayoutManager = new NewStaggeredGridLayoutManager(4, OrientationHelper.VERTICAL);
//        lesson_category_rv.setLayoutManager(staggeredGridLayoutManager);
//        lesson_category_rv.setAdapter(adapter2);
//    }


    // 2.获取一级分类
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
                            JSONArray jsonDatas = new JSONArray(data);
                            JSONObject jsonSecond = (JSONObject) jsonDatas.get(0);
                            String second_info = jsonSecond.getString("second_info");

                            mCategoryBeans = new Gson().fromJson(data, new TypeToken<List<CategoryBean>>() {}.getType());
                            mSecondInfoBeans = new Gson().fromJson(second_info, new TypeToken<List<CategoryBean.SecondInfoBean>>() {}.getType());

                            LogUtils.i(TAG + "getCatFirstData mCategoryBeans.size " + mCategoryBeans.size()
                                    + " mSecondInfoBeans.size " + mSecondInfoBeans.size()
                            );
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
