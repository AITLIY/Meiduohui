package com.meiduohui.groupbuying.UI.activitys;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.jaeger.library.StatusBarUtil;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.coupons.MessageDetailsActivity;
import com.meiduohui.groupbuying.UI.activitys.login.BindMobileActivity;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.wallet.MyWalletActivity;
import com.meiduohui.groupbuying.UI.activitys.publish.ComboActivity;
import com.meiduohui.groupbuying.UI.activitys.publish.GeneralQuanActivity;
import com.meiduohui.groupbuying.UI.activitys.publish.RedPacketActivity;
import com.meiduohui.groupbuying.UI.fragments.home.CouponFragment;
import com.meiduohui.groupbuying.UI.fragments.home.HomeFragment;
import com.meiduohui.groupbuying.UI.fragments.home.MakeMoneyFragment;
import com.meiduohui.groupbuying.UI.fragments.home.MineFragment;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.RedPacketBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomepageActivity extends AppCompatActivity {


    private String TAG = "MineFragment: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.ll_homepage)
    LinearLayout ll_homepage;
    @BindView(R.id.ll_coupon)
    LinearLayout ll_coupon;
    @BindView(R.id.ll_publish)
    LinearLayout mLlPublish;
    @BindView(R.id.ll_make_money)
    LinearLayout ll_make_money;
    @BindView(R.id.ll_mine)
    LinearLayout ll_mine;
    @BindView(R.id.ll_publish_content)
    LinearLayout mLlPublishContent;
    @BindView(R.id.rv_red_packet)
    RelativeLayout mRvRedPacket;
    @BindView(R.id.rv_red_packet2)
    RelativeLayout mRvRedPacket2;
    @BindView(R.id.civ_shop_img)
    CircleImageView mCivShopImg;
    @BindView(R.id.shop_name)
    TextView mShopName;
    @BindView(R.id.tv_price)
    TextView mTvPrice;
    @BindView(R.id.tv_get_money)
    TextView mTvGetMoney;
    @BindView(R.id.iv_open_red)
    ImageView mIvOpenRed;


    //    @BindView(R.id.v_blur)
    //    View mVBlur;
    //    @BindView(R.id.blurring_view)
    //    BlurringView mBlurringView;

    private RedPacketBean mRedPacketBean;
    private String mMoney;

    private HomeFragment mHomeFragment;
    private MakeMoneyFragment mMakeMoneyFragment;
    private CouponFragment mCouponFragment;
    private MineFragment mMineFragment;
    private List<Fragment> mFragments;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILE = 202;
    private static final int JUMP_TO_SHOP = 500;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:
                    LogUtils.i(TAG + "redInfo getSy_number " + mRedPacketBean.getSy_number());
                    if (!mRedPacketBean.getSy_number().equals("0")) {
                        mIvOpenRed.setVisibility(View.VISIBLE);
                    }
                    break;

                case LOAD_DATA1_FAILE:
                    ToastUtil.show(mContext, (String) msg.obj);
                    break;

                case LOAD_DATA2_SUCCESS:
                    setContent2();
                    break;

                case LOAD_DATA2_FAILE:
                    ToastUtil.show(mContext, (String) msg.obj);
                    break;

                case JUMP_TO_SHOP:

                    Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                    intent.putExtra("Order_id", GlobalParameterApplication.jumpShopId);
                    startActivity(intent);
                    break;

                case NET_ERROR:

                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        ButterKnife.bind(this);
        // 状态栏上浮
        StatusBarUtil.setTranslucentForImageView(this, 0, findViewById(R.id.needOffsetView));
        // 毛玻璃
        //        mBlurringView.setBlurredView(mVBlur);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (GlobalParameterApplication.isNeedRefresh) {
            GlobalParameterApplication.isNeedRefresh = false;
            refreshDate();
        }

        if (GlobalParameterApplication.isShareSussess) {
            GlobalParameterApplication.isShareSussess = false;
            getRed();
        }

        if (GlobalParameterApplication.isNeedJump) {
            GlobalParameterApplication.isNeedJump = false;
            mHandler.sendEmptyMessageDelayed(JUMP_TO_SHOP,1500);
        }

    }

    private void init() {
        initDate();
        initView();
        initFrament();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                redInfo();
            }
        }, 2000);
    }

    private void initDate() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();
    }

    private void initView() {

        mRvRedPacket.setOnClickListener(null);
        mRvRedPacket2.setOnClickListener(null);

        mCurrentTabItemId = ll_homepage.getId();
        ll_homepage.setSelected(true);//默认选中

        if (!GlobalParameterApplication.getInstance().getUserIsShop()) {
            mLlPublish.setVisibility(View.GONE);
        }

    }

    private void initFrament() {
        mHomeFragment = new HomeFragment();
        mCouponFragment = new CouponFragment();
        mMakeMoneyFragment = new MakeMoneyFragment();
        mMineFragment = new MineFragment();

        mFragments = new ArrayList<>();
        mCurrentFragment = mHomeFragment;
        changeFragment(mHomeFragment);//默认加载
    }

    private int mCurrentTabItemId;  //当前选中按钮的id
    private Fragment mCurrentFragment;  //当前选中按钮的id


    @OnClick({R.id.ll_homepage, R.id.ll_coupon, R.id.ll_make_money, R.id.ll_mine})
    public void onClick(View view) {

        if (mCurrentTabItemId == view.getId()) {
            return;
        }

        switch (view.getId()) {
            case R.id.ll_homepage:
                changeFragment(mHomeFragment);
                break;
            case R.id.ll_coupon:

                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(HomepageActivity.this, LoginActivity.class));
                    return;
                } else {
                    changeFragment(mCouponFragment);
                }
                break;
            case R.id.ll_make_money:

                UserBean userBean = GlobalParameterApplication.getInstance().getUserInfo();

                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(HomepageActivity.this, LoginActivity.class));
                    return;
                } else if (userBean != null && TextUtils.isEmpty(userBean.getMobile())) {
                    ToastUtil.show(this, "绑定手机可参加赚钱活动");
                    startActivity(new Intent(this, BindMobileActivity.class));
                    return;
                } else {
                    changeFragment(mMakeMoneyFragment);
                }
                break;

            case R.id.ll_mine:

                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(HomepageActivity.this, LoginActivity.class));
                    return;
                } else {
                    changeFragment(mMineFragment);
                }
                break;
        }
        changeTabItemStyle(view);
    }

    // 切换Fragment
    public void changeFragment(Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();

        if (!mFragments.contains(fragment)) {
            mFragments.add(fragment);
            manager.beginTransaction()
                    .add(R.id.content_fl, fragment)
                    .commit();
        }

        manager.beginTransaction()
                .hide(mCurrentFragment)
                .show(fragment)
                .commit();

        mCurrentFragment = fragment;
    }

    // 改变Fragment(切换会闪烁)
    public void replaceContentPage(Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.content_fl, fragment)
                .addToBackStack(null)
                .commit();
    }

    // 根据是否被选中改变底部选项栏状态
    private void changeTabItemStyle(View view) {

        mCurrentTabItemId = view.getId();

        if (mCurrentTabItemId != ll_homepage.getId()) {
            mIvOpenRed.setVisibility(View.GONE);
        }else if (mRedPacketBean != null && !mRedPacketBean.getSy_number().equals("0")) {
            mIvOpenRed.setVisibility(View.VISIBLE);
        }

        ll_homepage.setSelected(view.getId() == R.id.ll_homepage);
        ll_coupon.setSelected(view.getId() == R.id.ll_coupon);
        ll_make_money.setSelected(view.getId() == R.id.ll_make_money);
        ll_mine.setSelected(view.getId() == R.id.ll_mine);
    }

    // 刷新主页
    private void refreshDate() {
        finish();
        startActivity(getIntent());
    }

    // 去优惠券
    public void goToCoupon() {
        mCurrentTabItemId = ll_coupon.getId();
        changeFragment(mCouponFragment);
        changeTabItemStyle(ll_coupon);
    }


    private boolean IsShowPublish;

    @OnClick({R.id.iv_close, R.id.iv_share, R.id.iv_close2, R.id.iv_look, R.id.iv_open_red,
            R.id.ll_publish, R.id.ll_publish_content, R.id.ll_taocan, R.id.ll_tongyong, R.id.ll_hongbao})
    public void onPublishClik(View view) {
        switch (view.getId()) {

            case R.id.iv_close:
                mRvRedPacket.setVisibility(View.GONE);
                break;

            case R.id.iv_share:
                LogUtils.i(TAG + " onPublishClik 分享 ");
                mRvRedPacket.setVisibility(View.GONE);
                GlobalParameterApplication.shareIntention = CommonParameters.SHARE_SHOPS;
                WxShareUtils.shareWeb(this, CommonParameters.SHARE_JUMP + CommonParameters.APP_INDICATE + "44",
                        " 分享 ", " 赚钱 ", null);
                break;

            case R.id.iv_close2:
                mRvRedPacket2.setVisibility(View.GONE);
                break;

            case R.id.iv_look:
                startActivity(new Intent(this, MyWalletActivity.class));
                break;

            case R.id.iv_open_red:

                readRedPacket();
                break;

            case R.id.ll_publish:
                IsShowPublish = true;
                mLlPublishContent.setVisibility(View.VISIBLE);
                break;

            case R.id.ll_publish_content:
                IsShowPublish = false;
                mLlPublishContent.setVisibility(View.GONE);
                break;

            case R.id.ll_taocan:
                startActivity(new Intent(this, ComboActivity.class));
                IsShowPublish = false;
                mLlPublishContent.setVisibility(View.GONE);
                break;

            case R.id.ll_tongyong:
                startActivity(new Intent(this, GeneralQuanActivity.class));
                IsShowPublish = false;
                mLlPublishContent.setVisibility(View.GONE);
                break;

            case R.id.ll_hongbao:
                startActivity(new Intent(this, RedPacketActivity.class));
                IsShowPublish = false;
                mLlPublishContent.setVisibility(View.GONE);
                break;

        }
    }

    private boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (IsShowPublish) {
                IsShowPublish = false;
                mLlPublishContent.setVisibility(View.GONE);
                return false;
            }

            if (!isExit) {
                isExit = true;
                ToastUtil.show(this, "再次点击返回按钮退出");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    private void setContent() {

        mRvRedPacket.setVisibility(View.VISIBLE);

        Glide.with(mContext)
                .load(mRedPacketBean.getShop_img())
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mCivShopImg);

        mShopName.setText(mRedPacketBean.getShop_name());
        mTvPrice.setText(mRedPacketBean.getMax());

    }

    private void setContent2() {

        mRvRedPacket2.setVisibility(View.VISIBLE);
        mTvGetMoney.setText(mMoney);
    }

    public void readRedPacket() {

        if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
            startActivity(new Intent(HomepageActivity.this, LoginActivity.class));
        } else {
            mIvOpenRed.setVisibility(View.GONE);
            setContent();
        }
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

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "redInfo getShop_name " + mRedPacketBean.getShop_name());

                        } else {
                            mHandler.obtainMessage(LOAD_DATA1_FAILE, msg).sendToTarget();
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

                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);
                             LogUtils.i(TAG + "getRed mMoney " + mMoney);

                        } else {
                            mHandler.obtainMessage(LOAD_DATA2_FAILE, msg).sendToTarget();
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
