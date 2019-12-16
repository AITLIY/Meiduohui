package com.meiduohui.groupbuying.UI.activitys.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.coupons.MessageDetailsActivity;
import com.meiduohui.groupbuying.adapter.ShopInfoListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.ShopInfoBean;
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

public class CollectListActivity extends AppCompatActivity {

    private String TAG = "CollectListActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.swipelistview)
    SwipeMenuListView mSwipeListView;
    @BindView(R.id.PullToRefreshScroll_View)
    PullToRefreshScrollView mPullToRefreshScrollView;

    private int mPage = 1;
    private boolean mIsPullUp = false;

    private ArrayList<ShopInfoBean> mShopInfoBeans = new ArrayList<>();         // 搜索结果列表
    private ArrayList<ShopInfoBean> mShowList = new ArrayList<>();              // 显示的列表
    private ShopInfoListAdapter mAdapter;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILED = 102;
    private static final int NET_ERROR = 404;
    private static final int MEM_COLLECTDEL_RESULT_SUCCESS = 401;
    private static final int MEM_COLLECTDEL_RESULT_FAILED = 402;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    if (!mIsPullUp) {

                        if (mShopInfoBeans.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "没有收藏~");
                        }
                    }
                    updataListView();
                    break;

                case LOAD_DATA1_FAILED:

                    setViewForResult(false, "查询数据失败~");
                    break;

                case MEM_COLLECTDEL_RESULT_SUCCESS:
                    mLoadingDailog.dismiss();
                    addtoTop();
                    ToastUtil.show(mContext,(String) msg.obj);
                    break;

                case MEM_COLLECTDEL_RESULT_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext,(String) msg.obj);
                    break;

                case NET_ERROR:
                    mLoadingDailog.dismiss();
                    setViewForResult(false, "网络异常,请稍后重试~");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_list);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initDailog();
        init();
    }

    private LoadingDailog mLoadingDailog;
    private void initDailog() {
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("加载中...")
                .setCancelable(false)
                .setCancelOutside(false);
        mLoadingDailog = loadBuilder.create();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        initPullToRefresh();
        initSwipeListView();
    }

    @OnClick(R.id.iv_back)
    public void onBackClick(View v) {
        finish();
    }

    private void initPullToRefresh() {

        // 1.设置模式
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);

        // 2.1 通过调用getLoadingLayoutProxy方法，设置下拉刷新状况布局中显示的文字 ，第一个参数为true,代表下拉刷新
        ILoadingLayout headLables = mPullToRefreshScrollView.getLoadingLayoutProxy(true, false);
        headLables.setPullLabel("下拉刷新");
        headLables.setRefreshingLabel("正在刷新...");
        headLables.setReleaseLabel("放开刷新");

        // 2.2 设置上拉加载底部视图中显示的文字，第一个参数为false,代表上拉加载更多
        ILoadingLayout footerLables = mPullToRefreshScrollView.getLoadingLayoutProxy(false, true);
        footerLables.setPullLabel("上拉加载");
        footerLables.setRefreshingLabel("正在载入...");
        footerLables.setReleaseLabel("放开加载更多");

        // 3.设置监听事件
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                addtoTop();         // 请求网络数据
                refreshComplete();  // 数据加载完成后，关闭header,footer
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                addtoBottom();      //  请求网络数据
                refreshComplete();  // 数据加载完成后，关闭header,footer
            }
        });

    }

    // 下拉刷新的方法:
    public void addtoTop() {
        mPage=1;
        mIsPullUp = false;
        getCollectList();// 下拉刷新
    }

    // 上拉加载的方法:
    public void addtoBottom() {
        mPage++;
        mIsPullUp = true;
        getCollectList();     // 加载更多；
    }

    // 刷新完成时关闭
    public void refreshComplete() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshScrollView.onRefreshComplete();
            }
        }, 1000);
    }

    private void initSwipeListView() {

        mSwipeListView = findViewById(R.id.swipelistview);
        mSwipeListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem itemdel = new SwipeMenuItem(getApplication());
                itemdel.setWidth(PxUtils.dip2px(mContext, 60));
    //                itemdel.setBackground(getResources().getDrawable(R.drawable.icon_btn_del_vehicle));
                itemdel.setBackground(R.color.red);
                itemdel.setTitleSize(16);
                itemdel.setTitle("删除");
                itemdel.setTitleColor(Color.WHITE);
                menu.addMenuItem(itemdel);
            }
        };
        mSwipeListView.setMenuCreator(creator);
//        mSwipeListView.setDividerHeight(0);

        mSwipeListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, final int index) {
                switch (index) {
                    case 0:
                        // 删除
                        mLoadingDailog.show();
                        collectShopDel(mShowList.get(position).getId());
                        break;
                }
                return false;
            }
        });

        mSwipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("shop_id",mShowList.get(position).getShop_id());
                startActivity(intent);
            }
        });

    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

//        mAdapter = new ShopInfoListAdapter(mContext, mShowList);
//        mSwipeListView.setAdapter(mAdapter);

        getCollectList();     // 初始化数据
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
            mShowList.addAll(mShopInfoBeans);
            mAdapter = new ShopInfoListAdapter(mContext, mShowList);
            mSwipeListView.setAdapter(mAdapter);

//            mAdapter.notifyDataSetChanged();
        } else {

            mShowList.addAll(mShopInfoBeans);
//            mAdapter.notifyDataSetChanged();
            mAdapter = new ShopInfoListAdapter(mContext, mShowList);
            mSwipeListView.setAdapter(mAdapter);

            if (mShopInfoBeans.size() == 0) {
                ToastUtil.show(mContext, "没有更多结果");
            }
        }
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 收藏列表
    private void getCollectList() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_COLLECTLIST;
        LogUtils.i(TAG + "getCollectList url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getCollectList result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getCollectList msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mShopInfoBeans = new Gson().fromJson(data, new TypeToken<List<ShopInfoBean>>() {
                            }.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getCollectList mShopInfoBeans.size " + mShopInfoBeans.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getCollectList volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_COLLECTLIST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getCollectList token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("page", mPage + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getCollectList json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 取消收藏商户
    private void collectShopDel(final String id) {

        String url = HttpURL.BASE_URL + HttpURL.MEM_COLLECTDEL;
        LogUtils.i(TAG + "collectShopDel url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "collectShopDel result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "collectShopDel msg " + msg);
                        String status = jsonResult.getString("status");

                        LogUtils.i(TAG + "collectShopDel status " + status + " msg " + msg);

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(MEM_COLLECTDEL_RESULT_SUCCESS,msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(MEM_COLLECTDEL_RESULT_FAILED,msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "collectShopDel volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_COLLECTDEL + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "collectShopDel token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("id", id);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "collectShopDel json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
