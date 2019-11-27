package com.meiduohui.groupbuying.UI.activitys.publish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.OptionsPickerView;
import com.githang.statusbar.StatusBarCompat;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.NetworkUtils;
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

public class GeneralQuanActivity extends AppCompatActivity {

    private String TAG = "GeneralQuanActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.tv_type)
    TextView mTvType;
    @BindView(R.id.ed_number)
    EditText mEdNumber;
    @BindView(R.id.ed_price)
    EditText mEdPrice;
    @BindView(R.id.ed_yxq)
    EditText mEdYxq;

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
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, (String) msg.obj);
                    finish();
                    break;

                case LOAD_DATA1_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, (String) msg.obj);
                    break;

                case NET_ERROR:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, "网络异常,请稍后再试");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_quan);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initDailog();
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
    }

    @OnClick({R.id.iv_back, R.id.ll_type, R.id.tv_affirm})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:

                finish();
                break;

            case R.id.ll_type:

                setType();
                break;

            case R.id.tv_affirm:

                String type = mTvType.getText().toString();
                String number = mEdNumber.getText().toString();
                String price = mEdPrice.getText().toString();
                String yxq = mEdYxq.getText().toString();

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"网络异常,请稍后重试");
                    return;
                }

                if (TextUtils.isEmpty(type)) {

                    ToastUtil.show(mContext,"请选择优惠券类型");
                    return;
                } else if (TextUtils.isEmpty(number)) {

                    ToastUtil.show(mContext,"请输入卡券数量");
                    return;
                } else if (TextUtils.isEmpty(price)) {

                    ToastUtil.show(mContext,"请输入价格");
                    return;
                } else if (TextUtils.isEmpty(yxq)) {

                    ToastUtil.show(mContext,"请输入有效期");
                    return;
                }

                mLoadingDailog.show();
                addGeneraQuan(number,price,yxq);
                break;
        }
    }

    private int mType;
    private static final List<String> options1Items = new ArrayList<>();

    private void setType() {

        options1Items.clear();
        options1Items.add("代金券");
        options1Items.add("折扣券");
//        options1Items.add("会员券");

        OptionsPickerView pvOptions = new  OptionsPickerView.Builder(GeneralQuanActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                //返回的分别是三个级别的选中位置
                String s =  options1Items.get(options1);
                mTvType.setText(s);

                switch (s) {

                    case "代金券":
                        mType = 1;
                        mEdPrice.setHint("请输入代金券的价格");
                        break;

                    case "折扣券":

                        mEdPrice.setHint("请输入折扣(例如0.8,0.9)");
                        break;

                    case "会员券":
                        mType = 3;
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


    //--------------------------------------请求服务器数据--------------------------------------------

    // 发布通用券
    private void addGeneraQuan(final String number, final String price, final String yxq) {

        final String url = HttpURL.BASE_URL + HttpURL.SHOP_ADDQUAN;
        LogUtils.i(TAG + "addGeneraQuan url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "addGeneraQuan result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "addGeneraQuan msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(LOAD_DATA1_SUCCESS,msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(LOAD_DATA1_FAILED,msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "addGeneraQuan volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SHOP_ADDQUAN + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "addGeneraQuan token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("shop_id", mUserBean.getShop_id());
                map.put("type", mType+"");
                map.put("number", number);
                map.put("price", price);

                String str = "";
                String con = "";

                if (mType == 1) {
                    double pri = Double.parseDouble(price);
//                    str = String.format("%.2f", pri) + "元";
                    str = price + "元";
                    con = "代金券";

                } else if (mType == 2) {
                    double cut = Double.parseDouble(price) * 10;
//                    str = String.format("%.1f", cut) + "折";
                    str = price + "折";
                    con = "折扣券";

                } else if (mType == 3) {
                    double pri = Double.parseDouble(price);
//                    str = String.format("%.2f", pri) + "元";
                    str = price + "元";
                    con = "会员券";
                }

                map.put("content", str+con);
                map.put("yxq", yxq);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "addGeneraQuan json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
