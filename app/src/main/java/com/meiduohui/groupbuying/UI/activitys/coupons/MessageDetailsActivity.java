package com.meiduohui.groupbuying.UI.activitys.coupons;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.login.LoginActivity;
import com.meiduohui.groupbuying.UI.activitys.main.PlusImageActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.wallet.MyWalletActivity;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.UI.views.GlideImageLoader;
import com.meiduohui.groupbuying.UI.views.MyRecyclerView;
import com.meiduohui.groupbuying.adapter.CommentListAdapter;
import com.meiduohui.groupbuying.adapter.GeneralCouponListAdapter;
import com.meiduohui.groupbuying.adapter.MoreMsgListAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.CommentBean;
import com.meiduohui.groupbuying.bean.MessageInfoBean;
import com.meiduohui.groupbuying.bean.RedPacketBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.MapUtils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtils;
import com.meiduohui.groupbuying.utils.UnicodeUtils;
import com.meiduohui.groupbuying.utils.WxShareUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

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

public class MessageDetailsActivity extends AppCompatActivity {

    private String TAG = "MessageDetailsActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.iv_img_play)
    ImageView mIvImgPlay;
    @BindView(R.id.iv_img)
    ImageView mIvImg;
    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_m_price)
    TextView mTvMPrice;
    @BindView(R.id.tv_intro)
    TextView mTvIntroe;
    @BindView(R.id.tv_m_old_price)
    TextView mTvMOldPrice;
    @BindView(R.id.tv_shop_name)
    TextView mTvShopName;
    @BindView(R.id.tv_shop_collect)
    TextView mTvShopCollect;
    @BindView(R.id.tv_shop_cancel_collect)
    TextView mTvShopCancelCollect;
    @BindView(R.id.tv_distance)
    TextView mTvDistance;
    @BindView(R.id.tv_shop_intro)
    TextView mTvShopIntro;
    @BindView(R.id.tv_address)
    TextView mTvAddress;
    @BindView(R.id.tv_sjh)
    TextView mTvSjh;
    @BindView(R.id.tv_q_title)
    TextView mTvQTitle;
    @BindView(R.id.tv_have_quan)
    TextView mTvHaveQuan;
    @BindView(R.id.tv_have_quaned)
    TextView mTvHaveQuaned;
    @BindView(R.id.tv_use_time)
    TextView mTvUseTime;
    @BindView(R.id.tv_beizhu1)
    TextView mTvBeizhu1;
    @BindView(R.id.tv_beizhu2)
    TextView mTvBeizhu2;
    @BindView(R.id.iv_beizhu)
    ImageView iv_beizhu;
    @BindView(R.id.ll_q_title)
    LinearLayout mLlQTitle;
    @BindView(R.id.ll_more_coupon)
    LinearLayout mLlMoreCoupon;
    @BindView(R.id.rv_more_coupon_list)
    MyRecyclerView mRvMoreCouponList;
    @BindView(R.id.more_msg_tv)
    TextView mMoreMsgTv;
    @BindView(R.id.more_msg_v)
    View mMoreMsgV;
    @BindView(R.id.more_msg_rl)
    RelativeLayout mMoreMsgRl;
    @BindView(R.id.comment_tv)
    TextView mCommentTv;
    @BindView(R.id.comment_v)
    View mCommentV;
    @BindView(R.id.comment_rl)
    RelativeLayout mCommentRl;
    @BindView(R.id.rv_more_message_list)
    MyRecyclerView mRvMoreMessageList;
    @BindView(R.id.rv_comment_list)
    MyRecyclerView mRvCommentList;
    @BindView(R.id.PullToRefreshScroll_View)
    PullToRefreshScrollView mPullToRefreshScrollView;                  // 上下拉PullToRefreshScrollView
    @BindView(R.id.tv_go_to_Buy)
    TextView mTvGoToBuy;
    @BindView(R.id.et_comment_content)
    EditText mEtCommentContent;
    @BindView(R.id.ll_comment)
    LinearLayout mLlComment;
    @BindView(R.id.iv_open_red)
    ImageView mIvOpenRed;
    @BindView(R.id.v_bootom_line)
    View v_bootom_line;

    private int mPosition;
    private int mPage1 = 1;
    private int mPage2 = 1;
    private boolean mIsComment = false;
    private boolean mIsPullUp1 = false;
    private boolean mIsPullUp2 = false;
    private boolean mIsGeneral = false;

    private boolean mIsNeedComment;
    private String mOrderId = "";            // 信息id
    private String mShopId = "";            // 信息id
    private RedPacketBean mRedPacketBean;
    private String mMoney;

    private MessageInfoBean.MInfoBean mMInfoBean;
    private GeneralCouponListAdapter mGeneralCouponListAdapter;

    private List<MessageInfoBean.MessageMoreBean> mMessageMoreBeans = new ArrayList<>();
    private List<MessageInfoBean.MessageMoreBean> mShowList1 = new ArrayList<>();
    private MoreMsgListAdapter mMoreMsgListAdapter;

    private List<CommentBean> mCommentBeans = new ArrayList<>();
    private List<CommentBean> mShowList2 = new ArrayList<>();
    private CommentListAdapter mCommentListAdapter;

    private static final int MOVE_TO_COMMENT = 1000;
    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILED = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILED = 202;
    private static final int LOAD_DATA3_SUCCESS = 311;
    private static final int LOAD_DATA3_FAILED = 312;
    private static final int MEM_COLLECT_RESULT_SUCCESS = 301;
    private static final int MEM_COLLECT_RESULT_FAILED = 302;
    private static final int MEM_COLLECTDEL_RESULT_SUCCESS = 401;
    private static final int MEM_COLLECTDEL_RESULT_FAILED = 402;
    private static final int ORDER_GETQUAN_RESULT_SUCCESS = 501;
    private static final int ORDER_GETQUAN_RESULT_FAILED = 502;
    private static final int SHOP_REDINFO_SUCCESS = 601;
    private static final int SHOP_REDINFO_FAILED = 602;
    private static final int SHOP_GETRED_SUCCESS = 701;
    private static final int SHOP_GETRED_FAILED = 702;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case MOVE_TO_COMMENT:

                    LogUtils.i(TAG + "smoothScrollTo mRvCommentList.getTop() " + v_bootom_line.getTop());
                    LogUtils.i(TAG + "smoothScrollTo mRvCommentList.getLeft() " + v_bootom_line.getLeft());
                    LogUtils.i(TAG + "smoothScrollTo mRvCommentList.getRight() " + v_bootom_line.getRight());
                    LogUtils.i(TAG + "smoothScrollTo mRvCommentList.getBottom() " + v_bootom_line.getBottom());
                    mPullToRefreshScrollView.getRefreshableView().smoothScrollTo(0, v_bootom_line.getBottom());
                    break;

                case LOAD_DATA1_SUCCESS:

                    setResultData();
                    if (!mIsPullUp1) {

                        if (mMessageMoreBeans.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "没有发布其他消息~");
                        }
                    }
                    upDataListView1();
                    break;

                case LOAD_DATA1_FAILED:

                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case LOAD_DATA2_SUCCESS:

                    if (!mIsPullUp2) {

                        if (mCommentBeans.size() > 0) {
                            setViewForResult(true, null);

                        } else {
                            setViewForResult(false, "还没有评论~");
                        }
                    }
                    upDataListView2();
                    break;

                case LOAD_DATA2_FAILED:

                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case LOAD_DATA3_SUCCESS:
                    mLoadingDailog.dismiss();
                    getCommentData();      // 添加成功
                    ToastUtils.show(mContext, "评论成功");
                    break;

                case LOAD_DATA3_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case MEM_COLLECT_RESULT_SUCCESS:
                    mLoadingDailog.dismiss();
                    setCollectStatusView(true);
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case MEM_COLLECT_RESULT_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case MEM_COLLECTDEL_RESULT_SUCCESS:
                    mLoadingDailog.dismiss();
                    setCollectStatusView(false);
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case MEM_COLLECTDEL_RESULT_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case ORDER_GETQUAN_RESULT_SUCCESS:
                    mLoadingDailog.dismiss();
                    if (mIsGeneral) {
                        mIsGeneral = false;
                        setHaveQuanView(true);
                    } else {
                        mMInfoBean.getS_quan_info().get(mPosition).setGeted(true);
                        mGeneralCouponListAdapter.notifyDataSetChanged();
                    }
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case ORDER_GETQUAN_RESULT_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case SHOP_REDINFO_SUCCESS:

                    LogUtils.i(TAG + "redInfo getSy_number " + mRedPacketBean.getSy_number());
                    if (!mRedPacketBean.getSy_number().equals("0")) {
                        mIvOpenRed.setVisibility(View.VISIBLE);
                    }
                    break;

                case SHOP_REDINFO_FAILED:

                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case SHOP_GETRED_SUCCESS:
                    showGetRed();
                    break;

                case SHOP_GETRED_FAILED:
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;


                case NET_ERROR:
                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, "网络异常,请稍后重试");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        ButterKnife.bind(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (GlobalParameterApplication.isShareSussess) {
            GlobalParameterApplication.isShareSussess = false;
            addZf(mMInfoBean.getOrder_id());
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    getRed();
                }
            }, 1000);
        }
    }

    private void init() {
        initDailog();
        initData();

        initPullToRefresh();
        initMoreMsgList();
        initCommentList();
//        initCommentEt();
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

        updateData();       // 初始化数据

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (mMInfoBean != null)
                    redInfo();
            }
        }, 500);

    }

    private void updateData() {

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            mOrderId = bundle.getString("Order_id");
            mShopId = bundle.getString("shop_id");
            mIsNeedComment = bundle.getBoolean("onComment", false);
            if (mIsNeedComment) {
                changeTabItemStyle(mCommentRl);
                setCouponListView(false);
            }

            LogUtils.i(TAG + "initData getOrder_id " + mOrderId);
            getShopInfoData();
        }
    }

    @OnClick({R.id.tv_shop_collect, R.id.tv_shop_cancel_collect, R.id.tv_have_quan,
            R.id.iv_go_address, R.id.iv_call_shops,R.id.ll_beizhu,R.id.rl_comment,
            R.id.tv_go_to_Buy,R.id.iv_open_red})
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tv_shop_collect:
                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    mLoadingDailog.show();
                    collectShop();
                }
                break;

            case R.id.tv_shop_cancel_collect:

                mLoadingDailog.show();
                collectShopDel();

            case R.id.tv_have_quan:

                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    mIsGeneral = true;
                    mLoadingDailog.show();
                    getQuan(mMInfoBean.getR_id());
                }
                break;

            case R.id.iv_go_address:
                showMapSelect();
                break;

            case R.id.iv_call_shops:
                showCallSelect();
                break;

            case R.id.ll_beizhu:
                if (isShow) {
                    isShow = false;
                    isShowBeizhu(false);
                } else {
                    isShow = true;
                    isShowBeizhu(true);
                }
                break;

            case R.id.rl_comment:

                String comment = mEtCommentContent.getText().toString().trim();

                LogUtils.i(TAG + "init comment " + comment);

                if (TextUtils.isEmpty(comment)) {
                    ToastUtils.show(mContext,"请输入评价内容");
                    return;
                }

                addCommentData(comment);
                break;

            case R.id.tv_go_to_Buy:

                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    Intent intent = new Intent(this, OrderActivity.class);
                    intent.putExtra("m_id", mOrderId);
                    startActivity(intent);
                }
                break;

            case R.id.iv_open_red:

                if (!GlobalParameterApplication.getInstance().getLoginStatus()) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
//                    mIvOpenRed.setVisibility(View.GONE);
                    showRedInfo();
                }
                break;
        }
    }

    private PopupWindow popupWindow;
    // 导航窗口
    public void showMapSelect() {

        if (mMInfoBean==null)
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

        final Double dlat = Double.parseDouble(mMInfoBean.getWd());
        final Double dlon = Double.parseDouble(mMInfoBean.getJd());
        final String address = mMInfoBean.getAddress();

        // 高德导航
        mGaode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (MapUtils.isGdMapInstalled()) {
                    MapUtils.openGaoDeNavi(mContext, 0, 0, null, dlat, dlon, address);

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

                if (MapUtils.isBaiduMapInstalled()) {
                    MapUtils.openBaiDuNavi(mContext, 0, 0, null, dlat, dlon, address);

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
    // 打电话窗口
    public void showCallSelect() {

        if (mMInfoBean==null)
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

        mCall.setText("拨打：" + mMInfoBean.getSjh());
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

    private PopupWindow popupWindow3;
    // 分享获取红包窗口
    public void showRedInfo() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_read_redpacket, null);

        CircleImageView mImg = view.findViewById(R.id.civ_shop_img);
        TextView mName = view.findViewById(R.id.tv_shop_name);
        TextView mPrice= view.findViewById(R.id.tv_price);
        ImageView mShare = view.findViewById(R.id.iv_share);
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

        Glide.with(mContext)
                .load(mRedPacketBean.getShop_img())
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mImg);
        mName.setText(mRedPacketBean.getShop_name() + "送您红包");
        mPrice.setText(mRedPacketBean.getMax());
        // 分享
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LogUtils.i(TAG + " showRedInfo 分享 ");
                GlobalParameterApplication.shareIntention = CommonParameters.SHARE_MESSAGE;
                showShare();
                popupWindow3.dismiss();
            }
        });

        // 关闭
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow3.dismiss();
            }
        });

    }

    private PopupWindow popupWindow4;
    // 查看红包窗口
    public void showGetRed() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_get_redpacket, null);

        TextView mGetMoney = view.findViewById(R.id.tv_get_money);
        ImageView mLook = view.findViewById(R.id.iv_look);
        ImageView mClose = view.findViewById(R.id.iv_close);

        popupWindow4 = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow4.setFocusable(false);
        popupWindow4.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.alpha = 0.5f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        getWindow().setAttributes(wl);
        popupWindow4.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                WindowManager.LayoutParams wl = getWindow().getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                getWindow().setAttributes(wl);
            }
        });
        popupWindow4.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        mGetMoney.setText(mMoney);

        // 查看
        mLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(mContext, MyWalletActivity.class));
                popupWindow4.dismiss();
            }
        });

        // 关闭
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow4.dismiss();
            }
        });
    }

    private PopupWindow popupWindow5;
    // 分享微信窗口
    public void showShare() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_we_share, null);

        LinearLayout mSession = view.findViewById(R.id.ll_we_Session);
        LinearLayout mTimeline = view.findViewById(R.id.ll_we_Timeline);
        TextView mCancel = view.findViewById(R.id.tv_cancel);

        popupWindow5 = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow5.setFocusable(true);
        popupWindow5.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.alpha = 0.5f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        getWindow().setAttributes(wl);
        popupWindow5.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                WindowManager.LayoutParams wl = getWindow().getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                getWindow().setAttributes(wl);
            }
        });
        popupWindow5.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        // 好友
        mSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(0);
                popupWindow5.dismiss();
            }
        });

        // 朋友圈
        mTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(1);
                popupWindow5.dismiss();
            }
        });

        //取消
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow5.dismiss();
            }
        });

    }

    // 分享到微信
    private void share(final int type) {

        String url = "";
        if (!TextUtils.isEmpty(mMInfoBean.getVideo())){
            url = mMInfoBean.getVideo() + CommonParameters.VIDEO_END;
        } else {
            url = mMInfoBean.getImg().get(0);
        }
        LogUtils.i(TAG + "onZF url " + url);

//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_tab_mei);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_tab_red_packet);

        WxShareUtils.shareWeb(mContext,  CommonParameters.SHARE_JUMP + CommonParameters.APP_INDICATE
                        + "_" + mMInfoBean.getOrder_id() + "_" + CommonParameters.TYPE_SHOP,
                mMInfoBean.getShop_name() + "给您发红包了！", mRedPacketBean.getTitle(), bmp, type);
    }

    private static final int CALL_PHONE = 1000;

    // 打电话
    private void getCall() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mMInfoBean.getSjh())));
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
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mMInfoBean.getSjh())));

                } else {
                    LogUtils.i(TAG + " onRequestPermissionsResult FAILED");
                    ToastUtils.show(mContext, "您已取消授权，无法打电话");
                }
                break;

        }
    }

    @OnClick({R.id.more_msg_rl, R.id.comment_rl})
    public void onOptionClick(View view) {
        switch (view.getId()) {
            case R.id.more_msg_rl:

                setCouponListView(true);
                break;

            case R.id.comment_rl:

                setCouponListView(false);
                break;
        }
        changeTabItemStyle(view);
    }

    // 设置是否是显示更多优惠券
    private void setCouponListView(boolean isShow) {

        if (isShow) {
            mIsComment = false;
            mRvMoreMessageList.setVisibility(View.VISIBLE);
            mRvCommentList.setVisibility(View.GONE);

            mTvGoToBuy.setVisibility(View.VISIBLE);
            mLlComment.setVisibility(View.GONE);

            if (mShowList1.size() > 0)
                setViewForResult(true, null);
            else {
                addtoTop();      // 加载优惠券
            }

        } else {
            mIsComment = true;
            mRvMoreMessageList.setVisibility(View.GONE);
            mRvCommentList.setVisibility(View.VISIBLE);

            mTvGoToBuy.setVisibility(View.GONE);
            mLlComment.setVisibility(View.VISIBLE);

            if (mShowList2.size() > 0)
                setViewForResult(true, null);
            else {
                addtoTop();     // 加载评论
            }

        }
    }

    // 设置标题栏颜色
    private void changeTabItemStyle(View view) {

        mMoreMsgTv.setTextColor(view.getId() == R.id.more_msg_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));
        mCommentTv.setTextColor(view.getId() == R.id.comment_rl ? getResources().getColor(R.color.black) : getResources().getColor(R.color.text_general));

        mMoreMsgV.setVisibility(view.getId() == R.id.more_msg_rl ? View.VISIBLE : View.GONE);
        mCommentV.setVisibility(view.getId() == R.id.comment_rl ? View.VISIBLE : View.GONE);
    }

    private boolean isShow;
    // 是否显示规则说明
    private void isShowBeizhu(boolean isShow) {

        if (isShow){
            iv_beizhu.setImageResource(R.drawable.icon_tab_sort_top);
            mTvBeizhu1.setVisibility(View.GONE);
            mTvBeizhu2.setVisibility(View.VISIBLE);
            isShow = true;
        }else {
            iv_beizhu.setImageResource(R.drawable.icon_tab_sort_bottom);
            mTvBeizhu1.setVisibility(View.VISIBLE);
            mTvBeizhu2.setVisibility(View.GONE);
            isShow = false;
        }
    }

    // 设置结果数据
    private void setResultData() {
        setContentData();
        initCouponList();
    }

    // 设置内容数据
    private void setContentData() {

        String url = mMInfoBean.getVideo();

        if (!TextUtils.isEmpty(url)) {
            setSrcTypeView(true);
            setVideoView(url);
            url = url + CommonParameters.VIDEO_END;
            LogUtils.i(TAG + "setContentData url " + url);
        } else {
            List<String> urls = mMInfoBean.getImg();
            url = urls.get(0);
            LogUtils.i(TAG + "setContentData url " + urls.get(0));
            setSrcTypeView(false);
            initBanner(urls);
        }

        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mIvImg);

        mTvTitle.setText(mMInfoBean.getTitle());
        mTvIntroe.setText(mMInfoBean.getIntro());
        mTvMPrice.setText("¥" + mMInfoBean.getM_price());
        mTvMOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mTvMOldPrice.setText("¥" + mMInfoBean.getM_old_price());

        mTvShopName.setText(mMInfoBean.getShop_name());
        mTvDistance.setText(mMInfoBean.getJuli());
        mTvShopIntro.setText(mMInfoBean.getShop_intro());
        mTvAddress.setText(mMInfoBean.getAddress());
        mTvSjh.setText("电话：" + mMInfoBean.getSjh());
        if (!TextUtils.isEmpty(mMInfoBean.getQ_title())){
            mLlQTitle.setVisibility(View.VISIBLE);
            mTvQTitle.setText(mMInfoBean.getQ_title());
        }

        LogUtils.i(TAG + "setContentData getShop_collect_state " + mMInfoBean.getShop_collect_state());
        setCollectStatusView(mMInfoBean.getShop_collect_state() == 2);
        LogUtils.i(TAG + "setContentData getHave_quan " + mMInfoBean.getHave_quan());
        setHaveQuanView(mMInfoBean.getHave_quan() == 1);

        LogUtils.i(TAG + "setContentData getStart_time " + mMInfoBean.getStart_time()
                + " " + Long.parseLong(mMInfoBean.getStart_time())
                + " " + TimeUtils.LongToString(Long.parseLong(mMInfoBean.getStart_time()), "yyyy.MM.dd"));

        mTvUseTime.setText("有效时间：" + TimeUtils.LongToString(Long.parseLong(mMInfoBean.getStart_time()), "yyyy.MM.dd")
                + " - " + TimeUtils.LongToString(Long.parseLong(mMInfoBean.getEnd_time()), "yyyy.MM.dd"));

        if (!TextUtils.isEmpty(mMInfoBean.getBeizhu())) {
            mTvBeizhu1.setText(mMInfoBean.getBeizhu());
            mTvBeizhu2.setText(mMInfoBean.getBeizhu());
        } else {
            mTvBeizhu1.setText("暂无");
            mTvBeizhu2.setText("暂无");
        }

    }

    // 设置调用系统播放器
    private void setVideoView(final String url) {

        mIvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(url);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
                mediaIntent.setDataAndType(Uri.parse(url), mimeType);
                startActivity(mediaIntent);
            }
        });
    }

    // 初始化轮播图
    private void initBanner(final List<String> urls) {
        List<String> list = new ArrayList<>();

        for (String url : urls) {
            list.add(url);
            LogUtils.i(TAG + "initBanner url " + url);
        }

        mBanner.setImages(list)
                .setImageLoader(new GlideImageLoader())
                .setDelayTime(2000)
                .setIndicatorGravity(BannerConfig.RIGHT)
                .start();
        //banner点击事件 position为当前显示的第几张图,从1开始,不是0
        mBanner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
//                Toast.makeText(MessageDetailsActivity.this, "点击了" + String.valueOf(position) + "个", Toast.LENGTH_SHORT).show();
                viewPluImg(position-1,urls);
            }
        });
    }

    // 查看大图
    private void viewPluImg(int position,List<String> urls) {
        Intent intent = new Intent(mContext, PlusImageActivity.class);
        intent.putStringArrayListExtra(CommonParameters.IMG_LIST, (ArrayList<String>) urls);
        intent.putExtra(CommonParameters.POSITION, position);
        startActivity(intent);
    }

    // 设置是否是视频
    private void setSrcTypeView(boolean isVideo) {

        if (isVideo) {
            mIvImgPlay.setVisibility(View.VISIBLE);
            mBanner.setVisibility(View.GONE);
        } else {
            mIvImgPlay.setVisibility(View.GONE);
            mBanner.setVisibility(View.VISIBLE);
        }
    }

    // 设置是否收藏
    private void setCollectStatusView(boolean isCollect) {

        if (!isCollect) {
            mTvShopCollect.setVisibility(View.VISIBLE);
            mTvShopCancelCollect.setVisibility(View.GONE);
        } else {
            mTvShopCollect.setVisibility(View.GONE);
            mTvShopCancelCollect.setVisibility(View.VISIBLE);
        }
    }

    // 设置是否获取券
    private void setHaveQuanView(boolean isHave) {

        if (!isHave) {
            mTvHaveQuan.setVisibility(View.VISIBLE);
            mTvHaveQuaned.setVisibility(View.GONE);
        } else {
            mTvHaveQuan.setVisibility(View.GONE);
            mTvHaveQuaned.setVisibility(View.VISIBLE);
        }
    }

    // 初始化通用优惠券
    private void initCouponList() {

        if (mMInfoBean.getS_quan_info().size() > 0){
            mLlMoreCoupon.setVisibility(View.VISIBLE);
        } else {
            return;
        }

        mGeneralCouponListAdapter = new GeneralCouponListAdapter(mContext, mMInfoBean.getS_quan_info());
        mGeneralCouponListAdapter.setOnItemClickListener(new GeneralCouponListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mPosition = position;
                getQuan(mMInfoBean.getS_quan_info().get(position).getR_id());
                LogUtils.i(TAG + "initCouponList onItemClick position " + position);
            }
        });
        mRvMoreCouponList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMoreCouponList.setAdapter(mGeneralCouponListAdapter);
    }

    // 初始化更多消息
    private void initMoreMsgList() {

        mMoreMsgListAdapter = new MoreMsgListAdapter(mContext, mShowList1);
        mMoreMsgListAdapter.setOnItemClickListener(new MoreMsgListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Intent intent = new Intent(mContext, MessageDetailsActivity.class);
                intent.putExtra("Order_id", mShowList1.get(position).getOrder_id());
                startActivity(intent);
                LogUtils.i(TAG + "initMoreMsgList onItemClick position " + position);
            }
        });
        mRvMoreMessageList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvMoreMessageList.setAdapter(mMoreMsgListAdapter);
    }

    // 初始化评论输入
    private void initCommentEt() {

        mEtCommentContent.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mEtCommentContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    String comment = mEtCommentContent.getText().toString().trim();

                    LogUtils.i(TAG + "init comment " + comment);

                    if (TextUtils.isEmpty(comment)) {
                        return false;
                    }

                    addCommentData(comment);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }

                    return true;
                }
                return false;
            }
        });

    }

    // 初始化评论列表
    private void initCommentList() {

        mCommentListAdapter = new CommentListAdapter(mContext, mShowList2);
        mCommentListAdapter.setOnItemClickListener(new CommentListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        mRvCommentList.setLayoutManager(new LinearLayoutManager(mContext));
        mRvCommentList.setAdapter(mCommentListAdapter);
    }

    // 初始化列表
    private void initPullToRefresh() {

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
                addtoBottom();      //  请求网络数据
                refreshComplete();  // 数据加载完成后，关闭header,footer
            }
        });

    }

    // 下拉刷新的方法:
    public void addtoTop() {

        if (!mIsComment) {
            mPage1 = 1;
            mIsPullUp1 = false;

            getShopInfoData();       // 下拉刷新
        } else{
            mPage2 = 1;
            mIsPullUp2 = false;

            getCommentData();     // 下拉刷新；
        }
    }

    // 上拉加载的方法:
    public void addtoBottom() {

        if (!mIsComment) {
            mPage1++;
            mIsPullUp1 = true;
            getShopInfoData();      // 加载更多
        } else{
            mPage2++;
            mIsPullUp2 = true;
            getCommentData();     // 加载更多；
        }
    }

    // 刷新完成时关闭
    public void refreshComplete() {
        mHandler.postDelayed(new Runnable() {
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
    private void upDataListView1() {

        if (!mIsPullUp1) {

            mShowList1.clear();
            mShowList1.addAll(mMessageMoreBeans);
            mMoreMsgListAdapter.notifyDataSetChanged();

        } else {

            mShowList1.addAll(mMessageMoreBeans);
            mMoreMsgListAdapter.notifyDataSetChanged();
            if (mMessageMoreBeans.size() == 0) {
                //                ToastUtil.show(mContext, "没有更多结果");
            }
        }
    }

    // 更新列表数据
    private void upDataListView2() {

        if (!mIsPullUp2) {

            mShowList2.clear();
            mShowList2.addAll(mCommentBeans);
            mCommentListAdapter.notifyDataSetChanged();

        } else {

            mShowList2.addAll(mCommentBeans);
            mCommentListAdapter.notifyDataSetChanged();
            if (mCommentBeans.size() == 0) {
                //                ToastUtil.show(mContext, "没有更多结果");
            }
        }

        if (mIsNeedComment) {
            mIsNeedComment = false;
            mHandler.sendEmptyMessageDelayed(MOVE_TO_COMMENT,500);
        }

    }



    //--------------------------------------请求服务器数据--------------------------------------------

    // 获取商铺发布信息
    private void getShopInfoData() {

        String url = HttpURL.BASE_URL + HttpURL.SHOP_SHOPINFO;
        LogUtils.i(TAG + "getShopInfoData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getShopInfoData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getShopInfoData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            MessageInfoBean messageInfoBean = new Gson().fromJson(data, MessageInfoBean.class);

                            mMInfoBean = messageInfoBean.getM_info();
                            mOrderId = mMInfoBean.getOrder_id();
                            mMessageMoreBeans = messageInfoBean.getMessage_more();

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getShopInfoData mMessageMoreBeans.size " + mMessageMoreBeans.size());
                            return;
                        }

                        mHandler.obtainMessage(LOAD_DATA1_FAILED, msg).sendToTarget();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getShopInfoData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SHOP_SHOPINFO + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getShopInfoData token " + token);
                String md5_token = MD5Utils.md5(token);

                if (!TextUtils.isEmpty(mOrderId))
                    map.put("m_id", mOrderId);
                if (!TextUtils.isEmpty(mShopId))
                    map.put("shop_id", mShopId);
                if (GlobalParameterApplication.mLocation!=null) {
                    map.put("lat", GlobalParameterApplication.mLocation.getLatitude()+"");
                    map.put("lon", GlobalParameterApplication.mLocation.getLongitude()+"");
                }

                if (mUserBean != null)
                    map.put("mem_id", mUserBean.getId());
                map.put("page", mPage1 + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getShopInfoData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 收藏商户
    private void collectShop() {

        if (mUserBean == null) {
            ToastUtils.show(mContext, "您还未登录");
            return;
        }

        mLoadingDailog.show();

        String url = HttpURL.BASE_URL + HttpURL.MEM_COLLECT;
        LogUtils.i(TAG + "collectShop url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "collectShop result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "collectShop msg " + msg);
                        String status = jsonResult.getString("status");

                        LogUtils.i(TAG + "collectShop status " + status + " msg " + msg);

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(MEM_COLLECT_RESULT_SUCCESS, msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(MEM_COLLECT_RESULT_SUCCESS, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "collectShop volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_COLLECT + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "collectShop token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("shop_id", mMInfoBean.getShop_id());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "collectShop json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 取消收藏商户
    private void collectShopDel() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_COLLECTDEL;
        LogUtils.i(TAG + "collectShopDel url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "collectShopDel result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "collectShopDel msg " + msg);
                        String status = jsonResult.getString("status");

                        LogUtils.i(TAG + "collectShopDel status " + status + " msg " + msg);

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(MEM_COLLECTDEL_RESULT_SUCCESS, msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(MEM_COLLECTDEL_RESULT_FAILED, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "collectShopDel volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_COLLECTDEL + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "collectShopDel token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("id", mMInfoBean.getShop_collect_id() + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "collectShopDel json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 获取优惠券
    private void getQuan(final String id) {

        if (mUserBean == null) {
            ToastUtils.show(mContext, "您还未登录");
            return;
        }

        mLoadingDailog.show();

        String url = HttpURL.BASE_URL + HttpURL.ORDER_GETQUANL;
        LogUtils.i(TAG + "getQuan url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getQuan result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getQuan msg " + msg);
                        String status = jsonResult.getString("status");

                        LogUtils.i(TAG + "getQuan status " + status + " msg " + msg);

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(ORDER_GETQUAN_RESULT_SUCCESS, msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(ORDER_GETQUAN_RESULT_FAILED, msg).sendToTarget();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getQuan volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_GETQUANL + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getQuan token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("r_id", id);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getQuan json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 获取评论列表
    private void getCommentData() {

        String url = HttpURL.BASE_URL + HttpURL.COMMENT_COMMENTLIST;
        LogUtils.i(TAG + "getCommentData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getCommentData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getCommentData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");

                            mCommentBeans = new Gson().fromJson(data, new TypeToken<List<CommentBean>>() {
                            }.getType());

                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);
                            LogUtils.i(TAG + "getCommentData mCommentBeans.size " + mCommentBeans.size());

                            return;
                        }
                        mHandler.obtainMessage(LOAD_DATA2_FAILED, msg).sendToTarget();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "getCommentData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.COMMENT_COMMENTLIST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getCommentData token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("m_id", mOrderId);
                map.put("page", mPage2 + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getCommentData json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 添加评论
    private void addCommentData(final String comment) {

        if (mUserBean == null) {
            ToastUtils.show(mContext, "您还未登录");
            return;
        }

        mLoadingDailog.show();

        String url = HttpURL.BASE_URL + HttpURL.COMMENT_ADDCOMMENT;
        LogUtils.i(TAG + "addCommentData url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "addCommentData result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "addCommentData msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {
                            mHandler.sendEmptyMessage(LOAD_DATA3_SUCCESS);
                            return;
                        }
                        mHandler.obtainMessage(LOAD_DATA3_FAILED, msg).sendToTarget();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "addCommentData volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.COMMENT_ADDCOMMENT + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "addCommentData token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("content", comment);
                map.put("m_id", mOrderId);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "addCommentData json " + map.toString());
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

                map.put("mem_id", mUserBean.getShop_id());
                map.put("m_id", id);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "addZf json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
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
                            if (mRedPacketBean!=null) {
                                mHandler.sendEmptyMessage(SHOP_REDINFO_SUCCESS);
                                LogUtils.i(TAG + "redInfo getShop_name " + mRedPacketBean.getShop_name());
                            }
                        } else {
                            mHandler.obtainMessage(SHOP_REDINFO_FAILED, msg).sendToTarget();
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

                map.put("shop_id", mMInfoBean.getShop_id());

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

                            mHandler.sendEmptyMessage(SHOP_GETRED_SUCCESS);
                            LogUtils.i(TAG + "getRed mMoney " + mMoney);

                        } else {
                            mHandler.obtainMessage(SHOP_GETRED_FAILED, msg).sendToTarget();
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
