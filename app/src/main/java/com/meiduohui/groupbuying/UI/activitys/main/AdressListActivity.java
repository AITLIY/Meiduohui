package com.meiduohui.groupbuying.UI.activitys.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
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
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.MyRecyclerView;
import com.meiduohui.groupbuying.adapter.AdressListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.AddressBean;
import com.meiduohui.groupbuying.bean.UserBean;
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

public class AdressListActivity extends AppCompatActivity {

    private String TAG = "AdressListActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    private int mPage = 1;
    private boolean mIsPullUp = false;

    @BindView(R.id.PullToRefreshScroll_View)
    PullToRefreshScrollView mPullToRefreshScrollView;
    @BindView(R.id.tv_current_couty)
    TextView mTvCurrentCouty;
    @BindView(R.id.rv_adress_list)
    MyRecyclerView mRvAdressList;

    private List<AddressBean> mAddressBeans = new ArrayList<>();       // 搜索结果列表
    private List<AddressBean> mShowList = new ArrayList<>();              // 显示的列表
    private AdressListAdapter mAdapter;

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

                    if (!mIsPullUp) {

                        if (mAddressBeans.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "没有区域信息~");
                        }
                    }
                    updataListView();

                    break;

                case LOAD_DATA1_FAILED:
                    setViewForResult(false, "查询数据失败~");
                    break;

                case NET_ERROR:
                    setViewForResult(false, "网络异常,请稍后重试~");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_list);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    private void init() {
        initData();
        initPullToRefresh();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        mTvCurrentCouty.setText(GlobalParameterApplication.mCounty);

        mAdapter = new AdressListAdapter(mContext, mShowList);
        mAdapter.setOnItemClickListener(new AdressListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                intent.putExtra("county", mShowList.get(position).getName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mRvAdressList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvAdressList.setAdapter(mAdapter);

        getAddress();
    }

    @OnClick({R.id.iv_back, R.id.ll_current_couty})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_current_couty:
                Intent intent = new Intent();
                intent.putExtra("county", GlobalParameterApplication.mCounty);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void initPullToRefresh() {

        // 1.设置模式
        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
//        mPullToRefreshScrollView.setMode(PullToRefreshBase.Mode.BOTH);

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
        mPage = 1;
        mIsPullUp = false;
        getAddress();// 下拉刷新
    }

    // 上拉加载的方法:
    public void addtoBottom() {
        mPage++;
        mIsPullUp = true;
        getAddress();     // 加载更多；
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
            mShowList.addAll(mAddressBeans);

            mAdapter.notifyDataSetChanged();

        } else {

            mShowList.addAll(mAddressBeans);
            mAdapter.notifyDataSetChanged();
            if (mAddressBeans.size() == 0) {
                ToastUtil.show(mContext, "没有更多结果");
            }
        }
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取地址列表
    private void getAddress() {

        String url = HttpURL.BASE_URL + HttpURL.SET_GETADDRESS;
        LogUtils.i(TAG + "getAddress url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getAddress result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getAddress msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mAddressBeans = new Gson().fromJson(data, new TypeToken<List<AddressBean>>() {
                            }.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getAddress mAddressBeans.size " + mAddressBeans.size());
                        } else {

                            mHandler.sendEmptyMessage(LOAD_DATA1_FAILED);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getAddress volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SET_GETADDRESS + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getAddress token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("page", mPage + "");
                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getAddress json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
