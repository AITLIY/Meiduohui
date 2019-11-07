package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.zxing.util.QrCodeGenerator;
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

    @BindView(R.id.rv_invite)
    RelativeLayout mRvInvite;
    @BindView(R.id.iv_qr_code)
    ImageView mIvQrCode;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;

    private CouponBean mCouponBean;
    private String mQrCode="";

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

                    LoadQrCode();
                    break;

                case GET_QRCODE_FAIL:
                    ToastUtil.show(mContext, (String) msg.obj);
                    break;

                case NET_ERROR:

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

        initData();
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

    @OnClick({R.id.iv_back, R.id.iv_close, R.id.tv_right_away_used,R.id.iv_go_address, R.id.iv_call_shops})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_close:
                mRvInvite.setVisibility(View.GONE);
                break;
            case R.id.tv_right_away_used:
//                generateQrCode();
                getQuanQrcode();
                break;
            case R.id.iv_go_address:
                showMapSelect();
                break;
            case R.id.iv_call_shops:
                showCallSelect();
                break;
        }
    }

    private void LoadQrCode() {

        mRvInvite.setVisibility(View.VISIBLE);
        mTvShopName.setText(mCouponBean.getShop_name());

        Glide.with(mContext)
                .load(mQrCode)
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mIvQrCode);
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

        mRvInvite.setVisibility(View.VISIBLE);
        mTvShopName.setText(mCouponBean.getShop_name());

        Bitmap bitmap = QrCodeGenerator.getQrCodeImage(date, mIvQrCode.getWidth(), mIvQrCode.getHeight());
        if (bitmap == null) {
            ToastUtil.show(mContext, "生成二维码出错");
            mIvQrCode.setImageResource(R.drawable.icon_bg_default_img);

        } else {
            mIvQrCode.setImageBitmap(bitmap);
        }
    }

    private PopupWindow popupWindow;

    public void showMapSelect() {

        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.alpha = 0.6f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        window.setAttributes(wl);

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_select_map, null);

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                Window window = getWindow();
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                window.setAttributes(wl);
            }
        });


        final Double dlat = Double.parseDouble(mCouponBean.getWd());
        final Double dlon = Double.parseDouble(mCouponBean.getJd());
        final String address = mCouponBean.getAddress();


        // 高德导航
        view.findViewById(R.id.tv_gaode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MapUtil.isGdMapInstalled()) {
                    MapUtil.openGaoDeNavi(mContext, 0, 0, null, dlat, dlon, address);

                } else {
                    //这里必须要写逻辑，不然如果手机没安装该应用，程序会闪退，这里可以实现下载安装该地图应用
                    Toast.makeText(mContext, "未安装高德地图", Toast.LENGTH_SHORT).show();
                }
                popupWindow.dismiss();

            }
        });

        // 百度导航
        view.findViewById(R.id.tv_baidu).setOnClickListener(new View.OnClickListener() {
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

        // 取消
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, -view.getHeight());
    }

    private PopupWindow popupWindow2;

    public void showCallSelect() {

        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.alpha = 0.6f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        window.setAttributes(wl);

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_call, null);

        popupWindow2 = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow2.setFocusable(true);
        popupWindow2.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                Window window = getWindow();
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                window.setAttributes(wl);
            }
        });

        TextView call = view.findViewById(R.id.tv_call_number);
        call.setText("拨打：" + mCouponBean.getSjh());

        // 打电话
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCall();
                popupWindow2.dismiss();

            }
        });

        // 取消
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow2.dismiss();
            }
        });

        popupWindow2.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, -view.getHeight());
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

                map.put("order_id", mCouponBean.getQ_id());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getQuanQrcode json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
