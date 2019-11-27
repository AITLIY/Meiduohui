package com.meiduohui.groupbuying.UI.activitys.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.coupons.PayOrderActivity;
import com.meiduohui.groupbuying.adapter.OrderListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.OrderBean;
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

public class VipOrderListActivity extends AppCompatActivity {

    private String TAG = "VipOrderListActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.all_order_tv)
    TextView mAllOrderTv;
    @BindView(R.id.all_order_v)
    View mAllOrderV;
    @BindView(R.id.un_pay_tv)
    TextView mUnPayTv;
    @BindView(R.id.un_pay_v)
    View mUnPayV;
    @BindView(R.id.un_use_tv)
    TextView mUnUseTv;
    @BindView(R.id.un_use_v)
    View mUnUseV;
    @BindView(R.id.used_tv)
    TextView mUsedTv;
    @BindView(R.id.used_v)
    View mUsedV;
    @BindView(R.id.ptr_coupon_list)
    PullToRefreshListView mPullToRefreshListView;

    private ArrayList<OrderBean> mShowList = new ArrayList<>();                 // 订单显示的列表
    private ArrayList<OrderBean> mOrderBeans = new ArrayList<>();               // 订单搜索结的果列表
    private OrderListAdapter mAdapter;

    private boolean mIsPullUp = false;
    private int mPage = 1;
    private int mState = 5;
    private String mQrCode = "";
    private int mPostion;

    private final int UN_PAY = 0;
    private final int USE_RL = 1;
    private final int IS_USED = 2;
    private final int IS_ALL = 5;
    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILEDD = 102;
    private static final int CANCEL_ORDER_SUCCESS = 201;
    private static final int CANCEL_ORDER_FAILED = 202;
    private static final int DEL_ORDER_SUCCESS = 301;
    private static final int DEL_ORDER_FAILED = 302;
    private static final int GET_QRCODE_SUCCESS = 401;
    private static final int GET_QRCODE_FAILED = 402;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    if (!mIsPullUp) {

                        if (mOrderBeans.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "暂无订单~");
                        }
                    }
                    updataListView();
                    break;

                case LOAD_DATA1_FAILEDD:

                    setViewForResult(false, "查询数据失败~");
                    break;

                case CANCEL_ORDER_SUCCESS:
                    mLoadingDailog.dismiss();
                    addtoTop();         // 取消后
                    break;

                case CANCEL_ORDER_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, (String) msg.obj);
                    break;

                case DEL_ORDER_SUCCESS:
                    mLoadingDailog.dismiss();
                    addtoTop();         // 删除后
                    ToastUtil.show(mContext, (String) msg.obj);
                    break;

                case DEL_ORDER_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, (String) msg.obj);
                    break;

                case GET_QRCODE_SUCCESS:
//                    generateQrCode1(mPostion);
                    mLoadingDailog.dismiss();
                    LoadQrCode(mPostion);
                    break;

                case GET_QRCODE_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, (String) msg.obj);
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
        setContentView(R.layout.activity_shop_order_list);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    private void init() {
        initDailog();
        initView();
        initData();
    }

    private LoadingDailog mLoadingDailog;
    private void initDailog() {
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("加载中...")
                .setCancelable(false)
                .setCancelOutside(false);
        mLoadingDailog = loadBuilder.create();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        mAdapter = new OrderListAdapter(mContext, mShowList);
        mAdapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
            @Override
            public void onCancel(final int position) {

                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("确定要取消订单吗")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mLoadingDailog.show();
                                cancelOrder(mShowList.get(position).getOrder_id());
                            }
                        }).show();
            }

            @Override
            public void onPay(int position) {
                GlobalParameterApplication.getInstance().setPayIntention(CommonParameters.UNPAY_ORDER);
                Intent intent = new Intent(VipOrderListActivity.this, PayOrderActivity.class);
                intent.putExtra("OrderID", mShowList.get(position).getOrder_id());
                intent.putExtra("table", CommonParameters.SYSTEM_ORDER);
                intent.putExtra("notify", CommonParameters.NOTIFY_CHANGE_ORDER);
                startActivity(intent);
            }

            @Override
            public void onDelete(final int position) {
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage("确定要删除订单吗")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mLoadingDailog.show();
                                delOrder(mShowList.get(position).getOrder_id());
                            }
                        }).show();
            }

            @Override
            public void onUse(int position) {
                mLoadingDailog.show();
                mPostion = position;
                getOrderQrcode(mPostion);
            }
        });
        mAdapter.setShop(false);
        mPullToRefreshListView.setAdapter(mAdapter);

        getOrderList();     // 初始化数据
    }

    private void initView() {
        initPullListView();
    }

    @OnClick(R.id.iv_back)
    public void onBackClick(View view) {
        finish();
    }

    private PopupWindow popupWindow;

    public void LoadQrCode(int position) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_quan_qr, null);

        TextView mTitle = view.findViewById(R.id.tv_title);
        TextView mName = view.findViewById(R.id.tv_shop_name);
        ImageView mImg = view.findViewById(R.id.iv_qr_code);
        ImageView mClose = view.findViewById(R.id.iv_close);

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.alpha = 0.5f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        getWindow().setAttributes(wl);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                WindowManager.LayoutParams wl = getWindow().getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                getWindow().setAttributes(wl);
            }
        });
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        mTitle.setText(mShowList.get(position).getM_title());
        mName.setText(mShowList.get(position).getShop_name());

        Glide.with(mContext)
                .load(mQrCode)
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mImg);
        // 关闭
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow.dismiss();
            }
        });

    }

    /**
     * 生成二维码
     */
    private void generateQrCode1(int pos) {
        if (TextUtils.isEmpty(mShowList.get(pos).getOrder_id())) {
            ToastUtil.show(mContext, "操作失败");
            return;
        }

        String date = CommonParameters.DOWNLOAD_URL + "_" + mShowList.get(pos).getOrder_id() + "_" + "3";

//        mRvInvite.setVisibility(View.VISIBLE);
//        mTvTitle.setText(mShowList.get(pos).getM_title());
//        mTvName.setText(mShowList.get(pos).getShop_name());
//
//        Bitmap bitmap = QrCodeGenerator.getQrCodeImage(date, mIvQrCode.getWidth(), mIvQrCode.getHeight());
//        if (bitmap == null) {
//            ToastUtil.show(mContext, "生成二维码出错");
//            mIvQrCode.setImageResource(R.drawable.icon_bg_default_img);
//
//        } else {
//            mIvQrCode.setImageBitmap(bitmap);
//        }
    }

    @OnClick({R.id.all_order_rl, R.id.un_pay_rl, R.id.un_use_rl, R.id.used_rl})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.all_order_rl:
                mState = IS_ALL;
                addtoTop();         // 全部
                break;

            case R.id.un_pay_rl:
                mState = UN_PAY;
                addtoTop();         // 待付款
                break;

            case R.id.un_use_rl:
                mState = USE_RL;
                addtoTop();         // 未使用
                break;

            case R.id.used_rl:
                mState = IS_USED;
                addtoTop();         // 已使用
                break;
        }
        changeTabItemStyle(view);
    }


    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        mAllOrderTv.setTextColor(view.getId() == R.id.all_order_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));
        mUnPayTv.setTextColor(view.getId() == R.id.un_pay_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));
        mUnUseTv.setTextColor(view.getId() == R.id.un_use_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));
        mUsedTv.setTextColor(view.getId() == R.id.used_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));

        mAllOrderV.setVisibility(view.getId() == R.id.all_order_rl ? View.VISIBLE : View.GONE);
        mUnPayV.setVisibility(view.getId() == R.id.un_pay_rl ? View.VISIBLE : View.GONE);
        mUnUseV.setVisibility(view.getId() == R.id.un_use_rl ? View.VISIBLE : View.GONE);
        mUsedV.setVisibility(view.getId() == R.id.used_rl ? View.VISIBLE : View.GONE);
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

    }

    // 下拉刷新的方法:
    public void addtoTop() {
        mPage = 1;
        mIsPullUp = false;
        getOrderList();     // 下拉刷新；
    }

    // 上拉加载的方法:
    public void addtoBottom() {
        mPage++;
        mIsPullUp = true;
        getOrderList();     // 加载更多；
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
            mShowList.addAll(mOrderBeans);
            mAdapter.notifyDataSetChanged();

        } else {

            mShowList.addAll(mOrderBeans);
            mAdapter.notifyDataSetChanged();
            if (mOrderBeans.size() == 0) {
                ToastUtil.show(mContext, "没有更多结果");
            }
        }
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 我的订单列表
    private void getOrderList() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_ORDERLIST;
        LogUtils.i(TAG + "getOrderList url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getOrderList result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getOrderList msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mOrderBeans = new Gson().fromJson(data, new TypeToken<List<OrderBean>>() {
                            }.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getOrderList .size " + mOrderBeans.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getOrderList volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_ORDERLIST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getOrderList token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("page", mPage + "");
                if (mState != IS_ALL)
                    map.put("state", mState + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getOrderList json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 取消订单
    private void cancelOrder(final String id) {

        String url = HttpURL.BASE_URL + HttpURL.ORDER_CANCELORDER;
        LogUtils.i(TAG + "cancelOrder url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "cancelOrder result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "cancelOrder msg " + msg);
                        String status = jsonResult.getString("status");

                        LogUtils.i(TAG + "cancelOrder status " + status + " msg " + msg);

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(CANCEL_ORDER_SUCCESS, msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(CANCEL_ORDER_FAILED, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "cancelOrder volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_CANCELORDER + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "cancelOrder token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("order_id", id);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "cancelOrder json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 删除订单
    private void delOrder(final String id) {

        String url = HttpURL.BASE_URL + HttpURL.ORDER_DELORDER;
        LogUtils.i(TAG + "delOrder url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "delOrder result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "delOrder msg " + msg);
                        String status = jsonResult.getString("status");

                        LogUtils.i(TAG + "delOrder status " + status + " msg " + msg);

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(DEL_ORDER_SUCCESS, msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(DEL_ORDER_FAILED, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "delOrder volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_DELORDER + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "delOrder token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("order_id", id);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "delOrder json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 生成订单核销二维码
    private void getOrderQrcode(final int position) {

        String url = HttpURL.BASE_URL + HttpURL.ORDER_ORDERQRCODE;
        LogUtils.i(TAG + "getOrderQrcode url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getOrderQrcode result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getOrderQrcode msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            mQrCode = jsonResult.getString("data");

                            mHandler.sendEmptyMessage(GET_QRCODE_SUCCESS);
                            LogUtils.i(TAG + "getOrderQrcode url " + mQrCode);

                        } else {
                            mHandler.sendEmptyMessage(GET_QRCODE_FAILED);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getOrderQrcode volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_ORDERQRCODE + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getOrderQrcode token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("order_id", mShowList.get(position).getOrder_id());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getOrderQrcode json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
