package com.meiduohui.groupbuying.UI.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.MainActivity;
import com.meiduohui.groupbuying.UI.views.MyRecyclerView;
import com.meiduohui.groupbuying.adapter.FirstCatInfoBeanAdapter;
import com.meiduohui.groupbuying.adapter.LevelVipAdapter;
import com.meiduohui.groupbuying.adapter.ViewPagerAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.IndexBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.PxUtils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;

    private ArrayList<IndexBean.DataBean.BannerInfoBean> mBannerInfoBeans;     // 轮播图的集合
    private ArrayList<IndexBean.DataBean.CatInfoBean> mCatInfoBeans;            // 一级分类的集合
    private ArrayList<IndexBean.DataBean.CatInfoBean> mNewCatInfoBeans;            // 一级分类的集合

    private PtrFrameLayout ptrFrameLayout; // 下拉刷新
    private StoreHouseHeader storeHouseHeader;
    private MaterialHeader materialHeader;
    private PtrClassicDefaultHeader ptrClassicDefaultHeader;

    private ScrollView scrollView;
    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    private List<ImageView> mImageList; // 轮播的图ImageView集合
    private TextView mTvPagerTitle;     // 轮播标题
    private List<View> mDots;           // 轮播小点
    private int previousPosition = 0;   // 前一个被选中的position
    private static final int DELAYED_TIME = 2000;//间隔时间
    // 在values文件夹下创建了ids.xml文件，并定义了4张轮播图对应的viewid，用于点击事件
    private int[] imgae_ids = new int[]{R.id.pager_image1, R.id.pager_image2, R.id.pager_image3, R.id.pager_image4, R.id.pager_image5};

    private GridView mGridView;
    private PullToRefreshScrollView PullToRefreshScroll_View;
    private BaseAdapter mGridViewAdapter;

    private LinearLayout ll_select_region,ll_search_site;
    private ImageView iv_scan_code;

    private static final int LOAD_DATA1_SUCCESS = 101;  //  请求首页
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILE = 202;
    private static final int LOAD_DATA3_SUCCESS = 301;
    private static final int LOAD_DATA3_FAILE = 302;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    initViewPagerData();
                    initAdapter();
                    autoPlayView();

                    initAdapter2();
                    initData2();
                    break;

                case LOAD_DATA1_FAILE:

                    break;

                case LOAD_DATA2_SUCCESS:


                    break;

                case LOAD_DATA2_FAILE:

                    break;

                case LOAD_DATA3_SUCCESS:

//                    initRecommend();
                    ptrFrameLayout.refreshComplete();
                    break;

                case LOAD_DATA3_FAILE:

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
        mContext = getContext();

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

    }


    private void init() {

        initView();
        initData();
    }

    private void initView() {

        ll_select_region = mView.findViewById(R.id.ll_select_region);
        ll_search_site = mView.findViewById(R.id.ll_search_site);
        iv_scan_code = mView.findViewById(R.id.iv_scan_code);

        mViewPager = mView.findViewById(R.id.banner_vp);
        mTvPagerTitle = mView.findViewById(R.id.tv_pager_title);
        mGridView = mView.findViewById(R.id.class_category_gv);
        PullToRefreshScroll_View = mView.findViewById(R.id.PullToRefreshScroll_View);
        PullToRefreshScroll_View.setMode(PullToRefreshBase.Mode.BOTH);

        ll_select_region.setOnClickListener(this);
        ll_search_site.setOnClickListener(this);
        iv_scan_code.setOnClickListener(this);

        scrollView = mView.findViewById(R.id.scrollView);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_select_region:

                break;
            case R.id.ll_search_site:

                break;
            case R.id.iv_scan_code:

                break;
        }
    }


    private void initData() {
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        level_item_rv = mView.findViewById(R.id.level_item_rv);
        mLevelListBeans = new ArrayList<>();
        updateData();
    }

    private void updateData() {

        getIndexData();
    }

    //-------------------------------------------轮播图---------------------------------------------------

    // 1.初始化ViewPager的内部的view
    public void initViewPagerData() {

        // 添加图片到图片列表里
        mImageList = new ArrayList<>();
//        mBannerInfoBeans.addAll(mBannerInfoBeans);
        ImageView iv;
        for (int i = 0; i < mBannerInfoBeans.size(); i++) {
            iv = new ImageView(mContext);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setId(imgae_ids[i]);                         //给ImageView设置id
            iv.setOnClickListener(new pagerImageOnClick());//设置ImageView点击事件
            LogUtils.i("HomeFragment: IndexBean " + mBannerInfoBeans.get(i).getImg());
            mImageList.add(iv);

            Glide.with(mContext)
                    .load(mBannerInfoBeans.get(i).getImg())
                    .into(iv);
        }

        // 添加轮播点
        if (mDots==null) {

            LinearLayout container = mView.findViewById(R.id.ll_point_container);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.shape_white_poi);
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
//                    LogUtils.i("HomeFragment: IndexBean " + mIndexBean.get(0).getLink());
//                    goToTeacherWeb(mIndexBean.get(0).getLink());
                    break;
                case R.id.pager_image2:
                    break;
                case R.id.pager_image3:

                    break;
                case R.id.pager_image4:

                    break;
                case R.id.pager_image5:

                    break;
            }
        }
    }

    // 跳转页面
    private void goToTeacherWeb(String url) {

        if ("".equals(url)) {
            //            ToastUtil.show(mContext,"链接为空");
            return;
        }

        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("url",url);
        startActivity(intent);
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
                //                LogUtils.i("HomeFragment: IndexBean newPosition " + newPosition);
                //图片下面设置显示文本
                //                mTvPagerTitle.setText(IndexBean.get(newPosition).getText);

                //设置轮播点
                View newView = mDots.get(newPosition);
                Drawable gray_poi = mContext.getResources().getDrawable(R.drawable.shape_gray_poi);
                newView.setBackground(gray_poi);

                View oldView = mDots.get(previousPosition);
                Drawable white_poi = mContext.getResources().getDrawable(R.drawable.shape_white_poi);
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
//           LogUtils.i("HomeFragment: IndexBean runTask " + mViewPager.getCurrentItem());
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            mHandler.postDelayed(this, DELAYED_TIME);

        }
    };

    //-------------------------------------------推荐分类--------------------------------------------

    //初始化GrdView
    private void initAdapter2() {
        LogUtils.i("HomeFragment: Category mlessonCategory.size " + mCatInfoBeans.size());
        mNewCatInfoBeans = new ArrayList<>();
        IndexBean.DataBean.CatInfoBean lessonCategory = new IndexBean.DataBean.CatInfoBean();

        for (int i=0; i<mCatInfoBeans.size(); i++) {
            mNewCatInfoBeans.add(mCatInfoBeans.get(i));
            if (i==8) {
                lessonCategory.setImg("");
//                lessonCategory.setIco2(R.drawable.icon_btn_catgory_all);
                lessonCategory.setId("0");
                lessonCategory.setName("全部分类");
                mNewCatInfoBeans.add(lessonCategory);
            }
        }

        mGridViewAdapter = new FirstCatInfoBeanAdapter(getContext(),mNewCatInfoBeans);
        mGridView.setAdapter(mGridViewAdapter); // 为mGridView设置Adapter
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
                LogUtils.i("HomeFragment: Category onItemClick " + position);

//                Intent intent = new Intent(mContext, AllClassActivity.class);
//                intent.putExtra("ID",mNewCategory.get(position).getId());
//                startActivity(intent);
            }
        });
    }

    //--------------------------------------推荐列表-------------------------------------------------

    private MyRecyclerView level_item_rv;
    private List<IndexBean.DataBean.MessageInfoBean> mLevelListBeans;            // 会员类别
    private LevelVipAdapter mAdapter2;

    private void initData2() {

        mAdapter2 = new LevelVipAdapter(mLevelListBeans);
        level_item_rv.setLayoutManager(new LinearLayoutManager(mContext));
        level_item_rv.setAdapter(mAdapter2);

    }


    //--------------------------------------请求服务器数据--------------------------------------------

    // 1.获取轮播图数据
    private void getIndexData() {

        final String url = HttpURL.BASE_URL + HttpURL.INDEX_INDEX;
        LogUtils.i("HomeFragment: url1 " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("HomeFragment: result1 " + s);
//                    LogUtils.i("HomeFragment: result1 " + UnicodeUtils.revert("\u7b7e\u540d\u9a8c\u8bc1\u5931\u8d25"));

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            JSONObject jsonData = new JSONObject(data);
                            String banner_info = jsonData.getString("banner_info");
                            String cat_info = jsonData.getString("cat_info");
                            String message_info = jsonData.getString("message_info");
                            mBannerInfoBeans = new Gson().fromJson(banner_info, new TypeToken<List<IndexBean.DataBean.BannerInfoBean>>(){}.getType());
                            mCatInfoBeans = new Gson().fromJson(cat_info, new TypeToken<List<IndexBean.DataBean.CatInfoBean>>(){}.getType());
                            mLevelListBeans = new Gson().fromJson(message_info, new TypeToken<List<IndexBean.DataBean.MessageInfoBean>>(){}.getType());

                            LogUtils.i("HomeFragment: mBannerInfoBeans.size " + mBannerInfoBeans.size()
                                    + " mCatInfoBeans.size " + mCatInfoBeans.size()
                           );

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
                LogUtils.e("HomeFragment: volleyError1 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.INDEX_INDEX + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i("HomeFragment: token1 " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("lat", "34.914167");
                map.put("lon", "118.677470");
                map.put("device", CommonParameters.ANDROID);
                map.put("tui", CommonParameters.IS_RECOMMEND);
                map.put(CommonParameters.ACCESS_TOKEN, md5_token);

                LogUtils.i("HomeFragment json1 " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


    // 2.获取一级分类
    private void getCatFirstData() {

        String url = HttpURL.BASE_URL + HttpURL.CAT_FIRST;
        LogUtils.i("HomeFragment: url2 " + url);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("HomeFragment: result2 " + s);


                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("HomeFragment: volleyError2 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.CAT_FIRST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i("HomeFragment: token2 " + token);
                String md5_token = MD5Utils.md5(token);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);

                LogUtils.i("HomeFragment json2 " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 2.获取二级分类
    private void getcatSecondData() {

        String url = HttpURL.BASE_URL + HttpURL.CAT_SECOND;
        LogUtils.i("HomeFragment: url3 " + url);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!"".equals(s)) {
                    LogUtils.i("HomeFragment: result3 " + s);


                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e("HomeFragment: volleyError3 " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.CAT_SECOND + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i("HomeFragment: token3 " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("pid", "34.914167");
                map.put(CommonParameters.ACCESS_TOKEN, md5_token);

                LogUtils.i("HomeFragment json3 " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
