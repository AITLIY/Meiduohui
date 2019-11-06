package com.meiduohui.groupbuying.UI.fragments.home;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.zxing.activity.CaptureActivity;
import com.google.zxing.util.Constant;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.HomepageActivity;
import com.meiduohui.groupbuying.UI.activitys.categorys.AllCategoryActivity;
import com.meiduohui.groupbuying.UI.activitys.categorys.MessageListActivity;
import com.meiduohui.groupbuying.UI.activitys.categorys.SecCategoyActivity;
import com.meiduohui.groupbuying.UI.activitys.coupons.MessageDetailsActivity;
import com.meiduohui.groupbuying.UI.views.MyGridView;
import com.meiduohui.groupbuying.UI.views.MyRecyclerView;
import com.meiduohui.groupbuying.UI.views.NiceImageView;
import com.meiduohui.groupbuying.adapter.FirstCatListHomeAdapter;
import com.meiduohui.groupbuying.adapter.MessageInfoListAdapter;
import com.meiduohui.groupbuying.adapter.ViewPagerAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.IndexBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.GPSUtils;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.PxUtils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
public class HomeFragment extends Fragment implements GPSUtils.OnLocationResultListener {

    private String TAG = "HomeFragment: ";
    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    private List<IndexBean.BannerInfoBean> mBannerInfoBeans;         // 轮播图的集合
    private List<IndexBean.CatInfoBean> mCatInfoBeans;               // 一级分类的集合
    private List<IndexBean.CatInfoBean> mNewCatInfoBeans;            // 一级分类的集合（添加全部分类）
    private List<IndexBean.MessageInfoBean> mTuiMessageInfos;             // 推荐列表集合
    private List<IndexBean.MessageInfoBean> mMoreTuiMessageInfos;         // 推荐列表集合更多
    private List<IndexBean.MessageInfoBean> mFJMessageInfos;              // 附近列表集合
    private List<IndexBean.MessageInfoBean> mMoreFJMessageInfos;          // 附近列表集合更多
    private int mPosition;

    private Unbinder unbinder;

    @BindView(R.id.PullToRefreshScroll_View)
    PullToRefreshScrollView mPullToRefreshScrollView;                  // 上下拉PullToRefreshScrollView
    @BindView(R.id.current_city_tv)
    TextView current_city_tv;                                          // 当前城市

    private Location mLocation;                                                 // 默认地址
    private String mAddress = "定位中...";                                       // 默认城市

    @BindView(R.id.banner_vp)
    ViewPager mViewPager;                                               // 轮播ViewPager
    @BindView(R.id.tv_pager_title)
    TextView mTvPagerTitle;                                             // 轮播标题

    private ViewPagerAdapter mViewPagerAdapter;                                 // 轮播ViewPagerAdapter
    private List<ImageView> mImageList;                                         // 轮播的图ImageView集合
    private List<View> mDots;                                                   // 轮播小点
    private int previousPosition = 0;                                           // 前一个被选中的position
    private static final int DELAYED_TIME = 2000;                               // 间隔时间
    // 在values文件夹下创建了ids.xml文件，并定义了5张轮播图对应的viewid，用于点击事件
    private int[] imgae_ids = new int[]{R.id.pager_image1, R.id.pager_image2, R.id.pager_image3, R.id.pager_image4, R.id.pager_image5};

    @BindView(R.id.class_category_gv)
    MyGridView mGridView;                                                 // 分类GridView

    private FirstCatListHomeAdapter mFirstCatListHomeAdapter;                       // 分类FirstCatListHomeAdapter

    @BindView(R.id.recommend_rl)                                        // 推荐
    RelativeLayout recommend_rl;
    @BindView(R.id.recommend_tv)
    TextView recommend_tv;
    @BindView(R.id.recommend_v)
    View recommend_v;

    @BindView(R.id.nearby_tv)                                           // 附近
    TextView nearby_tv;
    @BindView(R.id.nearby_v)
    View nearby_v;

    @BindView(R.id.rv_message_list)
    MyRecyclerView mMyRecyclerView;                                     // 信息列表mMyRecyclerView

    private MessageInfoListAdapter mMessageInfoListAdapter;             // 信息列表MessageInfoListAdapter

    private boolean mIsPullUp = false;         // 是否是更多
    private boolean mIsPullUp2 = false;         // 是否是更多
    private boolean mIsFJ = false;             // 是否是附近

    private int mPage = 1;                     // 当前页数
    private int mDistance = 1;                 // 当前距离

    private final int IS_RECOMMEND = 2;          // 推荐
    private final int UPDATA_ADDRESS = 66;      // 更新地址
    private final int LOAD_DATA1_SUCCESS = 101;  // 首页成功
    private final int LOAD_DATA1_FAILE = 102;
    private final int ORDER_ADDZAN_RESULT_SUCCESS = 201;
    private final int ORDER_ADDZAN_RESULT_FAILE = 202;
    private final int WRITEOFF_SUCCESS = 301;
    private final int WRITEOFF_FAILE = 302;

    private final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case UPDATA_ADDRESS:

                    current_city_tv.setText(mAddress);
                    break;

                case LOAD_DATA1_SUCCESS:

                    if (!mIsFJ) {

                        if (!mIsPullUp) {
                            initBannerView();
                            initCategory();
                        }
                        updataListView(mMoreTuiMessageInfos); // 首页刷新

                    } else {
                        updataListView(mMoreFJMessageInfos); // 首页刷新
                    }

                    break;

                case LOAD_DATA1_FAILE:

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

                    mMessageInfoListAdapter.notifyDataSetChanged();
                    ToastUtil.show(mContext,(String) msg.obj);
                    break;

                case ORDER_ADDZAN_RESULT_FAILE:
                    ToastUtil.show(mContext,(String) msg.obj);
                    break;

                case WRITEOFF_SUCCESS:
                    ToastUtil.show(mContext,(String) msg.obj);
                    break;

                case WRITEOFF_FAILE:
                    ToastUtil.show(mContext,(String) msg.obj);
                    break;

                case NET_ERROR:

                    ToastUtil.show(mContext, "网络异常,请稍后重试");
                    break;

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this,mView);

        init();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (runTask != null) {
            mHandler.removeCallbacks(runTask);
        }
        unbinder.unbind();
    }


    private void init() {
        initView();
        initData();
    }

    private void initView() {

        initPullToRefresh();
    }

    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        mLocation = new Location(""); // 设置默认地址
//        mLocation.setLatitude(34.914167);
//        mLocation.setLongitude(118.677470);

        updateData();      // 初始化
    }

    private void updateData() {
        getLocation();

        mPage = 1;
        mDistance = 1;
        mIsPullUp = false;
        mIsPullUp2 = false;
        mIsFJ = false;

        mMoreTuiMessageInfos = new ArrayList<>();
        mMoreTuiMessageInfos = new ArrayList<>();
        getIndexData();     // 刷新页面
    }

    private static final int ACCESS_FINE_LOCATION = 1000;

    //获取Location
    private void getLocation() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
        } else {
            GPSUtils.getInstance(getContext()).getLngAndLat(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LogUtils.i(TAG + " getLocation SUCCESS");
                    GPSUtils.getInstance(getContext()).getLngAndLat(this);

                }else {
                    LogUtils.i(TAG + " getLocation FAILED");
                    ToastUtil.show(getContext(),"您已取消授权，定位无法使用");

                    current_city_tv.setText("定位失败");
                }
                break;
            case Constant.REQ_PERM_CAMERA:
                // 摄像头权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    ToastUtil.show(getContext(),"您已取消授权，扫描无法使用");
                }
                break;
            case Constant.REQ_PERM_EXTERNAL_STORAGE:
                // 文件读写权限申请
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权
                    startQrCode();
                } else {
                    // 被禁止授权
                    ToastUtil.show(getContext(),"您已取消授权，扫描无法使用");
                }
                break;
        }
    }

    @Override
    public void onLocationResult(Location location) {
        LogUtils.i(TAG + " getLocation onLocationResult()");
        getAddress(location);
    }

    @Override
    public void OnLocationChange(Location location) {
        LogUtils.i(TAG + " getLocation OnLocationChange()");
        getAddress(location);
    }

    private void getAddress(Location location){

        GlobalParameterApplication.mLocation = location;
        mLocation = location;

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

        mAddress = address;
        mHandler.sendEmptyMessage(UPDATA_ADDRESS);

        LogUtils.i(TAG + " getLocation address2 " + address);
    }


    @OnClick({R.id.ll_select_region,R.id.tv_search_site,R.id.iv_scan_code,R.id.iv_open_red})
    public void onItemBarClick(View v) {

        switch (v.getId()) {
            case R.id.ll_select_region:
                getLocation();
                break;

            case R.id.tv_search_site:
                Intent intent = new Intent(mContext, MessageListActivity.class);
                intent.putExtra("search",0);
                startActivity(intent);
                break;

            case R.id.iv_scan_code:
                startQrCode();
                break;

            case R.id.iv_open_red:
                ((HomepageActivity) getActivity()).showRedPacket();
                break;
        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        recommend_tv.setTextColor(view.getId() == R.id.recommend_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));
        nearby_tv.setTextColor(view.getId() == R.id.nearby_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));

        recommend_v.setVisibility(view.getId() ==  R.id.recommend_rl ? View.VISIBLE:View.GONE);
        nearby_v.setVisibility(view.getId() == R.id.nearby_rl ? View.VISIBLE:View.GONE);
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
    public void addtoTop(){
        changeTabItemStyle(recommend_rl);

        updateData();      // 下拉刷新
    }

    // 上拉加载的方法:
    public void addtoBottom(){

        if (!mIsFJ){
            mPage++;
            mIsPullUp = true;
        } else {
            mDistance++;
            mIsPullUp2 = true;
        }

        getIndexData();     // 加载更多；
    }

    // 刷新完成时关闭
    public void refreshComplete(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshScrollView.onRefreshComplete();
            }
        },1000);
    }

    //-------------------------------------------轮播图----------------------------------------------

    //初始化轮播图
    private void initBannerView() {
        initViewPagerData();
        initAdapter();
        autoPlayView();
    }

    // 1.初始化ViewPager的内部的view
    public void initViewPagerData() {

        // 添加图片到图片列表里
        mImageList = new ArrayList<>();
        NiceImageView iv;
        for (int i = 0; i < mBannerInfoBeans.size(); i++) {
            iv = new NiceImageView(mContext);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setId(imgae_ids[i]);                         //给ImageView设置id
            iv.setOnClickListener(new pagerImageOnClick());//设置ImageView点击事件
//            LogUtils.i(TAG + " IndexBean " + mBannerInfoBeans.get(i).getImg());
            mImageList.add(iv);

            Glide.with(mContext)
                    .load(mBannerInfoBeans.get(i).getImg())
                    .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                    .into(iv);
        }

        // 添加轮播点
        if (mDots==null) {

            LinearLayout container = mView.findViewById(R.id.ll_point_container);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_poi_white);
            mDots = addDots(mImageList.size(), container, drawable);
        }
    }

    // 添加小点到list
    public List<View> addDots(int number, final LinearLayout container, Drawable backgrount) {
        List<View> listDots = new ArrayList<>();
        int dotId;
        for (int i = 0; i < number; i++) {

            dotId = addDot(container, backgrount);
            listDots.add(mView.findViewById(dotId));
        }
        return listDots;
    }

    // 设置轮播小点 并添加到container
    public int addDot(final LinearLayout container, Drawable backgount) {
        final View dot = new View(mContext);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dotParams.width = PxUtils.dip2px(mContext, 6);
        dotParams.height = PxUtils.dip2px(mContext, 6);
        dotParams.setMargins(PxUtils.dip2px(mContext, 4), 0, PxUtils.dip2px(mContext, 4), 0);

        dot.setLayoutParams(dotParams);
        dot.setBackground(backgount);
        dot.setId(View.generateViewId());
        container.addView(dot); // 添加小点到横向线性布局

        return dot.getId();
    }

    // 图片点击事件
    private class pagerImageOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.pager_image1:
                    LogUtils.i(TAG + " mBannerInfoBeans " + mBannerInfoBeans.get(0).getUrl());
                    goToTeacherWeb(mBannerInfoBeans.get(0).getUrl());
                    break;
                case R.id.pager_image2:
                    LogUtils.i(TAG + " mBannerInfoBeans " + mBannerInfoBeans.get(1).getUrl());
                    goToTeacherWeb(mBannerInfoBeans.get(1).getUrl());
                    break;
                case R.id.pager_image3:
                    LogUtils.i(TAG + " mBannerInfoBeans " + mBannerInfoBeans.get(2).getUrl());
                    goToTeacherWeb(mBannerInfoBeans.get(2).getUrl());
                    break;
                case R.id.pager_image4:
                    LogUtils.i(TAG + " mBannerInfoBeans " + mBannerInfoBeans.get(3).getUrl());
                    goToTeacherWeb(mBannerInfoBeans.get(3).getUrl());
                    break;
                case R.id.pager_image5:
                    LogUtils.i(TAG + " mBannerInfoBeans " + mBannerInfoBeans.get(4).getUrl());
                    goToTeacherWeb(mBannerInfoBeans.get(4).getUrl());
                    break;
            }
        }
    }

    // 跳转页面
    private void goToTeacherWeb(String url) {

        if (TextUtils.isEmpty(url)) {
            ToastUtil.show(mContext,"数据错误");
            return;
        }

        Intent intent = new Intent(mContext, MessageDetailsActivity.class);
        intent.putExtra("Order_id",url);
        startActivity(intent);

//         Intent intent = new Intent(mContext, MainActivity.class);
//        intent.putExtra("url",url);
//        startActivity(intent);
    }

    // 2.为ViewPager配置Adater
    public void initAdapter() {

        mViewPagerAdapter = new ViewPagerAdapter(mImageList);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //伪无限循环，滑到最后一张图片又从新进入第一张图片
                int newPosition = position % mImageList.size();
                //                LogUtils.i(TAG + " IndexBean newPosition " + newPosition);
                //图片下面设置显示文本
                //                mTvPagerTitle.setText(IndexBean.get(newPosition).getText);

                //设置轮播点
                View newView = mDots.get(newPosition);
                Drawable gray_poi = mContext.getResources().getDrawable(R.drawable.shape_poi_orange);
                newView.setBackground(gray_poi);

                View oldView = mDots.get(previousPosition);
                Drawable white_poi = mContext.getResources().getDrawable(R.drawable.shape_poi_white);
                oldView.setBackground(white_poi);

                // 把当前的索引赋值给前一个索引变量, 方便下一次再切换.
                previousPosition = newPosition;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setFirstLocation();
    }

    // 设置ViewPager的默认选中
    private void setFirstLocation() {

        // 把ViewPager设置为默认选中Integer.MAX_VALUE / 2，从十几亿次开始轮播图片，达到无限循环目的;
        int m = (Integer.MAX_VALUE / 2) % mImageList.size();
        int currentPosition = Integer.MAX_VALUE / 2 - m;

        mViewPager.setCurrentItem(0);
    }

    // 3.开启线程，自动播放
    private void autoPlayView() {

        if (runTask != null) {
            mHandler.removeCallbacks(runTask);
        }
        mHandler.postDelayed(runTask, DELAYED_TIME);
    }

    private Runnable runTask = new Runnable() {

        @Override
        public void run() {
//           LogUtils.i(TAG + " IndexBean runTask " + mViewPager.getCurrentItem());
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            mHandler.postDelayed(this, DELAYED_TIME);

        }
    };

    //-------------------------------------------店铺分类--------------------------------------------

    // 初始化分类
    @SuppressLint("ClickableViewAccessibility")
    private void initCategory() {

        LogUtils.i(TAG + " Category mCatInfoBeans.size " + mCatInfoBeans.size());
        mNewCatInfoBeans = new ArrayList<>();
        IndexBean.CatInfoBean lessonCategory = new IndexBean.CatInfoBean();

        for (int i=0; i<mCatInfoBeans.size(); i++) {
            mNewCatInfoBeans.add(mCatInfoBeans.get(i));
            if (i==8) {
                lessonCategory.setImg("");
                lessonCategory.setImg2(R.drawable.icon_tab_all_cat);
                lessonCategory.setId("0");
                lessonCategory.setName("全部分类");
                mNewCatInfoBeans.add(lessonCategory);
            }
        }

        mFirstCatListHomeAdapter = new FirstCatListHomeAdapter(getContext(),mNewCatInfoBeans);
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

                if (position==9){

                    Intent intent = new Intent(mContext, AllCategoryActivity.class);
                    startActivity(intent);

                } else {

                    Intent intent = new Intent(mContext, SecCategoyActivity.class);
                    intent.putExtra("cat_id1",mNewCatInfoBeans.get(position).getId());
                    startActivity(intent);
                }


            }
        });
    }

    //--------------------------------------推荐列表-------------------------------------------------

    @OnClick({R.id.recommend_rl,R.id.nearby_rl})
    public void onMessageCatClick(View v) {

        switch (v.getId()) {

            case R.id.recommend_rl:

                mIsFJ = false;
                if (mMoreTuiMessageInfos != null)
                    updataListView(mMoreTuiMessageInfos); //  推荐请求
                else {
                    mIsPullUp = false;
                    getIndexData();     // 推荐请求
                }

                break;

            case R.id.nearby_rl:

                mIsFJ = true;
                LogUtils.i(TAG + " mMoreFJMessageInfos" + (mMoreFJMessageInfos == null));
                if (mMoreFJMessageInfos != null)
                    updataListView(mMoreFJMessageInfos); //  附近请求
                else {
                    mIsPullUp2 = false;
                    getIndexData();     // 附近请求
                }

                break;
        }

        changeTabItemStyle(v);
    }

    // 初始化列表
    private void updataListView(final List<IndexBean.MessageInfoBean> messageInfos) {

        mMessageInfoListAdapter = new MessageInfoListAdapter(mContext,messageInfos);
        mMessageInfoListAdapter.setOnItemClickListener(new MessageInfoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("Order_id",messageInfos.get(position).getOrder_id());
                startActivity(intent);
            }

            @Override
            public void onComment(int position) {
                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("Order_id",messageInfos.get(position).getOrder_id());
                startActivity(intent);
            }

            @Override
            public void onZF(int position) {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (requestCode == Constant.REQ_QR_CODE && resultCode == getActivity().RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(Constant.INTENT_EXTRA_KEY_QR_SCAN);
            //将扫描出的信息显示出来
//            ToastUtil.show(mContext,scanResult);

            parseQrCode(scanResult);
        }
    }

    private void parseQrCode(String scanString) {

        if (!GlobalParameterApplication.getInstance().getUserIsShop()) {

            ToastUtil.show(mContext,"您不是商家无法使用");
            return;
        }

        String type[] = scanString.split("_");

        if (type.length<3) {
            ToastUtil.show(mContext,"数据错误");
        }

        LogUtils.i(TAG + "parseQrCode type " + type[2]);

        switch (type[2]) {
            case "1":

                break;

            case "2":
                getWriteOff(type[1]);
                break;

            case "3":
                getwriteOffQuan(type[1]);
                break;
        }

    }


    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取首页数据
    private void getIndexData() {

        final String url = HttpURL.BASE_URL + HttpURL.INDEX_INDEX;
        LogUtils.i(TAG + "getIndexData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
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


                map.put("lat", mLocation.getLatitude()+"");
                map.put("lon", mLocation.getLongitude()+"");
                map.put("cat_id1", "");
                map.put("cat_id2", "");
                if (mUserBean != null)
                    map.put("mem_id", mUserBean.getId());

                if (!mIsFJ) { // 首页请求

                    map.put("tui", IS_RECOMMEND + "");
                    if (mIsPullUp)  // 请求更多
                        map.put("page", mPage + "");

                } else {   // 附近请求

                    map.put("fj", mDistance + "");
                }


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
                            mHandler.obtainMessage(WRITEOFF_SUCCESS,msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(WRITEOFF_SUCCESS,msg).sendToTarget();
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

                map.put("shop_id", mUserBean.getId());
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
                            mHandler.obtainMessage(WRITEOFF_SUCCESS,msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(WRITEOFF_SUCCESS,msg).sendToTarget();
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

                map.put("shop_id", mUserBean.getId());
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

}
