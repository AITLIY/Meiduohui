package com.meiduohui.groupbuying.UI.activitys.mine.wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.adapter.AccountListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.RecordBean;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountListActivity extends AppCompatActivity {

    private String TAG = "AccountListActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.tv_start_time)
    TextView mTvStartTime;
    @BindView(R.id.tv_end_time)
    TextView mTvEndTime;
    @BindView(R.id.tv_account_type)
    TextView mTvAccountType;
    @BindView(R.id.tv_zhichu)
    TextView mTvZhichu;
    @BindView(R.id.tv_shouru)
    TextView mTvShouru;
    @BindView(R.id.ptr_coupon_list)
    PullToRefreshListView mPullToRefreshListView;

    private String mStartDate = "";
    private String mEndDate = "";

    private int mPage = 1;
    private boolean mIsPullUp = false;
    private RecordBean mRecordBean = new RecordBean();
    private List<RecordBean.RecordListBean> mRecordListBeans = new ArrayList<>();       // 搜索结果列表
    private List<RecordBean.RecordListBean> mShowList = new ArrayList<>();              // 显示的列表
    private AccountListAdapter mAdapter;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILED = 102;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    setContent();
                    if (!mIsPullUp) {

                        if (mRecordListBeans.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "没有交易信息~");
                        }
                    }
                    updataListView();

                    break;

                case LOAD_DATA1_FAILED:
                    setViewForResult(false, "查询数据失败~");
                    break;

                case NET_ERROR:
                    setViewForResult(false, "网络异常,请稍后重试~");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    private void init() {
        initData();
        initPullToRefresh();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        mAdapter = new AccountListAdapter(mContext, mShowList);
        mPullToRefreshListView.setAdapter(mAdapter);

        setData();

        if (mUserBean != null)
            getRecord();
    }

    private void setData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);

        mStartDate = year + "-" + month + "-" + 1;
        mEndDate = year + "-" + month + "-" + date;

        mTvStartTime.setText(mStartDate);
        mTvEndTime.setText(mEndDate);
    }

    private void setContent() {

        mTvZhichu.setText("支出 ¥" + mRecordBean.getZhichu());
        mTvShouru.setText("收入 ¥" + mRecordBean.getShouru());
    }

    @OnClick({R.id.iv_back, R.id.tv_start_time, R.id.tv_end_time, R.id.tv_account_type})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_start_time:
                setTvStartTime();
                break;

            case R.id.tv_end_time:
                setTvEndTime();
                break;

            case R.id.tv_account_type:
                setType();
                break;
        }
    }

    private void setTvStartTime() {

        Calendar calendar = Calendar.getInstance();
        String dates[] = mStartDate.split("-");
        calendar.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]));

        TimePickerView pvTime = new TimePickerView.Builder(AccountListActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                String time = TimeUtils.DateToString(date, "yyyy-MM-dd");
                mTvStartTime.setText(time);
                mStartDate = time;
                addtoTop();
                LogUtils.i(TAG + "setTvEndTime mStartTime " + mStartDate);
            }
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(20)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                //                        .setTitleText("请选择时间")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.app_title_bar))//确定按钮文字颜色
                .setCancelColor(Color.GRAY)//取消按钮文字颜色
                //                .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                //                .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                //                .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年
                //                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                //                .setRangDate(startDate,endDate)//起始终止年月日设定
                //                .setLabel("年","月","日","时","分","秒")
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                //                .isDialog(true)//是否显示为对话框样式
                .build();
        pvTime.setDate(calendar);//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();
    }

    private void setTvEndTime() {

        Calendar calendar = Calendar.getInstance();
        String dates[] = mEndDate.split("-");
        calendar.set(Integer.parseInt(dates[0]), Integer.parseInt(dates[1]) - 1, Integer.parseInt(dates[2]));

        TimePickerView pvTime = new TimePickerView.Builder(AccountListActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                String time = TimeUtils.DateToString(date, "yyyy-MM-dd");
                mTvEndTime.setText(time);
                mEndDate = time;
                addtoTop();
                LogUtils.i(TAG + "setTvEndTime mEndTime " + mEndDate);
            }
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(20)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                //                        .setTitleText("请选择时间")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.app_title_bar))//确定按钮文字颜色
                .setCancelColor(Color.GRAY)//取消按钮文字颜色
                //                .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                //                .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                //                .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年
                //                                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                //                .setRangDate(startDate,endDate)//起始终止年月日设定
                //                                .setLabel("年","月","日","时","分","秒")
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                //                .isDialog(true)//是否显示为对话框样式
                .build();
        pvTime.setDate(calendar);//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();
    }

    private String mType = "";
    private static final List<String> options1Items = new ArrayList<>();

    private void setType() {

        options1Items.clear();
        options1Items.add("全部账单");
        options1Items.add("收入");
        options1Items.add("支出");

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(AccountListActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String s = options1Items.get(options1);
                mTvAccountType.setText(s);
                addtoTop();
                switch (s) {

                    case "全部账单":
                        mType = "";
                        mTvZhichu.setVisibility(View.VISIBLE);
                        mTvShouru.setVisibility(View.VISIBLE);
                        break;

                    case "收入":
                        mType = "1";
                        mTvZhichu.setVisibility(View.GONE);
                        mTvShouru.setVisibility(View.VISIBLE);
                        break;

                    case "支出":
                        mType = "0";
                        mTvZhichu.setVisibility(View.VISIBLE);
                        mTvShouru.setVisibility(View.GONE);
                        break;
                }

                LogUtils.i(TAG + "setType s " + s);
            }
        })
                //                .setSubmitText("确定")//确定按钮文字
                //                .setCancelText("取消")//取消按钮文字
                //                .setTitleText("城市选择")//标题
                .setSubCalSize(20)//确定和取消文字大小
                //                .setTitleSize(20)//标题文字大小
                //                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.app_title_bar))//确定按钮文字颜色
                .setCancelColor(Color.GRAY)//取消按钮文字颜色
                //                .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
                //                .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
                //                .setContentTextSize(18)//滚轮文字大小
                //                .setTextColorCenter(Color.BLUE)//设置选中项的颜色
                .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                //                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                //                .setLinkage(false)//设置是否联动，默认true
                //                .setLabels("省", "市", "区")//设置选择的三级单位
                //                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                //                .setCyclic(false, false, false)//循环与否
                //                .setSelectOptions(1, 1, 1)  //设置默认选中项
                //                .setOutSideCancelable(false)//点击外部dismiss default true
                //                .isDialog(true)//是否显示为对话框样式
                .build();
        pvOptions.setPicker(options1Items);
        pvOptions.show();
    }

    private void initPullToRefresh() {

        // 1.设置模式
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        // 2.1 通过调用getLoadingLayoutProxy方法，设置下拉刷新状况布局中显示的文字 ，第一个参数为true,代表下拉刷新
        ILoadingLayout headLables = mPullToRefreshListView.getLoadingLayoutProxy(true, false);
        headLables.setPullLabel("下拉刷新");
        headLables.setRefreshingLabel("正在刷新...");
        headLables.setReleaseLabel("放开刷新");

        // 2.2 设置上拉加载底部视图中显示的文字，第一个参数为false,代表上拉加载更多
        ILoadingLayout footerLables = mPullToRefreshListView.getLoadingLayoutProxy(false, true);
        footerLables.setPullLabel("上拉加载");
        footerLables.setRefreshingLabel("正在载入...");
        footerLables.setReleaseLabel("放开加载更多");

        // 3.设置监听事件
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        getRecord();
    }

    // 上拉加载的方法:
    public void addtoBottom() {

        mPage++;
        mIsPullUp = true;
        getRecord();
    }

    // 刷新完成时关闭
    public void refreshComplete() {
        mHandler.postDelayed(new Runnable() {
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
            mShowList.addAll(mRecordListBeans);

            mAdapter.notifyDataSetChanged();

        } else {

            mShowList.addAll(mRecordListBeans);
            mAdapter.notifyDataSetChanged();
            if (mRecordListBeans.size() == 0) {
                ToastUtils.show(mContext, "没有更多结果");
            }
        }
    }


    //--------------------------------------请求服务器数据--------------------------------------------

    // 资金流水
    private void getRecord() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_RECORD;
        LogUtils.i(TAG + "getRecord url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getRecord result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getRecord msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mRecordBean = new Gson().fromJson(data, RecordBean.class);
                            mRecordListBeans = mRecordBean.getRecord_list();

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getRecord mRecordListBeans.size " + mRecordListBeans.size());
                        } else {

                            mHandler.sendEmptyMessage(LOAD_DATA1_FAILED);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getRecord volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_RECORD + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getRecord token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                if (!TextUtils.isEmpty(mType))
                    map.put("type", mType);
                map.put("date", mStartDate + "_" + mEndDate);
                map.put("page", mPage + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getRecord json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}