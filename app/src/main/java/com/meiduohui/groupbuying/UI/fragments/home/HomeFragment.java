package com.meiduohui.groupbuying.UI.fragments.home;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.tu.loadingdialog.LoadingDailog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.Constant;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.categorys.AllCategoryActivity;
import com.meiduohui.groupbuying.UI.activitys.categorys.MessageListActivity;
import com.meiduohui.groupbuying.UI.activitys.categorys.SecCategoyActivity;
import com.meiduohui.groupbuying.UI.activitys.coupons.MessageDetailsActivity;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
import com.meiduohui.groupbuying.UI.activitys.login.RegisterActivity;
import com.meiduohui.groupbuying.UI.activitys.main.AdressListActivity;
import com.meiduohui.groupbuying.UI.activitys.main.HomepageActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.wallet.MyWalletActivity;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.UI.views.CustomDialog;
import com.meiduohui.groupbuying.UI.views.GlideImageLoader;
import com.meiduohui.groupbuying.UI.views.MyGridView;
import com.meiduohui.groupbuying.UI.views.MyRecyclerView;
import com.meiduohui.groupbuying.adapter.FirstCatListHomeAdapter;
import com.meiduohui.groupbuying.adapter.MessageInfoListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.IndexBean;
import com.meiduohui.groupbuying.bean.RedPacketBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.GPSUtils;
import com.meiduohui.groupbuying.utils.ImageUtils;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.PxUtils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtils;
import com.meiduohui.groupbuying.utils.UnicodeUtils;
import com.meiduohui.groupbuying.utils.WxShareUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements GPSUtils.OnLocationResultListener, AMapLocationListener {

    private String TAG = "HomeFragment: ";
    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private List<IndexBean.BannerInfoBean> mBannerInfoBeans = new ArrayList<>();         // 轮播图的集合
    private List<IndexBean.CatInfoBean> mCatInfoBeans = new ArrayList<>();               // 一级分类的集合
    private List<IndexBean.CatInfoBean> mNewCatInfoBeans = new ArrayList<>();            // 一级分类的集合（添加全部分类）
    private List<IndexBean.AdvInfoBean> mAdvInfoBeans = new ArrayList<>();               // 公告的集合
    private List<IndexBean.MessageInfoBean> mTuiMessageInfos = new ArrayList<>();             // 推荐列表集合
    private List<IndexBean.MessageInfoBean> mMoreTuiMessageInfos = new ArrayList<>();         // 推荐列表集合更多
    private List<IndexBean.MessageInfoBean> mFJMessageInfos = new ArrayList<>();              // 附近列表集合
    private List<IndexBean.MessageInfoBean> mMoreFJMessageInfos = new ArrayList<>();          // 附近列表集合更多
    private int mPosition;
    private boolean mIsShareApp;

    private Unbinder unbinder;

    @BindView(R.id.PullToRefreshScroll_View)
    PullToRefreshScrollView mPullToRefreshScrollView;                  // 上下拉PullToRefreshScrollView
    @BindView(R.id.current_city_tv)
    TextView current_city_tv;                                          // 当前城市

    private Location mLocation;                    // 默认地址
    private String mCounty = "";                                               // 默认城市

    @BindView(R.id.banner)                                              // 轮播图
    Banner mBanner;

    @BindView(R.id.class_category_gv)
    MyGridView mGridView;                                                // 分类GridView

    private FirstCatListHomeAdapter mFirstCatListHomeAdapter;                    // 分类FirstCatListHomeAdapter

    @BindView(R.id.tv_adv)
    TextView mTvAdv;                                                     // 公告

    @BindView(R.id.iv_open_red)                                          // 红包
    ImageView mIvOpenRed;

    private RedPacketBean mRedPacketBean;
    private String mMoney;

    @BindView(R.id.ll_list_type)                                         // 推荐
    LinearLayout ll_list_type;

    @BindView(R.id.recommend_rl)                                         // 推荐
    RelativeLayout recommend_rl;
    @BindView(R.id.recommend_tv)
    TextView recommend_tv;
    @BindView(R.id.recommend_v)
    View recommend_v;

    @BindView(R.id.nearby_tv)                                           // 附近
    TextView nearby_tv;
    @BindView(R.id.nearby_v)
    View nearby_v;

    @BindView(R.id.rv_tui_message_list)
    MyRecyclerView mMyRecyclerView;                                     // 信息列表mMyRecyclerView
    @BindView(R.id.rv_fj_message_list)
    MyRecyclerView mMyRecyclerView2;

    private MessageInfoListAdapter mMessageInfoListAdapter;                       // 信息列表MessageInfoListAdapter
    private MessageInfoListAdapter mMessageInfoListAdapter2;

    private boolean mIsPullUp = false;         // 是否是更多
    private boolean mIsPullUp2 = false;
    private boolean mIsFJ = false;             // 是否是附近

    private int mPage = 1;                     // 当前页数
    private int mPage2 = 1;

    private final int IS_RECOMMEND = 2;          // 推荐
    private final int UPDATA_ADDRESS = 166;       // 更新地址
    private final int GET_LOCATION = 67;         // 获取地址
    private final int STOP_LOCATION = 68;
    private final int START_ANIMATION = 70;
    private final int LOAD_DATA1_SUCCESS = 101;
    private final int LOAD_DATA1_FAILED = 102;
    private final int ORDER_ADDZF_RESULT_SUCCESS = 211;
    private final int ORDER_ADDZF_RESULT_FAILED = 222;
    private final int ORDER_ADDZAN_RESULT_SUCCESS = 201;
    private final int ORDER_ADDZAN_RESULT_FAILED = 202;
    private final int WRITEOFF_SUCCESS = 301;
    private final int WRITEOFF_FAILED = 302;
    private static final int SHOP_REDINFO_SUCCESS = 601;
    private static final int SHOP_REDINFO_FAILED = 602;
    private static final int SHOP_GETRED_SUCCESS = 701;
    private static final int SHOP_GETRED_FAILED = 702;

    private final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case GET_LOCATION:
                    getLocation();
                    break;

                case STOP_LOCATION:
                    mLoadingDailog.dismiss();
                    break;

                case START_ANIMATION:

                    if (mTvAdv!=null) {
                        TranslateAnimation animation = new TranslateAnimation(0, 0, PxUtils.sp2px(mContext,0 ), PxUtils.sp2px(mContext,-30));
                        animation.setDuration(500);
                        mTvAdv.startAnimation(animation);
                    }
                    break;

                case UPDATA_ADDRESS:
                    if (mTvAdv != null) {
                        current_city_tv.setText(mCounty);
                    }
                    break;

                case LOAD_DATA1_SUCCESS:

                    if (!mIsFJ) {

                        if (!mIsPullUp) {

                            initBanner(mBannerInfoBeans);
                            initCategory();

                            if (advTask!=null){
                                mHandler.removeCallbacks(advTask);
                            }
                            mHandler.post(advTask);
                        }

                        if (mMoreTuiMessageInfos.size() > 0) {
                            setViewForResult(true, null);
                        } else {
                            setViewForResult(false, "当前区域没有推荐信息~");
                        }
                        updataListView(mMoreTuiMessageInfos); // 首页刷新
                    } else {

                        if (mMoreFJMessageInfos.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "附近没有商家发布信息~");
                        }
                        updataListView2(mMoreFJMessageInfos); // 首页刷新
                    }

                    break;

                case LOAD_DATA1_FAILED:

                    break;

                case ORDER_ADDZF_RESULT_SUCCESS:

                    if (!mIsFJ) {
                        IndexBean.MessageInfoBean tuiMsg = mMoreTuiMessageInfos.get(mPosition);
                        tuiMsg.setZf((Integer.parseInt(tuiMsg.getZf()) + 1) + "");
                        mMessageInfoListAdapter.notifyDataSetChanged();
                    } else {
                        IndexBean.MessageInfoBean fjMsg = mMoreFJMessageInfos.get(mPosition);
                        fjMsg.setZf((Integer.parseInt(fjMsg.getZf()) + 1) + "");
                        mMessageInfoListAdapter2.notifyDataSetChanged();
                    }
                    break;

                case ORDER_ADDZF_RESULT_FAILED:
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case ORDER_ADDZAN_RESULT_SUCCESS:

                    IndexBean.MessageInfoBean infoBean;

                    if (!mIsFJ)
                        infoBean = mMoreTuiMessageInfos.get(mPosition);
                    else
                        infoBean = mMoreFJMessageInfos.get(mPosition);

                    if (infoBean.getZan_info() == 0) {
                        infoBean.setZan_info(1);
                        infoBean.setZan((Integer.parseInt(infoBean.getZan()) + 1) + "");
                    } else {
                        infoBean.setZan_info(0);
                        infoBean.setZan((Integer.parseInt(infoBean.getZan()) - 1) + "");
                    }
                    if (!mIsFJ)
                        mMessageInfoListAdapter.notifyDataSetChanged();
                    else
                        mMessageInfoListAdapter2.notifyDataSetChanged();
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case ORDER_ADDZAN_RESULT_FAILED:
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case WRITEOFF_SUCCESS:
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case WRITEOFF_FAILED:
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case SHOP_REDINFO_SUCCESS:
                    LogUtils.i(TAG + "redInfo getSy_number " + mRedPacketBean.getSy_number());
                    if (!mRedPacketBean.getSy_number().equals("0")) {
                        mIvOpenRed.setVisibility(View.VISIBLE);
                    }
                    break;

                case SHOP_REDINFO_FAILED:
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case SHOP_GETRED_SUCCESS:
                    showGetRed();
                    break;

                case SHOP_GETRED_FAILED:
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case NET_ERROR:
                    ToastUtils.show(mContext, "网络异常,请稍后重试");
                    break;

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, mView);

        init();
        return mView;
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i(TAG + " onResume onResume()");

        if (GlobalParameterApplication.isShareSussess) {
            GlobalParameterApplication.isShareSussess = false;

            if (!mIsShareApp) {
                if (!mIsFJ) {
                    addZf(mMoreTuiMessageInfos.get(mPosition).getOrder_id());
                } else {
                    addZf(mMoreFJMessageInfos.get(mPosition).getOrder_id());
                }

            } else {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getRed();
                    }
                }, 1000);
            }
        }

        getLocation();      // 初始化定位
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (advTask!=null){
            mHandler.removeCallbacks(advTask);
        }

        unbinder.unbind();
    }


    private void init() {
        initDailog();
        initData();
        initView();
//        getLocation();      // 初始化定位
    }

    private LoadingDailog mLoadingDailog;
    private void initDailog() {
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(getContext())
                .setMessage("加载中...")
                .setCancelable(false)
                .setCancelOutside(false);
        mLoadingDailog = loadBuilder.create();
    }

    private void initView() {
        initPullToRefresh();
    }

    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        initLocation();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                redInfo();
            }
        }, 500);
    }

    private void initLocation() {

        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(8000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
//        mLocationClient.startLocation();
    }

    private static final int ACCESS_FINE_LOCATION = 1000;
    //获取Location
    private void getLocation() {

        if (ContextCompat.checkSelfPermission(GlobalParameterApplication.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
        } else {

//            int result = GPSUtils.getInstance(getContext()).getLngAndLat(this);
//
//            if (result==0) {
//                mLoadingDailog.dismiss();
//            }

            //启动定位
            mLocationClient.startLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LogUtils.i(TAG + " getLocation SUCCESS");
                    mLoadingDailog.show();
//                    int result = GPSUtils.getInstance(getContext()).getLngAndLat(this);
//                    if (result==0) {
//                        mLoadingDailog.dismiss();
//                    }

                    //启动定位
                    mLocationClient.startLocation();
                } else {
                    LogUtils.i(TAG + " getLocation FAILED");
                    ToastUtils.show(getContext(), "您已取消授权，定位无法使用");

                    current_city_tv.setText("定位失败");
                }
                break;
            case Constant.REQ_PERM_CAMERA:
            case Constant.REQ_PERM_EXTERNAL_STORAGE:
                // 文件读写权限申请
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    ToastUtils.show(getContext(), "您已取消授权，扫描无法使用");
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mLoadingDailog.dismiss();
        aMapLocation.getLatitude();//获取纬度
        aMapLocation.getLongitude();//获取经度

        aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
        aMapLocation.getCountry();//国家信息
        aMapLocation.getProvince();//省信息
        aMapLocation.getCity();//城市信息
        aMapLocation.getDistrict();//城区信息
        aMapLocation.getStreet();//街道信息
        aMapLocation.getStreetNum();//街道门牌号信息

        LogUtils.i(TAG + " getLocation onLocationChanged() "
                + " getLatitude " +  aMapLocation.getLatitude()
                + " getLongitude " +  aMapLocation.getLongitude()
                + " getAddress " +  aMapLocation.getAddress()
                + " getProvince " +  aMapLocation.getProvince()
                + " getCity " +  aMapLocation.getCity()
                + " getDistrict " +  aMapLocation.getDistrict()
                + " getStreet " +  aMapLocation.getStreet()
                + " getStreetNum " +  aMapLocation.getStreetNum());

        Location location = new Location("");
        location.setLatitude(aMapLocation.getLatitude());
        location.setLongitude(aMapLocation.getLongitude());

        mLocation = location;
        GlobalParameterApplication.mLocation = location;

        if (!TextUtils.isEmpty(aMapLocation.getDistrict())) {
            mCounty = aMapLocation.getDistrict();
            GlobalParameterApplication.mCounty = mCounty;
        } else {
            mCounty = "定位失败";
            GlobalParameterApplication.mCounty = mCounty;
            mHandler.sendEmptyMessageDelayed(GET_LOCATION, 5000);
        }
        mHandler.sendEmptyMessage(UPDATA_ADDRESS);
        addtoTop();      // OnLocationChange初始化
    }

    @Override
    public void onLocationResult(Location location) {
        LogUtils.i(TAG + " getLocation onLocationResult()");
        mLoadingDailog.dismiss();
        getAddress(location);
        addtoTop();      // onLocationResult初始化
    }

    @Override
    public void OnLocationChange(Location location) {
        LogUtils.i(TAG + " getLocation OnLocationChange()");
        mLoadingDailog.dismiss();
        getAddress(location);
        addtoTop();      // OnLocationChange初始化
    }

    @Override
    public void onLocationFailed() {
        mLoadingDailog.dismiss();
        current_city_tv.setText("定位失败");
    }

    private void getAddress(Location location) {

        mLocation = location;
        GlobalParameterApplication.mLocation = mLocation;

        String address = "";
        LogUtils.i(TAG + " getLocation address1 " + "纬度：" + location.getLatitude() + " 经度：" + location.getLongitude());

        List<Address> addList = null;
        Geocoder ge = new Geocoder(getContext());
        try {
            addList = ge.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {

            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address ad = addList.get(i);
                //                address = ad.getAdminArea() + ad.getLocality() + ad.getSubLocality();
                address = ad.getSubLocality();
            }
        }

        mCounty = address;

        mHandler.sendEmptyMessage(UPDATA_ADDRESS);

        LogUtils.i(TAG + " getLocation address2 " + address);
    }


    @OnClick({R.id.ll_select_region, R.id.tv_search_site, R.id.iv_scan_code, R.id.iv_open_red})
    public void onItemBarClick(View v) {

        switch (v.getId()) {
            case R.id.ll_select_region:
//                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
//                } else {
//
//                    mLoadingDailog.show();
//                    mHandler.sendEmptyMessageDelayed(GET_LOCATION, 500);
//                    mHandler.sendEmptyMessageDelayed(STOP_LOCATION, 5000);
//                }

                Intent intent1 = new Intent(mContext, AdressListActivity.class);
                startActivityForResult(intent1, SELECT_COUTY);

                break;

            case R.id.tv_search_site:
                Intent intent = new Intent(mContext, MessageListActivity.class);
                intent.putExtra("search", 0);
                startActivity(intent);
                break;

            case R.id.iv_scan_code:
                startQrCode();
                break;

            case R.id.iv_open_red:
                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(mContext, LoginActivity.class));
                } else {
//                    mIvOpenRed.setVisibility(View.GONE);
                    showRedInfo();
                }
                break;
        }
    }

    //-------------------------------------------上下拉----------------------------------------------

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

        if (!mIsFJ) {
            mPage = 1;
            mIsPullUp = false;
            mMoreTuiMessageInfos.clear();
        } else {
            mPage2 = 1;
            mIsPullUp2 = false;
            mMoreFJMessageInfos.clear();
        }

        getIndexData();     // 下拉刷新
    }

    // 上拉加载的方法:
    public void addtoBottom() {

        if (!mIsFJ) {
            mPage++;
            mIsPullUp = true;
        } else {
            mPage2++;
            mIsPullUp2 = true;
        }

        getIndexData();      // 加载更多；
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

    //-------------------------------------------轮播图----------------------------------------------

    private void initBanner(final List<IndexBean.BannerInfoBean> arrs) {
        final List<String> list = new ArrayList<>();

        for (IndexBean.BannerInfoBean obj : arrs) {
            list.add(obj.getImg());
            LogUtils.i(TAG + "initBanner url " + obj.getUrl());
        }

        mBanner.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), PxUtils.dip2px(mContext,10));
            }
        });
        mBanner.setClipToOutline(true);

        mBanner.setImages(list)
//                .setBannerAnimation(Transformer.DepthPage) //设置banner动画效果
                .setImageLoader(new GlideImageLoader())
                .setDelayTime(2000)
                .setIndicatorGravity(BannerConfig.CENTER)
                .start();
        //banner点击事件 position为当前显示的第几张图,从1开始,不是0
        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                goToTeacherWeb(mBannerInfoBeans.get(position).getUrl());
            }
        });
    }

    // 跳转页面
    private void goToTeacherWeb(String url) {

        if (TextUtils.isEmpty(url)) {
            ToastUtils.show(mContext, "数据错误");
            return;
        }

        Intent intent = new Intent(mContext, MessageDetailsActivity.class);
        intent.putExtra("Order_id", url);
        startActivity(intent);

        //         Intent intent = new Intent(mContext, WebActivity.class);
        //        intent.putExtra("url",url);
        //        startActivity(intent);
    }

    //-------------------------------------------店铺分类--------------------------------------------

    // 初始化分类
    @SuppressLint("ClickableViewAccessibility")
    private void initCategory() {

        LogUtils.i(TAG + " Category mCatInfoBeans.size " + mCatInfoBeans.size());
        mNewCatInfoBeans = new ArrayList<>();
        IndexBean.CatInfoBean lessonCategory = new IndexBean.CatInfoBean();

        for (int i = 0; i < mCatInfoBeans.size(); i++) {
            mNewCatInfoBeans.add(mCatInfoBeans.get(i));
            if (i == 8) {
                lessonCategory.setImg("");
                lessonCategory.setImg2(R.drawable.icon_tab_all_cat);
                lessonCategory.setId("0");
                lessonCategory.setName("全部分类");
                mNewCatInfoBeans.add(lessonCategory);
            }
        }

        mFirstCatListHomeAdapter = new FirstCatListHomeAdapter(getContext(), mNewCatInfoBeans);
        mGridView.setAdapter(mFirstCatListHomeAdapter); // 为mGridView设置Adapter
        mGridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return MotionEvent.ACTION_MOVE == event.getAction();// 设置mGridView不能滑动
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 添加列表项被单击的监听器
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 单击的图片
                LogUtils.i(TAG + " Category onItemClick " + position);

                if (position == 9) {

                    Intent intent = new Intent(mContext, AllCategoryActivity.class);
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(mContext, SecCategoyActivity.class);
                    intent.putExtra("cat_id1", mNewCatInfoBeans.get(position).getId());
                    startActivity(intent);
                }


            }
        });
    }

    //-------------------------------------------最新公告--------------------------------------------

    private int number = 0;

    private Runnable advTask = new Runnable() {

        @Override
        public void run() {

            mTvAdv.setText(mAdvInfoBeans.get(number % mAdvInfoBeans.size()).getContent());
            mTvAdv.clearAnimation();

            mHandler.sendEmptyMessageDelayed(START_ANIMATION,1500);
            mHandler.postDelayed(this, 2000);
            number++;
        }
    };

    //--------------------------------------推荐列表-------------------------------------------------

    @OnClick({R.id.recommend_rl, R.id.nearby_rl})
    public void onMessageCatClick(View v) {

        switch (v.getId()) {

            case R.id.recommend_rl:

                setTuiListView(true);
                break;

            case R.id.nearby_rl:

                setTuiListView(false);
                break;
        }

        changeTabItemStyle(v);
    }

    private void setTuiListView(boolean isShow) {

        if (isShow) {

            mIsFJ = false;
            mMyRecyclerView.setVisibility(View.VISIBLE);
            mMyRecyclerView2.setVisibility(View.GONE);
            if (mMoreTuiMessageInfos.size() > 0) {
                setViewForResult(true, null);
            } else {
                addtoTop();    // 推荐请求
            }

        } else {

            mIsFJ = true;
            mMyRecyclerView.setVisibility(View.GONE);
            mMyRecyclerView2.setVisibility(View.VISIBLE);

            LogUtils.i(TAG + " mMoreFJMessageInfos" + (mMoreFJMessageInfos == null));
            if (mMoreFJMessageInfos.size() > 0) {
                setViewForResult(true, null);
            } else {
                addtoTop();   // 附近请求
            }

        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        recommend_tv.setTextColor(view.getId() == R.id.recommend_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));
        nearby_tv.setTextColor(view.getId() == R.id.nearby_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));

        recommend_v.setVisibility(view.getId() == R.id.recommend_rl ? View.VISIBLE : View.GONE);
        nearby_v.setVisibility(view.getId() == R.id.nearby_rl ? View.VISIBLE : View.GONE);
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

    // 初始化列表
    private void updataListView(final List<IndexBean.MessageInfoBean> messageInfos) {

        mMessageInfoListAdapter = new MessageInfoListAdapter(mContext, messageInfos);
        mMessageInfoListAdapter.setOnItemClickListener(new MessageInfoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("Order_id", messageInfos.get(position).getOrder_id());
                startActivity(intent);
            }

            @Override
            public void onComment(int position) {
                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("Order_id", messageInfos.get(position).getOrder_id());
                intent.putExtra("onComment", true);
                startActivity(intent);
            }

            @Override
            public void onZF(final int position) {

                mPosition = position;
                mIsShareApp = false;
                showShare(messageInfos);
            }

            @Override
            public void onZan(int position) {
                LogUtils.i(TAG + "addZan onZan " + position);
                mPosition = position;
                addZan(messageInfos.get(position).getOrder_id());
            }
        });
        mMyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mMyRecyclerView.setAdapter(mMessageInfoListAdapter);

    }

    // 初始化列表2
    private void updataListView2(final List<IndexBean.MessageInfoBean> messageInfos) {

        mMessageInfoListAdapter2 = new MessageInfoListAdapter(mContext, messageInfos);
        mMessageInfoListAdapter2.setOnItemClickListener(new MessageInfoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("Order_id", messageInfos.get(position).getOrder_id());
                startActivity(intent);
            }

            @Override
            public void onComment(int position) {
                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("Order_id", messageInfos.get(position).getOrder_id());
                intent.putExtra("onComment", true);
                startActivity(intent);
            }

            @Override
            public void onZF(final int position) {

                mPosition = position;
                mIsShareApp = false;
                showShare(messageInfos);
            }

            @Override
            public void onZan(int position) {
                LogUtils.i(TAG + "addZan onZan " + position);
                mPosition = position;
                addZan(messageInfos.get(position).getOrder_id());
            }
        });
        mMyRecyclerView2.setLayoutManager(new LinearLayoutManager(mContext));
        mMyRecyclerView2.setAdapter(mMessageInfoListAdapter2);

    }

    private PopupWindow popupWindow;

    public void showShare(final List<IndexBean.MessageInfoBean> messageInfos) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_we_share, null);

        LinearLayout mSession = view.findViewById(R.id.ll_we_Session);
        LinearLayout mTimeline = view.findViewById(R.id.ll_we_Timeline);
        TextView mCancel = view.findViewById(R.id.tv_cancel);

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
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
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        // 好友
        mSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsShareApp) {
                    share(messageInfos,0);
                } else {
                    share(0);
                }
                popupWindow.dismiss();
            }
        });

        // 朋友圈
        mTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsShareApp) {
                    share(messageInfos,1);
                } else {
                    share(1);
                }
                popupWindow.dismiss();
            }
        });

        //取消
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow.dismiss();
            }
        });

    }

    private void share(final List<IndexBean.MessageInfoBean> messageInfos, final int type) {

        LogUtils.i(TAG + " share SHARE_MESSAGE type " + type);
        GlobalParameterApplication.shareIntention = CommonParameters.SHARE_MESSAGE;

        String url = "";
        if (!TextUtils.isEmpty(messageInfos.get(mPosition).getVideo())){
            url = messageInfos.get(mPosition).getVideo() + CommonParameters.VIDEO_END;
        } else {
            url = messageInfos.get(mPosition).getImg().get(0);
        }
        LogUtils.i(TAG + "onZF url " + url);

        Glide.with(mContext).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
            /**
             * 成功的回调
             */
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                // 下面这句代码是一个过度dialog，因为是获取网络图片，需要等待时间

                Bitmap bitmap1 = ImageUtils.getIntance().comp(bitmap,32);

                WxShareUtils.shareWeb(mContext, CommonParameters.SHARE_JUMP + CommonParameters.APP_INDICATE
                                + "_" + messageInfos.get(mPosition).getOrder_id() + "_" + CommonParameters.TYPE_SHOP,
                        messageInfos.get(mPosition).getTitle(), messageInfos.get(mPosition).getIntro(), bitmap1, type);
            }

            /**
             * 失败的回调
             */
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                // 下面这句代码是一个过度dialog，因为是获取网络图片，需要等待时间

                LogUtils.i(TAG + "onZF onLoadFailed " + mPosition);
                WxShareUtils.shareWeb(mContext, CommonParameters.SHARE_JUMP + CommonParameters.APP_INDICATE
                                + "_" + messageInfos.get(mPosition).getOrder_id() + "_" + CommonParameters.TYPE_SHOP,
                        messageInfos.get(mPosition).getTitle(), messageInfos.get(mPosition).getIntro(), null, type);
            }
        });

    }

    private void share(final int type) {

        LogUtils.i(TAG + " share SHARE_SHOPS type " + type);
        GlobalParameterApplication.shareIntention = CommonParameters.SHARE_SHOPS;

//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_tab_mei);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_tab_red_packet);

        WxShareUtils.shareWeb(mContext,  CommonParameters.SHARE_JUMP + CommonParameters.APP_INDICATE
                        + "_" + "home"  + "_" + CommonParameters.TYPE_HOME,
                "美多惠APP给您发红包了！", "山东美多惠信息技术有限公司移动应用——美多惠APP，是一款涵盖各种热门产品优惠信息的移动应用，深受广大用户喜爱。", bmp, type);
    }

    // 开始扫码
    private void startQrCode() {
        // 申请相机权限
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Constant.REQ_PERM_CAMERA);
            return;
        }
        // 申请文件读写权限（部分朋友遇到相册选图需要读写权限的情况，这里一并写一下）
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.REQ_PERM_EXTERNAL_STORAGE);
            return;
        }
        // 二维码扫码
        Intent intent = new Intent(mContext, CaptureActivity.class);
        startActivityForResult(intent, Constant.REQ_QR_CODE);
    }

    private static final int SELECT_COUTY = 3000;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case SELECT_COUTY:

                if (resultCode == getActivity().RESULT_OK) {
                    mCounty = data.getStringExtra("county");
                    GlobalParameterApplication.mCounty = mCounty;
                    current_city_tv.setText(mCounty);
                    addtoTop();     // 选择区域
                    Log.d(TAG, "onActivityResult: county " + mCounty);
                }
                break;

            //扫描结果回调
            case Constant.REQ_QR_CODE:

                if (resultCode == getActivity().RESULT_OK) {

                    Bundle bundle = data.getExtras();
                    String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
                    //将扫描出的信息显示出来
                    //            ToastUtil.show(mContext,scanResult);

                    parseQrCode(scanResult);

                }
                break;
        }
    }

    private void parseQrCode(String scanString) {

        String contents[] = scanString.split("_");

        if (contents.length < 3) {
            ToastUtils.show(mContext, "数据错误");
            return;
        }
        LogUtils.i(TAG + "getwrite scanString " + scanString);
        LogUtils.i(TAG + "getwrite contents " + contents[2]);

        switch (contents[2]) {

            case CommonParameters.TYPE_SHARE:
                Intent intent = new Intent(mContext, RegisterActivity.class);
                intent.putExtra("mobile", contents[1]);
                startActivity(intent);
                break;

            case CommonParameters.TYPE_GENERAL:
               getwriteOffQuan(contents[1]);
                break;

            case CommonParameters.TYPE_ORDER:
                getWriteOff(contents[1]);
                break;
        }

    }

    private PopupWindow popupWindow3;

    public void showRedInfo() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_read_redpacket, null);

        CircleImageView mImg = view.findViewById(R.id.civ_shop_img);
        TextView mName = view.findViewById(R.id.tv_shop_name);
        TextView mPrice= view.findViewById(R.id.tv_price);
        ImageView mShare = view.findViewById(R.id.iv_share);
        ImageView mClose = view.findViewById(R.id.iv_close);

        popupWindow3 = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow3.setFocusable(false);
        popupWindow3.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow3.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        popupWindow3.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
        wl.alpha = 0.5f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        getActivity().getWindow().setAttributes(wl);
        popupWindow3.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                getActivity().getWindow().setAttributes(wl);
            }
        });
        popupWindow3.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        Glide.with(mContext)
                .load(mRedPacketBean.getShop_img())
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mImg);
        mName.setText(mRedPacketBean.getShop_name() + "送您红包");
        mPrice.setText(mRedPacketBean.getMax());
        // 分享
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIsShareApp = true;
                showShare(null);
                popupWindow3.dismiss();
            }
        });

        // 关闭
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow3.dismiss();
            }
        });

    }

    private PopupWindow popupWindow4;

    public void showGetRed() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_get_redpacket, null);

        TextView mGetMoney = view.findViewById(R.id.tv_get_money);
        ImageView mLook = view.findViewById(R.id.iv_look);
        ImageView mClose = view.findViewById(R.id.iv_close);

        popupWindow4 = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow4.setFocusable(false);
        popupWindow4.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
        wl.alpha = 0.5f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        getActivity().getWindow().setAttributes(wl);
        popupWindow4.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                getActivity().getWindow().setAttributes(wl);
            }
        });
        popupWindow4.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        mGetMoney.setText(mMoney);

        // 查看
        mLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(mContext, MyWalletActivity.class));
                popupWindow4.dismiss();
            }
        });

        // 关闭
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow4.dismiss();
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

                            if (!mIsFJ) {

                                mTuiMessageInfos = mIndexBean.getMessage_info();

                                if (!mIsPullUp) {

                                    mBannerInfoBeans = mIndexBean.getBanner_info();
                                    mCatInfoBeans = mIndexBean.getCat_info();
                                    mAdvInfoBeans = mIndexBean.getAdv_info();

                                    mMoreTuiMessageInfos = mTuiMessageInfos;

                                } else {

                                    mMoreTuiMessageInfos.addAll(mTuiMessageInfos);
                                }

                                LogUtils.i(TAG + "getIndexData mBannerInfoBeans.size " + mBannerInfoBeans.size()
                                        + " mCatInfoBeans.size " + mCatInfoBeans.size()
                                        + " mMoreTuiMessageInfos.size " + mMoreTuiMessageInfos.size()
                                );

                            } else {

                                mFJMessageInfos = mIndexBean.getMessage_info();

                                if (!mIsPullUp2) {
                                    mMoreFJMessageInfos = mFJMessageInfos;
                                } else {
                                    mMoreFJMessageInfos.addAll(mFJMessageInfos);
                                }

                                LogUtils.i(TAG + "getIndexData mMoreFJMessageInfos.size " + mMoreFJMessageInfos.size());
                            }

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            return;
                        } else if (CommonParameters.LOGIN_STATUS_CODE.equals(status)) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    new CustomDialog(mContext).builder()
                                            .setTitle("提示")
                                            .setMessage("登录已失效，请重新登录")
                                            .setPositiveButton("确定", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    GlobalParameterApplication.getInstance().setLoginStatus(false);
                                                    ((HomepageActivity) getActivity()).refreshDate();
                                                }
                                            })
                                            .setCancelable(false).show();
                                }
                            });

                        }
                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILED);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILED);
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

                if (mLocation != null) {
                    map.put("lat", mLocation.getLatitude() + "");
                    map.put("lon", mLocation.getLongitude() + "");
                    map.put("county", mCounty);
                }

                //                map.put("cat_id1", "");
                //                map.put("cat_id2", "");
                if (mUserBean != null)
                    map.put("mem_id", mUserBean.getId());

                if (!mIsFJ) { // 首页请求

                    map.put("tui", IS_RECOMMEND + "");
                    if (mIsPullUp)  // 请求更多
                        map.put("page", mPage + "");

                } else {   // 附近请求

                    map.put("fj", "10");
                    if (mIsPullUp2)  // 请求更多
                        map.put("page", mPage2 + "");
                }

                if (mUserBean != null)
                    map.put(CommonParameters.TOKEN, mUserBean.getToken());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getIndexData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 转发
    private void addZf(final String id) {

        String url = HttpURL.BASE_URL + HttpURL.ORDER_ADDZF;
        LogUtils.i(TAG + "addZf url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "addZf result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "addZf msg " + msg);
                        String status = jsonResult.getString("status");

                        LogUtils.i(TAG + "addZf status " + status + " msg " + msg);

                        if ("0".equals(status)) {
                            mHandler.sendEmptyMessage(ORDER_ADDZF_RESULT_SUCCESS);
                        } else {
                            mHandler.obtainMessage(ORDER_ADDZF_RESULT_FAILED, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "addZf volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_ADDZF + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "addZf token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getShop_id());
                map.put("m_id", id);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "addZf json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


    // 点赞
    private void addZan(final String id) {

        if (mUserBean == null) {
            ToastUtils.show(mContext, "您还未登录");
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
                            mHandler.obtainMessage(ORDER_ADDZAN_RESULT_SUCCESS, msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(ORDER_ADDZAN_RESULT_FAILED, msg).sendToTarget();
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

                map.put("mem_id", mUserBean.getId());
                map.put("m_id", id);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "addZan json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 通用券核销
    private void getwriteOffQuan(final String id) {

        String url = HttpURL.BASE_URL + HttpURL.ORDER_WRITEOFFQUAN;
        LogUtils.i(TAG + "getwriteOffQuan url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getwriteOffQuan result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getwriteOffQuan msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(WRITEOFF_SUCCESS, msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(WRITEOFF_FAILED, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getwriteOffQuan volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_WRITEOFFQUAN + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getwriteOffQuan token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("shop_id", mUserBean.getShop_id());
                map.put("quan_id", id);
                map.put("qrcode", "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getwriteOffQuan json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 订单核销
    private void getWriteOff(final String id) {

        String url = HttpURL.BASE_URL + HttpURL.ORDER_WRITEOFF;
        LogUtils.i(TAG + "getWriteOff url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getWriteOff result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getWriteOff msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(WRITEOFF_SUCCESS, msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(WRITEOFF_FAILED, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getWriteOff volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_WRITEOFF + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getWriteOff token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("shop_id", mUserBean.getShop_id());
                map.put("order_id", id);
                map.put("qrcode", "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getWriteOff json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 获取红包
    private void redInfo() {

        String url = HttpURL.BASE_URL + HttpURL.SHOP_REDINFO;
        LogUtils.i(TAG + "redInfo url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "redInfo result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "redInfo msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mRedPacketBean = new Gson().fromJson(data, RedPacketBean.class);

                            if (mRedPacketBean!=null) {
                                mHandler.sendEmptyMessage(SHOP_REDINFO_SUCCESS);
                                LogUtils.i(TAG + "redInfo getShop_name " + mRedPacketBean.getShop_name());
                            }

                        } else {
                            mHandler.obtainMessage(SHOP_REDINFO_FAILED, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "redInfo volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SHOP_REDINFO + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "redInfo token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("shop_id", "0");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "redInfo json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 抢红包
    public void getRed() {

        String url = HttpURL.BASE_URL + HttpURL.SHOP_GETRED;
        LogUtils.i(TAG + "redInfo url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getRed result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getRed msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            JSONObject jdate = new JSONObject(data);
                            mMoney = jdate.getString("red_money");

                            mHandler.sendEmptyMessage(SHOP_GETRED_SUCCESS);
                            LogUtils.i(TAG + "getRed mMoney " + mMoney);

                        } else {
                            mHandler.obtainMessage(SHOP_GETRED_FAILED, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getRed volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SHOP_GETRED + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getRed token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("red_id", mRedPacketBean.getId());
                map.put("mem_id", mUserBean.getId());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getRed json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


}
