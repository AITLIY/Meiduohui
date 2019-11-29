package com.meiduohui.groupbuying.UI.activitys.main;

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
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.jaeger.library.StatusBarUtil;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.coupons.MessageDetailsActivity;
import com.meiduohui.groupbuying.UI.activitys.login.BindMobileActivity;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
import com.meiduohui.groupbuying.UI.activitys.login.RegisterActivity;
import com.meiduohui.groupbuying.UI.activitys.publish.ComboActivity;
import com.meiduohui.groupbuying.UI.activitys.publish.GeneralQuanActivity;
import com.meiduohui.groupbuying.UI.activitys.publish.RedPacketActivity;
import com.meiduohui.groupbuying.UI.fragments.home.CouponFragment;
import com.meiduohui.groupbuying.UI.fragments.home.HomeFragment;
import com.meiduohui.groupbuying.UI.fragments.home.MakeMoneyFragment;
import com.meiduohui.groupbuying.UI.fragments.home.MineFragment;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomepageActivity extends AppCompatActivity {


    private String TAG = "HomepageActivity: ";
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

    //    @BindView(R.id.v_blur)
    //    View mVBlur;
    //    @BindView(R.id.blurring_view)
    //    BlurringView mBlurringView;

    private HomeFragment mHomeFragment;
    private MakeMoneyFragment mMakeMoneyFragment;
    private CouponFragment mCouponFragment;
    private MineFragment mMineFragment;
    private List<Fragment> mFragments;

    private static final int JUMP_TO_SHOP = 500;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case JUMP_TO_SHOP:

                    Intent intent = new Intent(mContext, MessageDetailsActivity.class);

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
        if (GlobalParameterApplication.isNeedJump) {
            GlobalParameterApplication.isNeedJump = false;
            jump();
            //            mHandler.sendEmptyMessageDelayed(JUMP_TO_SHOP,1500);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (GlobalParameterApplication.isNeedJump) {
            GlobalParameterApplication.isNeedJump = false;
            jump();
        }
    }

    private void jump() {

        Intent intent = getIntent();

        if (intent != null) {

            String url = intent.getStringExtra("share_url");
            LogUtils.i(TAG + " share_jump share_url " + url);

            String contents[] = url.split("_");

            if (contents.length < 3) {
                ToastUtil.show(mContext, "数据错误");
                return;
            }
            LogUtils.i("share_jump contents[1] " + contents[1]);
            LogUtils.i("share_jump contents[2] " + contents[2]);

            Intent mIntent;

            switch (contents[2]) {

                case CommonParameters.TYPE_SHARE:

                    if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                        mIntent = new Intent(mContext, RegisterActivity.class);
                        mIntent.putExtra("mobile", contents[1]);
                        startActivity(mIntent);
                    }
                    break;

                case CommonParameters.TYPE_SHOP:
                    mIntent = new Intent(mContext, MessageDetailsActivity.class);
                    mIntent.putExtra("Order_id", contents[1]);
                    startActivity(mIntent);
                    break;

                case CommonParameters.TYPE_HOME:

                    break;
            }

        }
    }

    private void init() {
        initDate();
        initView();
        initFrament();
    }

    private void initDate() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();
        GlobalParameterApplication.mHomeActivity = this;
    }

    private void initView() {

        mCurrentTabItemId = ll_homepage.getId();
        ll_homepage.setSelected(true);//默认选中

        if (GlobalParameterApplication.getInstance().getUserIsShop()) {
            mLlPublish.setVisibility(View.VISIBLE);
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
        ll_homepage.setSelected(view.getId() == R.id.ll_homepage);
        ll_coupon.setSelected(view.getId() == R.id.ll_coupon);
        ll_make_money.setSelected(view.getId() == R.id.ll_make_money);
        ll_mine.setSelected(view.getId() == R.id.ll_mine);
    }

    // 刷新主页
    private void refreshDate() {
        // way1
//        finish();
//        startActivity(getIntent());
        // way2
//        onCreate(null);
    }

    // 去优惠券
    public void goToCoupon() {
        mCurrentTabItemId = ll_coupon.getId();
        changeFragment(mCouponFragment);
        changeTabItemStyle(ll_coupon);
    }


    private boolean IsShowPublish;

    @OnClick({R.id.ll_publish, R.id.ll_publish_content, R.id.ll_taocan, R.id.ll_tongyong, R.id.ll_hongbao})
    public void onPublishClik(View view) {
        switch (view.getId()) {

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

}
