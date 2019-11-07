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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.GridSpacingItemDecoration;
import com.meiduohui.groupbuying.adapter.MsgSearchListAdapter;
import com.meiduohui.groupbuying.adapter.SecondCatListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CategoryBean;
import com.meiduohui.groupbuying.bean.IndexBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.PxUtils;
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

public class SecCategoyActivity extends AppCompatActivity {


    private String TAG = "SecCategoyActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.iv_img)
    ImageView iv_img;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.rv_second_cat_list)
    RecyclerView mRecyclerView;

    private List<CategoryBean.SecondInfoBean> mSecondInfoBeans;      // 所有二级级分类
    private SecondCatListAdapter mSecondCatListAdapter;

    private int cat_id1;
    private int cat_id2;

    @BindView(R.id.ptr_coupon_list)
    PullToRefreshListView mPullToRefreshListView;

    private boolean mIsPullUp = false;
    private int mPage = 1;

    private List<IndexBean.MessageInfoBean> mMessageInfos;                // 推荐列表集合
    private List<IndexBean.MessageInfoBean> mShowList;                    // 推荐列表集合更多

    private MsgSearchListAdapter mAdapter;

    private final int IS_RECOMMEND = 2;          // 推荐

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILE = 202;
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
                case LOAD_DATA2_SUCCESS:

                    if (!mIsPullUp) {

                        if (mMessageInfos.size() > 0) {
                            setViewForResult(true, "");

                        } else {
                            setViewForResult(false, "没有信息~");
                        }
                    }
                    updataListView();
                    break;

                case LOAD_DATA2_FAILE:

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
        setContentView(R.layout.activity_sec_categoy);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
        initPullListView();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        Intent intent = getIntent();
        if (intent != null) {
            String id1 = intent.getStringExtra("cat_id1");
            LogUtils.i(TAG + "initData id1 " + id1);

            getCatSesondData(id1);
            cat_id1 = Integer.parseInt(id1);
            getIndexData();
        }

        mShowList = new ArrayList<>();
        mAdapter = new MsgSearchListAdapter(mContext, mShowList);
        mAdapter.setOnItemClickListener(new MsgSearchListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onComment(int position) {

            }

            @Override
            public void onZF(int position) {

            }

            @Override
            public void onZan(int position) {

            }
        });
        mPullToRefreshListView.setAdapter(mAdapter);

    }

    // 数据
    private void initCategoryList() {

        mSecondCatListAdapter = new SecondCatListAdapter(mContext, mSecondInfoBeans, 0);
        mSecondCatListAdapter.setOnItemClickListener(new SecondCatListAdapter.OnItemClickListener() {
            @Override
            public void onCallbackClick(int FirPos, int SecPos, String catName) {
                cat_id1 = FirPos;
                cat_id2 = SecPos;
                Intent intent = new Intent(mContext, MessageListActivity.class);
                intent.putExtra("cat_id1", cat_id1);
                intent.putExtra("cat_id2", cat_id2);
                startActivity(intent);
            }
        });
        GridLayoutManager layoutManage = new GridLayoutManager(mContext, 4);
        mRecyclerView.setLayoutManager(layoutManage);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, PxUtils.dip2px(mContext, 15), true));
        mRecyclerView.setAdapter(mSecondCatListAdapter);
    }

    // 初始化列表
    private void initPullListView() {

        // 1.设置模式
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        // 2.初始化列表控件上下拉的状态
        ILoadingLayout startLabels = mPullToRefreshListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");           // 刚下拉时，显示的提示
        startLabels.setRefreshingLabel("正在刷新...");  // 刷新时
        startLabels.setReleaseLabel("放开刷新");        // 下来达到一定距离时，显示的提示

        ILoadingLayout endLabels = mPullToRefreshListView.getLoadingLayoutProxy(false, true);
        endLabels.setPullLabel("上拉加载");             // 刚下拉时，显示的提示
        endLabels.setRefreshingLabel("正在载入...");    // 刷新时
        endLabels.setReleaseLabel("放开加载更多");      // 下来达到一定距离时，显示的提示

        // 3.设置监听事件
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {  //拉动时
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                addtoTop();         // 请求网络数据
                refreshComplete();  // 数据加载完成后，关闭header,footer
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                addtoBottom();      //  请求网络数据
                refreshComplete();  // 数据加载完成后，关闭header,footer
            }
        });

        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //点击item时
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
    }

    // 下拉刷新的方法:
    public void addtoTop() {
        mPage = 1;
        mIsPullUp = false;
        getIndexData();     // 下拉刷新；
    }

    // 上拉加载的方法:
    public void addtoBottom() {
        mPage++;
        mIsPullUp = true;
        getIndexData();     // 加载更多；
    }

    // 刷新完成时关闭
    public void refreshComplete() {

        mPullToRefreshListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshListView.onRefreshComplete();
            }
        }, 1000);
    }

    // 根据获取结果显示view
    private void setViewForResult(boolean isSuccess, String msg) {

        if (isSuccess) {
            findViewById(R.id.not_data).setVisibility(View.GONE);

        } else {
            findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.not_data_tv)).setText(msg);
        }
    }

    // 更新列表数据
    private void updataListView() {

        if (!mIsPullUp) {

            mShowList.clear();
            mShowList.addAll(mMessageInfos);

            mAdapter.notifyDataSetChanged();
            //            mPullToRefreshListView.getRefreshableView().smoothScrollToPosition(0);//移动到首部
        } else {

            mShowList.addAll(mMessageInfos);
            mAdapter.notifyDataSetChanged();
            if (mMessageInfos.size() == 0) {
                ToastUtil.show(mContext, "没有更多结果");
            }
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_search_site})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_search_site:
                Intent intent = new Intent(mContext, MessageListActivity.class);
                intent.putExtra("search",0);
                startActivity(intent);
                break;
        }
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取二级分类
    private void getCatSesondData(final String id) {

        String url = HttpURL.BASE_URL + HttpURL.CAT_SECOND;
        LogUtils.i(TAG + "getCatSesondData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                            mSecondInfoBeans = new Gson().fromJson(data, new TypeToken<List<CategoryBean.SecondInfoBean>>() {
                            }.getType());

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

    // 获取首页数据
    private void getIndexData() {

        final String url = HttpURL.BASE_URL + HttpURL.INDEX_INDEX;
        LogUtils.i(TAG + "getIndexData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getIndexData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getIndexData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");

                            IndexBean mIndexBean = new Gson().fromJson(data, IndexBean.class);
                            mMessageInfos = mIndexBean.getMessage_info();

                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);
                            return;
                        }
                        mHandler.sendEmptyMessage(LOAD_DATA2_FAILE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA2_FAILE);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getIndexData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.INDEX_INDEX + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getIndexData token " + token);
                String md5_token = MD5Utils.md5(token);

                if (GlobalParameterApplication.mLocation != null) {
                    map.put("lat", GlobalParameterApplication.mLocation.getLatitude() + "");
                    map.put("lon", GlobalParameterApplication.mLocation.getLongitude() + "");
                }

                map.put("cat_id1", cat_id1 + "");
                if (cat_id2 != 0)
                    map.put("cat_id2", cat_id2 + "");
                if (mUserBean != null)
                    map.put("mem_id", mUserBean.getId());

                map.put("tui", IS_RECOMMEND + "");
                map.put("page", mPage + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getIndexData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
