package com.meiduohui.groupbuying.UI.fragments.home;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.coupons.CouponDetailsActivity;
import com.meiduohui.groupbuying.adapter.CouponListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CouponBean;
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
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CouponFragment extends Fragment {

    private String TAG = "CouponFragment: ";
    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;

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

    @BindView(R.id.coupon_item_list)
    PullToRefreshListView mPullToRefreshListView;

    private ArrayList<CouponBean> mShowList;                 // 优惠券显示的列表
    private ArrayList<CouponBean> mCouponBeans;              // 优惠券搜索结果列表
    private CouponListAdapter mAdapter;

    private int page = 1;
    private int state = 0;
    private final int IS_USED  = 0;        //参数查询
    private final int IS_UNUSED = 1;        //参数查询
    private final int IS_EXPIRED = 2;        //参数查询


    private boolean mIsPullUp = false;  // 查询的标志

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    if (!mIsPullUp) {

                        if (mCouponBeans.size()>0){
                            setViewForResult(true,"");

                        } else {
                            setViewForResult(false,"您还没有优惠券~");
                        }
                    }
                    upDataLessonListView();
                    break;

                case LOAD_DATA1_FAILE:

                    setViewForResult(false,"查询数据失败~");
                    break;

                case NET_ERROR:

                    setViewForResult(false,"网络异常,请稍后重试~");
                    break;
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_coupon, container, false);
        unbinder = ButterKnife.bind(this,mView);

        init();

        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        initPullListView();
    }

    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();

        mShowList = new ArrayList<>();
        mAdapter = new CouponListAdapter(mContext, mShowList);
        mPullToRefreshListView.setAdapter(mAdapter);

        getQuanList();     // 初始化数据
    }

    @OnClick({R.id.unused_rl,R.id.used_rl,R.id.expired_rl})
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
//                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
//                    startActivity(new Intent(mContext, LoginActivity.class));
//                } else {
                    CouponBean couponBean = mShowList.get(position-1);
                    LogUtils.i("AllClassFragment: ItemClick position " + position);
                    Intent intent = new Intent(mContext, CouponDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CouponBean",couponBean);
                    intent.putExtras(bundle);
                    startActivity(intent);
//                }
            }
        });
    }

    // 下拉刷新的方法:
    public void addtoTop(){
        page = 1;
        mIsPullUp = false;
        getQuanList();     // 下拉刷新；
    }

    // 上拉加载的方法:
    public void addtoBottom(){
        page++;
        mIsPullUp = true;
        getQuanList();     // 加载更多；
    }

    // 刷新完成时关闭
    public void refreshComplete(){
        
        mPullToRefreshListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshListView.onRefreshComplete();
            }
        }, 1000);
    }

    // 根据获取结果显示view
    private void setViewForResult(boolean isSuccess,String msg) {

        if (isSuccess) {
            mView.findViewById(R.id.not_data).setVisibility(View.GONE);

        } else {
            mView.findViewById(R.id.not_data).setVisibility(View.VISIBLE);
            ((TextView) mView.findViewById(R.id.not_data_tv)).setText(msg);
        }
    }

    // 更新列表数据
    private void upDataLessonListView() {

        if (!mIsPullUp) {

            mShowList.clear();
            mShowList.addAll(mCouponBeans);

            mAdapter.notifyDataSetChanged();
//            mPullToRefreshListView.getRefreshableView().smoothScrollToPosition(0);//移动到首部
        } else {

            mShowList.addAll(mCouponBeans);
            mAdapter.notifyDataSetChanged();
            if (mCouponBeans.size() == 0) {
                ToastUtil.show(mContext, "没有更多结果");
            }
        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        unused_tv.setTextColor(view.getId() == R.id.unused_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general2));
        used_tv.setTextColor(view.getId() == R.id.used_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general2));
        expired_tv.setTextColor(view.getId() == R.id.expired_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general2));

        unused_v.setVisibility(view.getId() ==  R.id.unused_rl ? View.VISIBLE:View.GONE);
        used_v.setVisibility(view.getId() == R.id.used_rl ? View.VISIBLE:View.GONE);
        expired_v.setVisibility(view.getId() == R.id.expired_rl ? View.VISIBLE:View.GONE);
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取一级分类
    private void getQuanList() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_QUANLIST;
        LogUtils.i(TAG + "getQuanList url " + url);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
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
                            mCouponBeans = new Gson().fromJson(data, new TypeToken<List<CouponBean>>() {}.getType());
//
                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getQuanList mCategoryBeans.size " + mCouponBeans.size());
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

                if (GlobalParameterApplication.getInstance().getUserIsShop())
                    map.put("shop_id", 1+"");
                else
                    map.put("mem_id", 1+"");

                map.put("page", page+"");
                map.put("state", state+"");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getQuanList json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


}
