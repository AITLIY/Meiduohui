package com.meiduohui.groupbuying.UI.activitys.mine.wallet;

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
import com.meiduohui.groupbuying.adapter.RecorderListAdapter;
import com.meiduohui.groupbuying.adapter.WithdrawalListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.RecordBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.bean.WalletBean;
import com.meiduohui.groupbuying.bean.WithdrawalBean;
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

public class MyWalletActivity extends AppCompatActivity {

    private String TAG = "MyWalletActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.tv_money)
    TextView mTvMoney;
    @BindView(R.id.tv_invite_money)
    TextView mTvInviteMoney;
    @BindView(R.id.tv_shop_money)
    TextView mTvShopMoney;
    @BindView(R.id.record_tv)
    TextView mRecordTv;
    @BindView(R.id.record_v)
    View mRecordV;
    @BindView(R.id.withdrawalList_tv)
    TextView mWithdrawalListTv;
    @BindView(R.id.withdrawalList_v)
    View mWithdrawalListV;
    @BindView(R.id.rv_record_list)
    MyRecyclerView mRvRecordList;
    @BindView(R.id.rv_withdrawalList_list)
    MyRecyclerView mRvWithdrawalListList;
    @BindView(R.id.PullToRefreshScroll_View)
    PullToRefreshScrollView mPullToRefreshScrollView;

    private int mPage = 1;
    private int mPage2 = 1;
    private boolean mIsPullUp = false;
    private boolean mIsPullUp2 = false;
    private boolean mIsRecord = true;

    private WalletBean mWalletBean;
    private List<RecordBean.RecordListBean> mRecordListBeans = new ArrayList<>();       // 搜索结果列表
    private List<RecordBean.RecordListBean> mShowList = new ArrayList<>();              // 显示的列表
    private ArrayList<WithdrawalBean> mWithdrawalBeans = new ArrayList<>();         // 搜索结果列表
    private ArrayList<WithdrawalBean> mShowList2 = new ArrayList<>();               // 显示的列表
    private RecorderListAdapter mAdapter;
    private WithdrawalListAdapter mAdapter2;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILED = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILED = 202;
    private static final int LOAD_DATA3_SUCCESS = 301;
    private static final int LOAD_DATA3_FAILED = 302;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:
                    setContentData();
                    break;

                case LOAD_DATA1_FAILED:

                    break;

                case LOAD_DATA2_SUCCESS:

                    if (!mIsPullUp) {

                        if (mRecordListBeans.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "没有记录~");
                        }
                    }
                    updataListView();
                    break;

                case LOAD_DATA2_FAILED:
                    setViewForResult(false, "查询数据失败~");
                    break;

                case LOAD_DATA3_SUCCESS:
                    if (!mIsPullUp2) {

                        if (mWithdrawalBeans.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "没有记录~");
                        }
                    }
                    updataListView2();
                    break;

                case LOAD_DATA3_FAILED:
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
        setContentView(R.layout.activity_my_wallet);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        initPullToRefresh();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        mAdapter = new RecorderListAdapter(mContext, mShowList);
        mAdapter.setOnItemClickListener(new RecorderListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        mRvRecordList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvRecordList.setAdapter(mAdapter);

        mAdapter2 = new WithdrawalListAdapter(mContext, mShowList2);
        mAdapter2.setOnItemClickListener(new WithdrawalListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        mRvWithdrawalListList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvWithdrawalListList.setAdapter(mAdapter2);

        getWallet();
        getRecord();

    }

    @OnClick({R.id.iv_back, R.id.tv_account_list, R.id.tv_add_money, R.id.tv_withdrawal})
    public void onBackClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_account_list:
                startActivity(new Intent(this, AccountListActivity.class));
                break;

            case R.id.tv_add_money:
                startActivity(new Intent(this, AddMoneyActivity.class));
                break;

            case R.id.tv_withdrawal:
                startActivity(new Intent(this, WithdrawalActivity.class));
                break;
        }
    }

    @OnClick({R.id.record_rl, R.id.withdrawalList_rl})
    public void onMessageCatClick(View v) {

        switch (v.getId()) {

            case R.id.record_rl:

                setRecordListView(true);
                break;

            case R.id.withdrawalList_rl:

                setRecordListView(false);
                break;
        }
        changeTabItemStyle(v);
    }

    private void setRecordListView(boolean isShow) {

        if (isShow) {

            mIsRecord = true;
            mRvRecordList.setVisibility(View.VISIBLE);
            mRvWithdrawalListList.setVisibility(View.GONE);

            if (mShowList.size() > 0) {

                setViewForResult(true, null);
            } else {

                getRecord();    // 资金流水
            }

        } else {

            mIsRecord = false;
            mRvRecordList.setVisibility(View.GONE);
            mRvWithdrawalListList.setVisibility(View.VISIBLE);

            if (mShowList2.size() > 0) {

                setViewForResult(true, null);
            } else {

                getWithdrawalList();    // 提现记录
            }
        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        mRecordTv.setTextColor(view.getId() == R.id.record_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));
        mWithdrawalListTv.setTextColor(view.getId() == R.id.withdrawalList_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));

        mRecordV.setVisibility(view.getId() == R.id.record_rl ? View.VISIBLE : View.GONE);
        mWithdrawalListV.setVisibility(view.getId() == R.id.withdrawalList_rl ? View.VISIBLE : View.GONE);
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

        if (mIsRecord) {

            mPage = 1;
            mIsPullUp = false;

            getWallet();    // 下拉刷新
            getRecord();    // 下拉刷新

        } else {

            mPage2 = 1;
            mIsPullUp2 = false;

            getWithdrawalList();    // 下拉刷新
        }

    }

    // 上拉加载的方法:
    public void addtoBottom() {


        if (mIsRecord) {
            mPage++;
            mIsPullUp = true;
            getRecord();
        } else {
            mPage2++;
            mIsPullUp2 = true;
        }

        //        getCollectList();     // 加载更多；
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

    private void setContentData() {

        mTvMoney.setText(mWalletBean.getMoney());
        mTvInviteMoney.setText("佣金总额：¥" + mWalletBean.getInvite_money());
        mTvShopMoney.setText("商户总额：¥" + mWalletBean.getShop_money());
    }

    // 更新列表数据
    private void updataListView() {

        if (!mIsPullUp) {

            mShowList.clear();
            mShowList.addAll(mRecordListBeans);

            mAdapter.notifyDataSetChanged();
            LogUtils.i("MyWalletActivity " + "getRecord updataListView");
        } else {

            mShowList.addAll(mRecordListBeans);
            mAdapter.notifyDataSetChanged();
            if (mRecordListBeans.size() == 0) {
                ToastUtil.show(mContext, "没有更多结果");
            }
        }
    }

    // 更新列表数据2
    private void updataListView2() {

        if (!mIsPullUp2) {

            mShowList2.clear();
            mShowList2.addAll(mWithdrawalBeans);

            mAdapter2.notifyDataSetChanged();
            LogUtils.i("MyWalletActivity " + "getRecord updataListView");
        } else {

            mShowList2.addAll(mWithdrawalBeans);
            mAdapter2.notifyDataSetChanged();
            if (mWithdrawalBeans.size() == 0) {
                ToastUtil.show(mContext, "没有更多结果");
            }
        }
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

    //--------------------------------------请求服务器数据--------------------------------------------

    // 钱包
    private void getWallet() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_WALLET;
        LogUtils.i(TAG + "getWallet url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getWallet result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getWallet msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mWalletBean = new Gson().fromJson(data, WalletBean.class);

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getWallet getId " + mWalletBean.getId());
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
                LogUtils.e(TAG + "getWallet volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_WALLET + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getWallet token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getWallet json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 资金流水
    private void getRecord() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_RECORD;
        LogUtils.i(TAG + "getRecord url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getRecord result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getRecord msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            RecordBean mRecordBeans = new Gson().fromJson(data, RecordBean.class);
                            mRecordListBeans = mRecordBeans.getRecord_list();

                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);
                            LogUtils.i(TAG + "getRecord mRecordListBeans.size " + mRecordListBeans.size());
                        } else {

                            mHandler.sendEmptyMessage(LOAD_DATA2_FAILED);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getRecord volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_RECORD + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getRecord token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("page", mPage + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getRecord json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 提现记录
    private void getWithdrawalList() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_WITHDRAWALLIST;
        LogUtils.i(TAG + "getWithdrawalList url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getWithdrawalList result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getWithdrawalList msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mWithdrawalBeans = new Gson().fromJson(data, new TypeToken<List<WithdrawalBean>>() {
                            }.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA3_SUCCESS);
                            LogUtils.i(TAG + "getWithdrawalList mWithdrawalBeans.size " + mWithdrawalBeans.size());
                        } else {

                            mHandler.sendEmptyMessage(LOAD_DATA3_FAILED);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getWithdrawalList volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_WITHDRAWALLIST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getWithdrawalList token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("page", mPage2 + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getWithdrawalList json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
