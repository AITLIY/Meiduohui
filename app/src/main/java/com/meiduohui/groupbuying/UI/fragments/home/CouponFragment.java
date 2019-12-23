package com.meiduohui.groupbuying.UI.fragments.home;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.coupons.CouponDetailsActivity;
import com.meiduohui.groupbuying.UI.activitys.coupons.MessageDetailsActivity;
import com.meiduohui.groupbuying.adapter.CouponListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CouponBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtils;
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


    private String TAG = "CouponFragment: ";
    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;
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
    @BindView(R.id.ptr_coupon_list)
    PullToRefreshListView mPullToRefreshListView;


    private ArrayList<CouponBean> mShowList = new ArrayList<>();                 // 优惠券显示的列表
    private ArrayList<CouponBean> mCouponBeans = new ArrayList<>();              // 优惠券搜索结果列表
    private CouponListAdapter mAdapter;

    private boolean mIsPullUp = false;
    private int mPage = 1;
    private int state = 0;
    private String mQrCode = "";
    private int mPostion;
    private final int IS_USED = 0;
    private final int IS_UNUSED = 1;
    private final int IS_EXPIRED = 2;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILED = 102;
    private static final int GET_QRCODE_SUCCESS = 401;
    private static final int GET_QRCODE_FAILD = 402;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    if (!mIsPullUp) {

                        if (mCouponBeans.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "您还没有优惠券~");
                        }
                    }
                    updataListView();
                    break;

                case LOAD_DATA1_FAILED:

                    setViewForResult(false, "查询数据失败~");
                    break;

                case GET_QRCODE_SUCCESS:
                    mLoadingDailog.dismiss();
                    LoadQrCode(mPostion);
                    break;

                case GET_QRCODE_FAILD:
                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case NET_ERROR:
                    mLoadingDailog.dismiss();
                    setViewForResult(false, "网络异常,请稍后重试~");
                    break;
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coupon, container, false);
        unbinder = ButterKnife.bind(this, mView);

        init();

        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void init() {
        initData();
        initDailog();
        initView();
    }

    private LoadingDailog mLoadingDailog;
    private void initDailog() {
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(mContext)
                .setMessage("加载中...")
                .setCancelable(false)
                .setCancelOutside(false);
        mLoadingDailog = loadBuilder.create();
    }

    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        mAdapter = new CouponListAdapter(mContext, mShowList);
        mAdapter.setOnItemClickListener(new CouponListAdapter.OnItemClickListener() {
            @Override
            public void onUse(int position) {

                if (mShowList.get(position).getM_id().equals("0")) {
                    mPostion = position;
                    mLoadingDailog.show();
                    getQuanQrcode(position);
                    //                    generateQrCode(position);
                } else {
                    Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                    intent.putExtra("Order_id", mShowList.get(position).getM_id());
                    startActivity(intent);
                }

            }

            @Override
            public void onDetail(int position) {
                CouponBean couponBean = mShowList.get(position);
                LogUtils.i(TAG + "ItemClick position " + position);
                Intent intent = new Intent(mContext, CouponDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("CouponBean", couponBean);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        mPullToRefreshListView.setAdapter(mAdapter);

        getQuanList();     // 初始化数据
    }

    private void initView() {
        initPullListView();
    }

    @OnClick({R.id.unused_rl, R.id.used_rl, R.id.expired_rl})
    public void onTopBarClick(View v) {

        switch (v.getId()) {

            case R.id.unused_rl:

                state = IS_USED;
                addtoTop();         // 未使用
                break;

            case R.id.used_rl:

                state = IS_UNUSED;
                addtoTop();         // 已使用
                break;

            case R.id.expired_rl:

                state = IS_EXPIRED;
                addtoTop();         // 已过期
                break;
        }

        changeTabItemStyle(v);
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        unused_tv.setTextColor(view.getId() == R.id.unused_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));
        used_tv.setTextColor(view.getId() == R.id.used_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));
        expired_tv.setTextColor(view.getId() == R.id.expired_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));

        unused_v.setVisibility(view.getId() == R.id.unused_rl ? View.VISIBLE : View.GONE);
        used_v.setVisibility(view.getId() == R.id.used_rl ? View.VISIBLE : View.GONE);
        expired_v.setVisibility(view.getId() == R.id.expired_rl ? View.VISIBLE : View.GONE);
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
        getQuanList();     // 下拉刷新；
    }

    // 上拉加载的方法:
    public void addtoBottom() {
        mPage++;
        mIsPullUp = true;
        getQuanList();     // 加载更多；
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
            mView.findViewById(R.id.not_data).setVisibility(View.GONE);

        } else {
            mView.findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            ((TextView) mView.findViewById(R.id.not_data_tv)).setText(msg);
        }
    }

    // 更新列表数据
    private void updataListView() {

        if (!mIsPullUp) {

            mShowList.clear();
            mShowList.addAll(mCouponBeans);

            mAdapter.notifyDataSetChanged();
            //            mPullToRefreshListView.getRefreshableView().smoothScrollToPosition(0);//移动到首部
        } else {

            mShowList.addAll(mCouponBeans);
            mAdapter.notifyDataSetChanged();
            if (mCouponBeans.size() == 0) {
                ToastUtils.show(mContext, "没有更多结果");
            }
        }
    }

    private PopupWindow popupWindow;

    public void LoadQrCode(int position) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_quan_qr, null);

        TextView mName = view.findViewById(R.id.tv_shop_name);
        ImageView mImg = view.findViewById(R.id.iv_qr_code);
        ImageView mClose = view.findViewById(R.id.iv_close);

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
        wl.alpha = 0.5f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        getActivity().getWindow().setAttributes(wl);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                getActivity().getWindow().setAttributes(wl);
            }
        });
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);

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
    private void generateQrCode(int pos) {
        if (TextUtils.isEmpty(mShowList.get(pos).getQ_id())) {
            ToastUtils.show(mContext, "操作失败");
            return;
        }
        String date = CommonParameters.DOWNLOAD_URL + "_" + mShowList.get(pos).getQ_id() + "_" + "2";

//        mRvInvite.setVisibility(View.VISIBLE);
//        mTvShopName.setText(mShowList.get(pos).getShop_name());
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

    //--------------------------------------请求服务器数据--------------------------------------------

    // 优惠券列表
    private void getQuanList() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_QUANLIST;
        LogUtils.i(TAG + "getQuanList url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getQuanList result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getQuanList msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mCouponBeans = new Gson().fromJson(data, new TypeToken<List<CouponBean>>() {
                            }.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getQuanList mCouponBeans.size " + mCouponBeans.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getQuanList volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_QUANLIST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getQuanList token " + token);
                String md5_token = MD5Utils.md5(token);


                map.put("mem_id", mUserBean.getId());
                map.put("page", mPage + "");
                map.put("state", state + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getQuanList json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 生成通用券核销二维码
    private void getQuanQrcode(final int position) {

        String url = HttpURL.BASE_URL + HttpURL.ORDER_QUANQRCODE;
        LogUtils.i(TAG + "getQuanQrcode url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getQuanQrcode result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getQuanQrcode msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            mQrCode = jsonResult.getString("data");

                            mHandler.sendEmptyMessage(GET_QRCODE_SUCCESS);
                            LogUtils.i(TAG + "getQuanQrcode url " + mQrCode);

                        } else {
                            mHandler.sendEmptyMessage(GET_QRCODE_FAILD);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getQuanQrcode volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_QUANQRCODE + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getQuanQrcode token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("quan_id", mShowList.get(position).getQ_id());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getQuanQrcode json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
