package com.meiduohui.groupbuying.UI.activitys.categorys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.coupons.MessageDetailsActivity;
import com.meiduohui.groupbuying.adapter.MsgSearchListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.IndexBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;
import com.meiduohui.groupbuying.utils.WxShareUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageListActivity extends AppCompatActivity {

    private String TAG = "HomeFragment: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.et_search_site)
    EditText mEtSearchSite;
    @BindView(R.id.ptr_coupon_list)
    PullToRefreshListView mPullToRefreshListView;

    private boolean mIsPullUp = false;
    private int mPage = 1;
    private int cat_id1;
    private int cat_id2;
    private String mKeywords;
    private int mPosition;

    private List<IndexBean.MessageInfoBean> mMessageInfos;                // 推荐列表集合
    private List<IndexBean.MessageInfoBean> mShowList;                    // 推荐列表集合更多

    private MsgSearchListAdapter mAdapter;

    private final int IS_RECOMMEND = 2;          // 推荐
    private final int LOAD_DATA1_SUCCESS = 101;  // 首页成功
    private final int LOAD_DATA1_FAILE = 102;
    private final int ORDER_ADDZAN_RESULT_SUCCESS = 301;
    private final int ORDER_ADDZAN_RESULT_FAILE = 302;
    private final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    if (!mIsPullUp) {

                        if (mMessageInfos.size() > 0) {
                            setViewForResult(true, "");

                        } else {
                            setViewForResult(false, "没有信息~");
                        }
                    }
                    updataListView();
                    break;

                case LOAD_DATA1_FAILE:

                    break;

                case ORDER_ADDZAN_RESULT_SUCCESS:

                    IndexBean.MessageInfoBean infoBean = mShowList.get(mPosition);

                    if (infoBean.getZan_info() == 0) {
                        infoBean.setZan_info(1);
                        infoBean.setZan((Integer.parseInt(infoBean.getZan()) + 1) + "");
                    } else {
                        infoBean.setZan_info(0);
                        infoBean.setZan((Integer.parseInt(infoBean.getZan()) - 1) + "");
                    }

                    mAdapter.notifyDataSetChanged();
                    ToastUtil.show(mContext,(String) msg.obj);
                    break;

                case ORDER_ADDZAN_RESULT_FAILE:
                    ToastUtil.show(mContext,(String) msg.obj);
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
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    private void init() {
        initCommentEt();
        initPullListView();
        initData();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        mShowList = new ArrayList<>();
        mAdapter = new MsgSearchListAdapter(mContext, mShowList);
        mAdapter.setOnItemClickListener(new MsgSearchListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("Order_id",mShowList.get(position).getOrder_id());
                startActivity(intent);
            }

            @Override
            public void onComment(int position) {
                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("Order_id",mShowList.get(position).getOrder_id());
                startActivity(intent);
            }

            @Override
            public void onZF(int position) {
                GlobalParameterApplication.shareIntention = CommonParameters.SHARE_MESSAGE;
                WxShareUtils.shareWeb(mContext, CommonParameters.SHARE_JUMP + CommonParameters.APP_INDICATE + mShowList.get(position).getOrder_id(),
                        mShowList.get(position).getTitle(), mShowList.get(position).getIntro(), null);
            }

            @Override
            public void onZan(int position) {
                LogUtils.i(TAG + "addZan onZan " + position);
                mPosition = position;
                addZan(mShowList.get(position).getOrder_id());
            }
        });
        mPullToRefreshListView.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            cat_id1 = intent.getIntExtra("cat_id1", 0);
            cat_id2 = intent.getIntExtra("cat_id2", 0);
            int search = intent.getIntExtra("search", 1);
            if (search == 1)
                getIndexData();      // 初始化
            LogUtils.i(TAG + "initData cat_id1 " + cat_id1
                    + " cat_id2 " + cat_id2
                    + " search " + search);
        }
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

    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void initCommentEt() {

        mEtSearchSite.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mEtSearchSite.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    String comment = mEtSearchSite.getText().toString().trim();

                    LogUtils.i(TAG + "init comment " + comment);

                    if (TextUtils.isEmpty(comment)) {
                        return false;
                    }
                    mKeywords = comment;
                    getIndexData();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }

                    return true;
                }
                return false;
            }
        });

    }


    //--------------------------------------请求服务器数据--------------------------------------------

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

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            return;
                        }
                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);
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
                    map.put("county", GlobalParameterApplication.mAddress);
                }

                map.put("cat_id1", cat_id1 + "");
                map.put("cat_id2", cat_id2 + "");
                if (!TextUtils.isEmpty(mKeywords))
                    map.put("keywords", mKeywords);
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

    // 点赞
    private void addZan(final String id) {

        if (mUserBean==null){
            ToastUtil.show(mContext,"您还未登录");
            return;
        }

        String url = HttpURL.BASE_URL + HttpURL.ORDER_ADDZAN;
        LogUtils.i(TAG + "addZan url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "addZan result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "addZan msg " + msg);
                        String status = jsonResult.getString("status");

                        LogUtils.i(TAG + "addZan status " + status + " msg " + msg);

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(ORDER_ADDZAN_RESULT_SUCCESS,msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(ORDER_ADDZAN_RESULT_FAILE,msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "addZan volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_ADDZAN + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "addZan token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getShop_id());
                map.put("m_id", id);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "addZan json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
