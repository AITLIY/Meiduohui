package com.meiduohui.groupbuying.UI.activitys.mine;

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
import com.meiduohui.groupbuying.UI.activitys.coupons.MessageDetailsActivity;
import com.meiduohui.groupbuying.UI.views.MyRecyclerView;
import com.meiduohui.groupbuying.adapter.PublishListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.PublishBean;
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

public class PublishListActivity extends AppCompatActivity {

    private String TAG = "CollectListActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.rv_message_list)
    MyRecyclerView mRvMessageList;
    @BindView(R.id.PullToRefreshScroll_View)
    PullToRefreshScrollView mPullToRefreshScrollView;

    private int mPage = 1;
    private boolean mIsPullUp = false;

    private List<PublishBean> mPublishBeans = new ArrayList<>();
    private List<PublishBean> mShowList = new ArrayList<>();
    private PublishListAdapter mPublishListAdapter;

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

                        if (mPublishBeans.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "没有发布任何消息~");
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
        setContentView(R.layout.activity_publish_list);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    @OnClick(R.id.iv_back)
    public void onBackClick(View v) {
        finish();
    }

    private void init() {
        initPullToRefresh();
        initData();
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
        getMessageList();     // 下拉刷新
    }

    // 上拉加载的方法:
    public void addtoBottom() {
        mPage++;
        mIsPullUp = true;
        getMessageList();     // 加载更多；
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

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();
        initMsgList();
        getMessageList();     // 初始化数据
    }

    private void initMsgList() {

        mPublishListAdapter = new PublishListAdapter(mContext, mShowList);
        mPublishListAdapter.setOnItemClickListener(new PublishListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("Order_id", mShowList.get(position).getOrder_id());
                startActivity(intent);
                LogUtils.i(TAG + "initMsgList onItemClick position " + position);
            }
        });
        mRvMessageList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMessageList.setAdapter(mPublishListAdapter);
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
            mShowList.addAll(mPublishBeans);
            mPublishListAdapter.notifyDataSetChanged();

        } else {

            mShowList.addAll(mPublishBeans);
            mPublishListAdapter.notifyDataSetChanged();

            if (mPublishBeans.size() == 0) {
                ToastUtil.show(mContext, "没有更多结果");
            }
        }
    }


    //--------------------------------------请求服务器数据--------------------------------------------

    // 我的发布列表
    private void getMessageList() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_MESSAGELIST;
        LogUtils.i(TAG + "getMessageList url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getMessageList result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getMessageList msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mPublishBeans = new Gson().fromJson(data, new TypeToken<List<PublishBean>>() {}.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getMessageList mPublishBeans.size " + mPublishBeans.size());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getMessageList volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_MESSAGELIST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getMessageList token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("shop_id", mUserBean.getShop_id());
                map.put("page", mPage + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getMessageList json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
