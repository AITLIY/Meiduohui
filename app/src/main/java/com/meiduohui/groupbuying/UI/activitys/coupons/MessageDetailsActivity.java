package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.MyRecyclerView;
import com.meiduohui.groupbuying.adapter.CommentListAdapter;
import com.meiduohui.groupbuying.adapter.GeneralCouponListAdapter;
import com.meiduohui.groupbuying.adapter.MoreMsgListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CommentBean;
import com.meiduohui.groupbuying.bean.IndexBean;
import com.meiduohui.groupbuying.bean.ShopInfoBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageDetailsActivity extends AppCompatActivity {

    private String TAG = "MessageDetailsActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_m_price)
    TextView mTvMPrice;
    @BindView(R.id.tv_intro)
    TextView mTvIntroe;
    @BindView(R.id.tv_m_old_price)
    TextView mTvMOldPrice;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;
    @BindView(R.id.tv_shop_collect)
    TextView mTvShopCollect;
    @BindView(R.id.tv_shop_cancel_collect)
    TextView mTvShopCancelCollect;
    @BindView(R.id.tv_distance)
    TextView mTvDistance;
    @BindView(R.id.tv_shop_intro)
    TextView mTvShopIntro;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.tv_sjh)
    TextView mTvSjh;
    @BindView(R.id.tv_q_title)
    TextView mTvQTitle;
    @BindView(R.id.tv_have_quan)
    TextView mTvHaveQuan;
    @BindView(R.id.tv_have_quaned)
    TextView mTvHaveQuaned;
    @BindView(R.id.tv_use_time)
    TextView mTvUseTime;
    @BindView(R.id.tv_beizhu)
    TextView mTvBeizhu;
    @BindView(R.id.rv_more_coupon_list)
    MyRecyclerView mRvMoreCouponList;
    @BindView(R.id.more_msg_tv)
    TextView mMoreMsgTv;
    @BindView(R.id.more_msg_v)
    View mMoreMsgV;
    @BindView(R.id.more_msg_rl)
    RelativeLayout mMoreMsgRl;
    @BindView(R.id.comment_tv)
    TextView mCommentTv;
    @BindView(R.id.comment_v)
    View mCommentV;
    @BindView(R.id.comment_rl)
    RelativeLayout mCommentRl;
    @BindView(R.id.rv_more_message_list)
    MyRecyclerView mRvMoreMessageList;
    @BindView(R.id.rv_comment_list)
    MyRecyclerView mRvCommentList;

    @BindView(R.id.PullToRefreshScroll_View)
    PullToRefreshScrollView mPullToRefreshScrollView;                  // 上下拉PullToRefreshScrollView

    @BindView(R.id.tv_go_to_Buy)
    TextView mTvGoToBuy;
    @BindView(R.id.et_comment_content)
    EditText mEtCommentContent;
    @BindView(R.id.ll_comment)
    LinearLayout mLlComment;

    private int mPage = 1;
    private boolean mIsComment = false;
    private boolean mIsPullUp = false;

    private Location mLocation;
    private IndexBean.MessageInfoBean mMessageInfoBean;

    private GeneralCouponListAdapter mGeneralCouponListAdapter;
    private ShopInfoBean.MInfoBean mMInfoBeans;
    private MoreMsgListAdapter mMoreMsgListAdapter;
    private List<ShopInfoBean.MessageMoreBean> mMessageMoreBeans;
    private CommentListAdapter mCommentListAdapter;
    private List<CommentBean> mShowList;
    private List<CommentBean> mCommentBeans;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILE = 202;
    private static final int MEM_COLLECT_RESULT = 301;
    private static final int MEM_COLLECTDEL_RESULT = 302;
    private static final int ORDER_GETQUANL_RESULT = 303;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    setResultData();
                    if (!mIsPullUp) {

                        if (mMessageMoreBeans.size() > 0) {
                            setViewForResult(true, "");

                        } else {
                            setViewForResult(false, "没有发布其他消息~");
                        }
                    }
                    break;

                case LOAD_DATA1_FAILE:


                    break;

                case LOAD_DATA2_SUCCESS:

                    upDataLessonListView();
                    if (!mIsPullUp) {

                        if (mCommentBeans.size() > 0) {
                            setViewForResult(true, "");

                        } else {
                            setViewForResult(false, "还没有评论~");
                        }
                    }
                    break;

                case LOAD_DATA2_FAILE:


                    break;

                case MEM_COLLECT_RESULT:

                    String result = (String) msg.obj;
                    if (result.equals("收藏成功"))
                        setCollectStatusView(true);
                    else
                        ToastUtil.show(mContext,"收藏失败");
                    break;

                case MEM_COLLECTDEL_RESULT:

                    String result2 = (String) msg.obj;
                    if (result2.equals("删除成功"))
                        setCollectStatusView(false);
                    else
                        ToastUtil.show(mContext,"取消收藏失败");
                    break;

                case ORDER_GETQUANL_RESULT:

                    String result3 = (String) msg.obj;
                    if (result3.equals("成功"))
                        setHaveQuanView(true);
                    else
                        ToastUtil.show(mContext,"领取失败，请稍后重试");
                    break;

                case NET_ERROR:

                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        initData();
        initPullToRefresh();
        initCommentList();
        initCommentEt();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        updateData();       // 初始化数据
    }

    private void updateData() {

        mPage = 1;
        mIsPullUp = false;
        mIsComment = false;

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            mMessageInfoBean = (IndexBean.MessageInfoBean) bundle.getSerializable("MessageInfoBean");
            mLocation = (Location) bundle.getParcelable("Location");

            LogUtils.i(TAG + "initData Shop_name " + mMessageInfoBean.getShop_id());
            getShopInfoData();
        }
    }

    @OnClick({R.id.tv_shop_collect, R.id.tv_shop_cancel_collect, R.id.tv_have_quan, R.id.iv_go_address, R.id.iv_call_shops})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_shop_collect:
                collectShop();
                break;

            case R.id.tv_shop_cancel_collect:
                collectShopDel();
                break;

            case R.id.tv_have_quan:
                getQuan();
                break;

            case R.id.iv_go_address:
                break;
            case R.id.iv_call_shops:
                break;
        }
    }

    @OnClick({R.id.more_msg_rl, R.id.comment_rl})
    public void onOptionClick(View view) {
        switch (view.getId()) {
            case R.id.more_msg_rl:
                setCommentListView(false);

                break;

            case R.id.comment_rl:
                setCommentListView(true);

                getCommentData();     // 加载评论
                break;
        }
        changeTabItemStyle(view);
    }

    private void setCommentListView(boolean isShow) {

        if (!isShow) {
            mIsComment = false;
            mRvMoreMessageList.setVisibility(View.VISIBLE);
            mRvCommentList.setVisibility(View.GONE);

            mTvGoToBuy.setVisibility(View.VISIBLE);
            mLlComment.setVisibility(View.GONE);

            if (mMessageMoreBeans.size()<1)
                setViewForResult(true, "");

        } else {
            mIsComment = true;
            mRvMoreMessageList.setVisibility(View.GONE);
            mRvCommentList.setVisibility(View.VISIBLE);

            mTvGoToBuy.setVisibility(View.GONE);
            mLlComment.setVisibility(View.VISIBLE);

            if (mShowList.size()<1)
                setViewForResult(true, "");
        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        mMoreMsgTv.setTextColor(view.getId() == R.id.more_msg_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));
        mCommentTv.setTextColor(view.getId() == R.id.comment_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));

        mMoreMsgV.setVisibility(view.getId() == R.id.more_msg_rl ? View.VISIBLE : View.GONE);
        mCommentV.setVisibility(view.getId() == R.id.comment_rl ? View.VISIBLE : View.GONE);
    }

    private void setResultData() {
        setContentData();
        initCouponList();
        initMoreMsgList();
    }

    private void setContentData() {

        mTvTitle.setText(mMInfoBeans.getTitle());
        mTvIntroe.setText(mMInfoBeans.getIntro());
        mTvMPrice.setText(mMInfoBeans.getM_price());
        mTvMOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvMOldPrice.setText("原价¥ " + mMInfoBeans.getM_price());

        mTvShopName.setText(mMInfoBeans.getShop_name());
        mTvDistance.setText(mMInfoBeans.getJuli());
        mTvShopIntro.setText(mMInfoBeans.getShop_intro());
        mTvAddress.setText(mMInfoBeans.getAddress());
        mTvSjh.setText("电话：" + mMInfoBeans.getSjh());
        mTvQTitle.setText(mMInfoBeans.getQ_title());

        setCollectStatusView(mMInfoBeans.getShop_collect_state() == 1);
        setHaveQuanView(mMInfoBeans.getHave_quan() == 1);

        LogUtils.i(TAG + "getStart_time result " + new Date().getTime());

        LogUtils.i(TAG + "getStart_time result " + mMInfoBeans.getStart_time()
                + " " +Long.parseLong(mMInfoBeans.getStart_time())
                + " " + TimeUtils.LongToString(Long.parseLong(mMInfoBeans.getStart_time()), "yyyy.MM.dd"));

        mTvUseTime.setText("有效时间：" + TimeUtils.LongToString(Long.parseLong(mMInfoBeans.getStart_time()), "yyyy.MM.dd")
                + " - " + TimeUtils.LongToString(Long.parseLong(mMInfoBeans.getEnd_time()), "yyyy.MM.dd"));
        mTvBeizhu.setText(mMInfoBeans.getBeizhu());

    }

    private void setCollectStatusView(boolean isCollect) {

        if (!isCollect) {
            mTvShopCollect.setVisibility(View.VISIBLE);
            mTvShopCancelCollect.setVisibility(View.GONE);
        } else {
            mTvShopCollect.setVisibility(View.GONE);
            mTvShopCancelCollect.setVisibility(View.VISIBLE);
        }
    }

    private void setHaveQuanView(boolean isHave) {

        if (!isHave) {
            mTvHaveQuan.setVisibility(View.VISIBLE);
            mTvHaveQuaned.setVisibility(View.GONE);
        } else {
            mTvHaveQuan.setVisibility(View.GONE);
            mTvHaveQuaned.setVisibility(View.VISIBLE);
        }
    }

    private void initCouponList() {

        mGeneralCouponListAdapter = new GeneralCouponListAdapter(mContext, mMInfoBeans.getS_quan_info());
        mGeneralCouponListAdapter.setOnItemClickListener(new GeneralCouponListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        mRvMoreCouponList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMoreCouponList.setAdapter(mGeneralCouponListAdapter);
    }

    private void initMoreMsgList() {

        mMessageMoreBeans = new ArrayList<>();

        mMoreMsgListAdapter = new MoreMsgListAdapter(mContext, mMessageMoreBeans);
        mMoreMsgListAdapter.setOnItemClickListener(new MoreMsgListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        mRvMoreMessageList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMoreMessageList.setAdapter(mMoreMsgListAdapter);
    }

    private void initCommentEt() {

        mEtCommentContent.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mEtCommentContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    String comment = mEtCommentContent.getText().toString().trim();

                    LogUtils.i(TAG + "init comment " + comment);

                    if (TextUtils.isEmpty(comment)){
                        return false;
                    }

                    addCommentData(comment);

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

    private void initCommentList() {

        mShowList = new ArrayList<CommentBean>();

        mCommentListAdapter = new CommentListAdapter(mContext, mShowList);
        mCommentListAdapter.setOnItemClickListener(new CommentListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        mRvCommentList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvCommentList.setAdapter(mCommentListAdapter);
    }

    // 初始化列表
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

        setCommentListView(false);
        changeTabItemStyle(findViewById(R.id.more_msg_rl));

        updateData();       // 下拉刷新

    }

    // 上拉加载的方法:
    public void addtoBottom() {

        mIsPullUp = true;

        if (mIsComment) {
            mPage++;
            getCommentData();     // 加载更多；
        }
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
    private void upDataLessonListView() {

        if (!mIsPullUp) {

            mShowList.clear();
            mShowList.addAll(mCommentBeans);

            mCommentListAdapter.notifyDataSetChanged();
            //            mPullToRefreshListView.getRefreshableView().smoothScrollToPosition(0);//移动到首部
        } else {

            mShowList.addAll(mCommentBeans);
            mCommentListAdapter.notifyDataSetChanged();
            if (mCommentBeans.size() == 0) {
//                ToastUtil.show(mContext, "没有更多结果");
            }
        }
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取商铺发布信息
    private void getShopInfoData() {

        String url = HttpURL.BASE_URL + HttpURL.SHOP_SHOPINFO;
        LogUtils.i(TAG + "getShopInfoData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getShopInfoData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getShopInfoData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            ShopInfoBean shopInfoBean = new Gson().fromJson(data, ShopInfoBean.class);

                            mMInfoBeans = shopInfoBean.getM_info();
                            mMessageMoreBeans = shopInfoBean.getMessage_more();

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getShopInfoData mMessageMoreBeans.size " + mMessageMoreBeans.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getShopInfoData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SHOP_SHOPINFO + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getShopInfoData token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("m_id", mMessageInfoBean.getOrder_id());
                map.put("lon", mLocation.getLongitude() + "");
                map.put("lat", mLocation.getLatitude() + "");
                map.put("mem_id", "");
                map.put("page", "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getShopInfoData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 收藏商户
    private void collectShop() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_COLLECT;
        LogUtils.i(TAG + "collectShop url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "collectShop result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "collectShop msg " + msg);

                        String status = jsonResult.getString("status");
                        String data = "";
                        if ("0".equals(status)) {

                            data = jsonResult.getString("data");
                            LogUtils.i(TAG + "collectShop status " + status + " data " + data);

                        }
                        mHandler.obtainMessage(MEM_COLLECT_RESULT,data).sendToTarget();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "collectShop volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_COLLECT + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "collectShop token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", 1+"");
                map.put("shop_id", mMessageInfoBean.getShop_id());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "collectShop json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 取消收藏商户
    private void collectShopDel() {

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
                        String data = "";
                        if ("0".equals(status)) {

                            data = jsonResult.getString("data");
                            LogUtils.i(TAG + "collectShop status " + status + " data " + data);

                        }
                        mHandler.obtainMessage(MEM_COLLECTDEL_RESULT,data).sendToTarget();

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

                map.put("shop_id", mMInfoBeans.getShop_collect_id()+"");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "collectShopDel json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 获取优惠券
    private void getQuan() {

        String url = HttpURL.BASE_URL + HttpURL.ORDER_GETQUANL;
        LogUtils.i(TAG + "getQuan url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getQuan result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getQuan msg " + msg);

                        String status = jsonResult.getString("status");
                        String data = "";
                        if ("0".equals(status)) {

                            data = jsonResult.getString("data");
                            LogUtils.i(TAG + "collectShop status " + status + " data " + data);

                        }
                        mHandler.obtainMessage(ORDER_GETQUANL_RESULT,data).sendToTarget();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getQuan volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_GETQUANL + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getQuan token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", 1+"");
                map.put("shop_id", mMessageInfoBean.getShop_id());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getQuan json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 获取评论列表
    private void getCommentData() {

        String url = HttpURL.BASE_URL + HttpURL.COMMENT_COMMENTLIST;
        LogUtils.i(TAG + "getCommentData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getCommentData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getCommentData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");

                            mCommentBeans = new Gson().fromJson(data, new TypeToken<List<CommentBean>>() {
                            }.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);
                            LogUtils.i(TAG + "getCommentData mMessageMoreBeans.size " + mCommentBeans.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getCommentData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.COMMENT_COMMENTLIST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getCommentData token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("m_id", mMessageInfoBean.getOrder_id());
                map.put("page", mPage + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getCommentData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 添加评论
    private void addCommentData(final String comment) {

        String url = HttpURL.BASE_URL + HttpURL.COMMENT_ADDCOMMENT;
        LogUtils.i(TAG + "addCommentData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "addCommentData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "addCommentData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");

                            

                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);
                            LogUtils.i(TAG + "addCommentData mMessageMoreBeans.size " + mCommentBeans.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "addCommentData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.COMMENT_ADDCOMMENT + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "addCommentData token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", 1+"");
                map.put("content", comment);
                map.put("m_id", mMessageInfoBean.getOrder_id());
                map.put("page", mPage + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "addCommentData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
