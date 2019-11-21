package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDailog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaeger.library.StatusBarUtil;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CouponBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.MapUtil;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CouponDetailsActivity extends AppCompatActivity {

    private String TAG = "CouponDetailsActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;

    @BindView(R.id.tv_q_price)
    TextView mTvQPrice;
    @BindView(R.id.tv_q_type)
    TextView mTvQType;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.tv_use_time)
    TextView mTvUseTime;
    @BindView(R.id.tv_shop_name2)
    TextView mTvShopName2;
    @BindView(R.id.tv_shop_intro)
    TextView mTvShopIntro;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.tv_sjh)
    TextView mTvSjh;
    @BindView(R.id.beizhu)
    TextView mBeizhu;

    private CouponBean mCouponBean;
    private String mQrCode = "";

    private static final int GET_QRCODE_SUCCESS = 401;
    private static final int GET_QRCODE_FAIL = 402;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case GET_QRCODE_SUCCESS:
//                    generateQrCode();
                    mLoadingDailog.dismiss();
                    LoadQrCode();
                    break;

                case GET_QRCODE_FAIL:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, (String) msg.obj);
                    break;

                case NET_ERROR:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, "网络异常,请稍后重试~");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_details);
        StatusBarUtil.setTranslucentForImageView(this, 0, findViewById(R.id.needOffsetView));
        ButterKnife.bind(this);

        init();
    }

    private void init() {
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

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            mCouponBean = (CouponBean) bundle.getSerializable("CouponBean");

            LogUtils.i(TAG + "initData Shop_name " + mCouponBean.getShop_name());
            setContent();
        }

    }

    private void setContent() {

        String q_type = mCouponBean.getQ_type();
        String s_type = "优惠券";
        if (q_type.equals("1")) {
            mTvQPrice.setText("¥" + mCouponBean.getQ_price());
            s_type = "代金券";

        } else if (q_type.equals("2")) {

            double discount = Double.parseDouble(mCouponBean.getQ_price()) * 10;
            mTvQPrice.setText( discount + "折");
            s_type = "折扣券";

        } else if (q_type.equals("3")) {
            mTvQPrice.setText("¥" + mCouponBean.getQ_price());
            s_type = "会员券";
        }
        mTvQType.setText(s_type);

        if (!TextUtils.isEmpty(mCouponBean.getTitle()))
            mTvContent.setText(mCouponBean.getTitle());
        else {
            String q_id = mCouponBean.getM_id();
            String s_content = "商家通用券";
            if (q_id.equals("0"))
                s_content = "商家通用券";
            mTvContent.setText(s_content);
        }

        String startTime = mCouponBean.getStart_time();
        String endTime = mCouponBean.getEnd_time();
        if (startTime==null)
            startTime = "0";
        if (endTime==null)
            endTime = "0";

        mTvUseTime.setText("有效时间：" + TimeUtils.LongToString(Long.parseLong(startTime),"yyyy.MM.dd")
                + " - " + TimeUtils.LongToString(Long.parseLong(endTime),"yyyy.MM.dd"));

        mTvShopName2.setText(mCouponBean.getShop_name());
        mTvShopIntro.setText(mCouponBean.getShop_intro());
        mTvAddress.setText(mCouponBean.getAddress());
        mTvSjh.setText("电话：" + mCouponBean.getSjh());
        mBeizhu.setText(mCouponBean.getBeizhu());

    }

    @OnClick({R.id.iv_back, R.id.tv_right_away_used,R.id.iv_go_address, R.id.iv_call_shops})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_right_away_used:

                if (mCouponBean.getM_id().equals("0")) {
//                generateQrCode();
                    mLoadingDailog.show();
                    getQuanQrcode();
                    //                    generateQrCode(position);
                } else {
                    Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                    intent.putExtra("Order_id", mCouponBean.getM_id());
                    startActivity(intent);
                }

                break;
            case R.id.iv_go_address:
                showMapSelect();
                break;
            case R.id.iv_call_shops:
                showCallSelect();
                break;
        }
    }

    private PopupWindow popupWindow;

    public void showMapSelect() {

        if (mCouponBean==null)
            return;

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_select_map, null);

        TextView mGaode = view.findViewById(R.id.tv_gaode);
        TextView mBaidu = view.findViewById(R.id.tv_baidu);
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

        final Double dlat = Double.parseDouble(mCouponBean.getWd());
        final Double dlon = Double.parseDouble(mCouponBean.getJd());
        final String address = mCouponBean.getAddress();

        // 高德导航
        mGaode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MapUtil.isGdMapInstalled()) {
                    MapUtil.openGaoDeNavi(mContext, 0, 0, null, dlat, dlon, address);

                } else {
                    //这里必须要写逻辑，不然如果手机没安装该应用，程序会闪退，这里可以实现下载安装该地图应用
                    Toast.makeText(mContext, "未安装高德地图", Toast.LENGTH_SHORT).show();
                }
                popupWindow.dismiss();

                popupWindow.dismiss();

            }
        });

        // 百度导航
        mBaidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MapUtil.isBaiduMapInstalled()) {
                    MapUtil.openBaiDuNavi(mContext, 0, 0, null, dlat, dlon, address);

                } else {
                    //这里必须要写逻辑，不然如果手机没安装该应用，程序会闪退，这里可以实现下载安装该地图应用
                    Toast.makeText(mContext, "未安装百度地图", Toast.LENGTH_SHORT).show();
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

    private PopupWindow popupWindow2;

    public void showCallSelect() {

        if (mCouponBean==null)
            return;

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_call, null);

        TextView mCall = view.findViewById(R.id.tv_call_number);
        TextView mCancel = view.findViewById(R.id.tv_cancel);

        popupWindow2 = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow2.setFocusable(true);
        popupWindow2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.alpha = 0.5f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        getWindow().setAttributes(wl);
        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                WindowManager.LayoutParams wl = getWindow().getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                getWindow().setAttributes(wl);
            }
        });
        popupWindow2.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        mCall.setText("拨打：" + mCouponBean.getSjh());
        // 打电话
        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCall();
                popupWindow2.dismiss();
            }
        });

        //取消
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow2.dismiss();
            }
        });

    }

    private static final int CALL_PHONE = 1000;

    // 打电话
    private void getCall() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCouponBean.getSjh())));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LogUtils.i(TAG + " onRequestPermissionsResult SUCCESS");

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mCouponBean.getSjh())));

                } else {
                    LogUtils.i(TAG + " onRequestPermissionsResult FAILED");
                    ToastUtil.show(mContext, "您已取消授权，无法打电话");
                }
                break;

        }
    }

    private PopupWindow popupWindow3;

    public void LoadQrCode() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_quan_qr, null);

        TextView mName = view.findViewById(R.id.tv_shop_name);
        ImageView mImg = view.findViewById(R.id.iv_qr_code);
        ImageView mClose = view.findViewById(R.id.iv_close);

        popupWindow3 = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow3.setFocusable(false);
        popupWindow3.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow3.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        popupWindow3.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.alpha = 0.5f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        getWindow().setAttributes(wl);
        popupWindow3.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                WindowManager.LayoutParams wl = getWindow().getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                getWindow().setAttributes(wl);
            }
        });
        popupWindow3.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        mName.setText(mCouponBean.getShop_name());

        Glide.with(mContext)
                .load(mQrCode)
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mImg);
        // 关闭
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow3.dismiss();
            }
        });

    }

    /**
     * 生成二维码
     */
    private void generateQrCode() {
        if (TextUtils.isEmpty(mCouponBean.getQ_id())) {
            ToastUtil.show(mContext, "操作失败");
            return;
        }

        String date = CommonParameters.DOWNLOAD_URL + "_" + mCouponBean.getQ_id() + "_" + "2";

        //        mRvInvite.setVisibility(View.VISIBLE);
        //        mTvShopName.setText(mCouponBean.getShop_name());
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

    // 生成通用券核销二维码
    private void getQuanQrcode() {

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
                            mHandler.sendEmptyMessage(GET_QRCODE_FAIL);
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

                map.put("quan_id", mCouponBean.getQ_id());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getQuanQrcode json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
