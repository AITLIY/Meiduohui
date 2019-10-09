package com.meiduohui.groupbuying.UI.fragments.home;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CouponFragment extends Fragment {

    private String TAG = "HomeFragment: ";
    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;

    private Unbinder unbinder;

    @BindView(R.id.unused_tv)                                     // 未使用
    TextView unused_tv;
    @BindView(R.id.unused_v)
    View unused_v;

    @BindView(R.id.used_tv)                                       // 已使用
    TextView used_tv;
    @BindView(R.id.used_v)
    View used_v;

    @BindView(R.id.expired_tv)                                    // 已过期
    TextView expired_tv;
    @BindView(R.id.expired_v)
    View expired_v;

    @BindView(R.id.coupon_item_list)
    PullToRefreshListView coupon_item_list;

    private int page = 1;
    private static final int SEARCH_LESSON_PARAMETER  = 100;        //参数查询
    private static final int SEARCH_LESSON_PULL_UP = 200;           //上拉加载
    private int mSearchType = 100;  // 查询的标志

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

//                    if (mSearchType==SEARCH_LESSON_PARAMETER) {
//
//                        if (mLessonSearches.size()>0){
//                            setViewForResult(true,"");
//
//                        } else {
//                            setViewForResult(false,"没有您要找的课程信息~");
//                        }
//                    }

                    break;

                case LOAD_DATA1_FAILE:

//                    lesson_item_list.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            lesson_item_list.onRefreshComplete();
//                            setViewForResult(false,"查询数据失败~");
//                        }
//                    }, 1000);
                    break;
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
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coupon, container, false);
        unbinder = ButterKnife.bind(this,mView);

        init();

        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        initPullListView();
    }

    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

//        mShowList = new ArrayList<>();
//        adapter = new LessonListAdapter(mContext, mShowList);
//        lesson_item_list.setAdapter(adapter);

    }

    @OnClick({R.id.unused_rl,R.id.used_rl,R.id.expired_rl})
    public void onTopBarClick(View v) {

        switch (v.getId()) {

            case R.id.unused_rl:



                break;

            case R.id.used_rl:



                break;

            case R.id.expired_rl:


                break;
        }

        changeTabItemStyle(v);
    }

    // 初始化列表
    private void initPullListView() {

        // 1.设置模式
        coupon_item_list.setMode(PullToRefreshBase.Mode.BOTH);

        // 2.初始化列表控件上下拉的状态
        ILoadingLayout startLabels = coupon_item_list.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");           // 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");  // 刷新时
        startLabels.setReleaseLabel("放开刷新");        // 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = coupon_item_list.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");             // 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");    // 刷新时
        endLabels.setReleaseLabel("放开加载更多");      // 下来达到一定距离时，显示的提示

        // 3.设置监听事件
        coupon_item_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {  //拉动时
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page= 1;

                mSearchType = SEARCH_LESSON_PARAMETER;
//                getLessonData(mSort,mCateId,mKeyword, page); // 下拉刷新搜索

                setViewForResult(true,"");
                LogUtils.i("AllClassFragment: onPullDownToRefresh 下拉" + page + "页");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page++;

                mSearchType = SEARCH_LESSON_PULL_UP;
//                getLessonData(mSort,mCateId,mKeyword, page); // 上拉加载搜索

                LogUtils.i("AllClassFragment: onPullUpToRefresh 上拉" + page + "页");
            }
        });

        coupon_item_list.setOnItemClickListener(new AdapterView.OnItemClickListener() { //点击item时
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                    return;
                } else {
//                    RecommendLesson.LessonBean lessonBean = mShowList.get(position-1);
//                    LogUtils.i("AllClassFragment: ItemClick position " + position);
//                    Intent intent = new Intent(mContext,LessonActivity2.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("LessonBean",lessonBean);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
                }
            }
        });
    }


//    @Override
//    public void onSearch(LessonCategory.SonlistBean sonlistBean) {
//
//        isShowlessonfiltrate(false);
//        lesson_filtrate_tv.setText(sonlistBean.getName());
//
//        mCateId = sonlistBean.getId()+"";
//        mKeyword = "";
//        mSort = "";
//        page= 1;
//
//        mSearchType = SEARCH_LESSON_PARAMETER;
//        getLessonData(mSort,mCateId,mKeyword, page); // 通过id搜索
//    }


    // 根据获取结果显示view
    private void setViewForResult(boolean isSuccess,String msg) {

        if (isSuccess) {
            mView.findViewById(R.id.not_data).setVisibility(View.GONE);

        } else {
            mView.findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            ((TextView) mView.findViewById(R.id.not_data_tv)).setText(msg);
        }
    }

    // 更新课程列表数据
    private void upDataLessonListView() {

        switch (mSearchType) {

            case SEARCH_LESSON_PARAMETER:

//                mShowList.clear();
//                mShowList.addAll(mLessonSearches);
//                LogUtils.i("AllClassFragment: SEARCH_LESSON_FOR_PARAMETER "  + mShowList.size());
//
//                adapter.notifyDataSetChanged();
//                coupon_item_list.getRefreshableView().smoothScrollToPosition(0);//移动到首部

                break;

            case SEARCH_LESSON_PULL_UP:

//                mShowList.addAll(mLessonSearches);
//                //                adapter.addLast(mLessonSearches);
//                LogUtils.i("AllClassFragment: SEARCH_LESSON_PULL_UP " + mShowList.size());
//
//                adapter.notifyDataSetChanged();
//
//                if (mLessonSearches.size() == 0) {
//                    ToastUtil.show(mContext, "没有更多结果");
//                }

                break;
        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        unused_tv.setTextColor(view.getId() == R.id.unused_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.m_old_price));
        used_tv.setTextColor(view.getId() == R.id.used_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.m_old_price));
        expired_tv.setTextColor(view.getId() == R.id.expired_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.m_old_price));

        unused_v.setVisibility(view.getId() ==  R.id.unused_rl ? View.VISIBLE:View.GONE);
        used_v.setVisibility(view.getId() == R.id.used_rl ? View.VISIBLE:View.GONE);
        expired_v.setVisibility(view.getId() == R.id.expired_rl ? View.VISIBLE:View.GONE);
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

//                            String data = jsonResult.getString("data");
//                            mCategoryBeans = new Gson().fromJson(data, new TypeToken<List<CategoryBean>>() {}.getType());
//
//                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
//                            LogUtils.i(TAG + "getCatFirstData mCategoryBeans.size " + mCategoryBeans.size());
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
