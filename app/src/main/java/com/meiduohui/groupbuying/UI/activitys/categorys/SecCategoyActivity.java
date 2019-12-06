package com.meiduohui.groupbuying.UI.activitys.categorys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.coupons.MessageDetailsActivity;
import com.meiduohui.groupbuying.UI.views.GridSpacingItemDecoration;
import com.meiduohui.groupbuying.UI.views.MyRecyclerView;
import com.meiduohui.groupbuying.adapter.MessageInfoListAdapter;
import com.meiduohui.groupbuying.adapter.SecondCatListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CategoryBean;
import com.meiduohui.groupbuying.bean.IndexBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.ImageUtils;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.PxUtils;
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

public class SecCategoyActivity extends AppCompatActivity {

    private String TAG = "SecCategoyActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.rv_second_cat_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.rv_coupon_list)
    MyRecyclerView mMyRecyclerView;
    @BindView(R.id.PullToRefreshScroll_View)
    PullToRefreshScrollView mPullToRefreshScrollView;

    private List<CategoryBean.SecondInfoBean> mSecondInfoBeans = new ArrayList<>();      // 所有二级级分类
    private SecondCatListAdapter mSecondCatListAdapter;

    private String cat_id;
    private int mPosition;

    private boolean mIsPullUp = false;
    private int mPage = 1;

    private List<IndexBean.MessageInfoBean> mMessageInfos = new ArrayList<>();
    private List<IndexBean.MessageInfoBean> mShowList = new ArrayList<>();
    private MessageInfoListAdapter mAdapter;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILED = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILED = 202;
    private final int ORDER_ADDZAN_RESULT_SUCCESS = 301;
    private final int ORDER_ADDZAN_RESULT_FAILED = 302;
    private final int ORDER_ADDZF_RESULT_SUCCESS = 211;
    private final int ORDER_ADDZF_RESULT_FAILED = 222;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    initCategoryList();
                    break;

                case LOAD_DATA1_FAILED:

                    break;
                case LOAD_DATA2_SUCCESS:

                    if (!mIsPullUp) {

                        if (mMessageInfos.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "没有信息~");
                        }
                    }
                    updataListView();
                    break;

                case LOAD_DATA2_FAILED:

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

                case ORDER_ADDZAN_RESULT_FAILED:
                    ToastUtil.show(mContext,(String) msg.obj);
                    break;

                case ORDER_ADDZF_RESULT_SUCCESS:

                    IndexBean.MessageInfoBean tuiMsg = mShowList.get(mPosition);
                    tuiMsg.setZf((Integer.parseInt(tuiMsg.getZf()) + 1) + "");
                    mAdapter.notifyDataSetChanged();
                    break;

                case ORDER_ADDZF_RESULT_FAILED:
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
        setContentView(R.layout.activity_sec_categoy);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
        initPullListView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (GlobalParameterApplication.isShareSussess) {
            GlobalParameterApplication.isShareSussess = false;
            addZf(mShowList.get(mPosition).getOrder_id());
        }
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        Intent intent = getIntent();
        if (intent != null) {
            cat_id= intent.getStringExtra("cat_id1");
            LogUtils.i(TAG + "initData cat_id " + cat_id);

            getCatSesondData(cat_id);
            getIndexData();
        }

        mShowList = new ArrayList<>();
        mAdapter = new MessageInfoListAdapter(mContext,mShowList);
        mAdapter.setOnItemClickListener(new MessageInfoListAdapter.OnItemClickListener() {
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
                intent.putExtra("onComment", true);
                startActivity(intent);
            }

            @Override
            public void onZF(final int position) {

                mPosition = position;
                GlobalParameterApplication.shareIntention = CommonParameters.SHARE_MESSAGE;
                showShare();
            }

            @Override
            public void onZan(int position) {
                LogUtils.i(TAG + "addZan onZan " + position);
                mPosition = position;
                addZan(mShowList.get(position).getOrder_id());
            }
        });
        mMyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mMyRecyclerView.setAdapter(mAdapter);
    }

    // 数据
    private void initCategoryList() {

        mSecondCatListAdapter = new SecondCatListAdapter(mContext, mSecondInfoBeans);
        mSecondCatListAdapter.setOnItemClickListener(new SecondCatListAdapter.OnItemClickListener() {
            @Override
            public void onCallbackClick(String id1, String id2, String catName) {
                LogUtils.i(TAG + "initCategoryList setOnItemClickListener cat_id " + cat_id
                        + " id2 " + id2
                        + " id2 " + id2);
                Intent intent = new Intent(mContext, MessageListActivity.class);
                intent.putExtra("cat_id1", cat_id);
                intent.putExtra("cat_id2", id2);
                startActivity(intent);
            }

        });
        GridLayoutManager layoutManage = new GridLayoutManager(mContext, 5);
        mRecyclerView.setLayoutManager(layoutManage);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(5, PxUtils.dip2px(mContext, 15), true));
        mRecyclerView.setAdapter(mSecondCatListAdapter);
    }

    // 初始化列表
    private void initPullListView() {

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

                addtoBottom();      // 请求网络数据
                refreshComplete();  // 数据加载完成后，关闭header,footer
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

        mPullToRefreshScrollView.postDelayed(new Runnable() {
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

    @OnClick({R.id.iv_back, R.id.tv_search_site})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_search_site:
                Intent intent = new Intent(mContext, MessageListActivity.class);
                intent.putExtra("search",0);
                startActivity(intent);
                break;
        }
    }

    private PopupWindow popupWindow;

    public void showShare() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_we_share, null);

        LinearLayout mSession = view.findViewById(R.id.ll_we_Session);
        LinearLayout mTimeline = view.findViewById(R.id.ll_we_Timeline);
        TextView mCancel = view.findViewById(R.id.tv_cancel);

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
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
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        // 好友
        mSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(0);
                popupWindow.dismiss();
            }
        });

        // 朋友圈
        mTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(1);
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

    private void share(final int type) {

        String url = "";
        if (!TextUtils.isEmpty(mShowList.get(mPosition).getVideo())){
            url = mShowList.get(mPosition).getVideo() + CommonParameters.VIDEO_END;
        } else {
            url = mShowList.get(mPosition).getImg().get(0);
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
                                + "_" + mShowList.get(mPosition).getOrder_id() + "_" + CommonParameters.TYPE_SHOP,
                        mShowList.get(mPosition).getTitle(), mShowList.get(mPosition).getIntro(), bitmap1, type);
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
                                + "_" + mShowList.get(mPosition).getOrder_id() + "_" + CommonParameters.TYPE_SHOP,
                        mShowList.get(mPosition).getTitle(), mShowList.get(mPosition).getIntro(), null, type);
            }
        });

    }


    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取二级分类
    private void getCatSesondData(final String id) {

        String url = HttpURL.BASE_URL + HttpURL.CAT_SECOND;
        LogUtils.i(TAG + "getCatSesondData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getCatSesondData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getCatSesondData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mSecondInfoBeans = new Gson().fromJson(data, new TypeToken<List<CategoryBean.SecondInfoBean>>() {
                            }.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getCatSesondData mCategoryBeans.size " + mSecondInfoBeans.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getCatSesondData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.CAT_SECOND + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getCatSesondData token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("pid", id);
                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getCatSesondData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

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

                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);
                            return;
                        }
                        mHandler.sendEmptyMessage(LOAD_DATA2_FAILED);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA2_FAILED);
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
                    map.put("county", GlobalParameterApplication.mCounty);
                }

                map.put("cat_id1", cat_id);

                if (mUserBean != null)
                    map.put("mem_id", mUserBean.getId());

                map.put("page", mPage + "");

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
                            mHandler.obtainMessage(ORDER_ADDZF_RESULT_FAILED,msg).sendToTarget();
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
                            mHandler.obtainMessage(ORDER_ADDZAN_RESULT_FAILED,msg).sendToTarget();
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

}
