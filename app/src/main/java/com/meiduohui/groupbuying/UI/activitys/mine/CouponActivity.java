package com.meiduohui.groupbuying.UI.activitys.mine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.adapter.ShopCouponListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.ShopCouponBean;
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

public class CouponActivity extends AppCompatActivity {

    private String TAG = "CouponActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;


    @BindView(R.id.ptr_coupon_list)
    PullToRefreshListView mPullToRefreshListView;

    private boolean mIsPullUp = false;
    private int mPage = 1;

    private ArrayList<ShopCouponBean> mShowList;                 // 优惠券显示的列表
    private ArrayList<ShopCouponBean> mShopCouponBeanss;              // 优惠券搜索结果列表
    private ShopCouponListAdapter mAdapter;

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

                        if (mShopCouponBeanss.size()>0){
                            setViewForResult(true,"");

                        } else {
                            setViewForResult(false,"您还没发布过优惠券~");
                        }
                    }
                    updataListView();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    private void init() {
        initView();
        initData();
    }

    private void initView() {
        initPullListView();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        mShowList = new ArrayList<>();
        mAdapter = new ShopCouponListAdapter(mContext, mShowList);
        mPullToRefreshListView.setAdapter(mAdapter);

        getShopQuanList();     // 初始化数据
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

//                CouponBean couponBean = mShowList.get(position - 1);
//                LogUtils.i(TAG + "ItemClick position " + position);
//                Intent intent = new Intent(mContext, CouponDetailsActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("CouponBean", couponBean);
//                intent.putExtras(bundle);
//                startActivity(intent);
            }
        });
    }

    // 下拉刷新的方法:
    public void addtoTop(){
        mPage = 1;
        mIsPullUp = false;
        getShopQuanList();     // 下拉刷新；
    }

    // 上拉加载的方法:
    public void addtoBottom(){
        mPage++;
        mIsPullUp = true;
        getShopQuanList();     // 加载更多；
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
            mShowList.addAll(mShopCouponBeanss);

            mAdapter.notifyDataSetChanged();
            //            mPullToRefreshListView.getRefreshableView().smoothScrollToPosition(0);//移动到首部
        } else {

            mShowList.addAll(mShopCouponBeanss);
            mAdapter.notifyDataSetChanged();
            if (mShopCouponBeanss.size() == 0) {
                ToastUtil.show(mContext, "没有更多结果");
            }
        }
    }

    //--------------------------------------请求服务器数据--------------------------------------------

    // 优惠券列表
    private void getShopQuanList() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_QUANLIST;
        LogUtils.i(TAG + "getShopQuanList url " + url);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,url,new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getShopQuanList result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getShopQuanList msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mShopCouponBeanss = new Gson().fromJson(data, new TypeToken<List<ShopCouponBean>>() {}.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getShopQuanList mShopCouponBeanss.size " + mShopCouponBeanss.size());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getShopQuanList volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_QUANLIST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getShopQuanList token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("shop_id", mUserBean.getShop_id());
                map.put("page", mPage+"");
//                map.put("state", state+"");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getShopQuanList json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
