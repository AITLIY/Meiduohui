package com.meiduohui.groupbuying.UI.fragments.home;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.mine.wallet.InviteMemListActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.wallet.InviteShopListActivity;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.InviteInfoBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.DonwloadSaveImg;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;
import com.meiduohui.groupbuying.utils.WxShareUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MakeMoneyFragment extends Fragment {

    private String TAG = "MineFragment: ";
    private View mView;
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;
    private Unbinder unbinder;

    @BindView(R.id.tv_invite_money)
    TextView mTvInviteMoney;
    @BindView(R.id.tv_invite_shop)
    TextView mTvInviteShop;
    @BindView(R.id.tv_invite_mem)
    TextView mTvInviteMem;
    @BindView(R.id.tv_content1)
    TextView mTvContent1;
    @BindView(R.id.tv_content2)
    TextView mTvContent2;

    private InviteInfoBean mInviteInfoBean = new InviteInfoBean();

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

                    setResultData();

                    break;

                case LOAD_DATA1_FAILE:

                    ToastUtil.show(mContext, (String) msg.obj);
                    break;

                case NET_ERROR:

                    ToastUtil.show(mContext, "网络异常,请稍后重试");
                    break;
            }

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_make_money, container, false);
        unbinder = ButterKnife.bind(this, mView);

        init();
        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void init() {

        initData();
    }

    private void initData() {
        mContext = getContext();
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        if (mUserBean != null)
            getInviteInfo();
    }

    private void setResultData() {
        mTvInviteMoney.setText(mInviteInfoBean.getInvite_money());
        mTvInviteShop.setText(mInviteInfoBean.getInvite_shop() + "");
        mTvInviteMem.setText(mInviteInfoBean.getInvite_mem() + "");
        mTvContent1.setText(mInviteInfoBean.getContent1());
        mTvContent2.setText(mInviteInfoBean.getContent2());
    }


    @OnClick({R.id.ll_invite_shop, R.id.ll_invite_mem, R.id.tv_take_part_in})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ll_invite_shop:

                startActivity(new Intent(mContext, InviteShopListActivity.class));
                break;

            case R.id.ll_invite_mem:

                startActivity(new Intent(mContext, InviteMemListActivity.class));
                break;

            case R.id.tv_take_part_in:

                //      generateQrCode();
                LoadQrCode();
                break;
        }
    }

    private static final int GET_WRITE_EXTERNAL_STORAGE = 2000;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case GET_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    DonwloadSaveImg.donwloadImg(mContext, mInviteInfoBean.getQrcode());
                } else {

                    ToastUtil.show(mContext, "您已取消授权，设置失败");
                }
                break;
        }
    }

    private PopupWindow popupWindow;

    public void LoadQrCode() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_invite_qr, null);

        ImageView mImg = view.findViewById(R.id.iv_qr_code);
        TextView mSave = view.findViewById(R.id.tv_save_msg);
        TextView mShare = view.findViewById(R.id.tv_share);
        ImageView mClose = view.findViewById(R.id.iv_close);

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
        wl.alpha = 0.5f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        getActivity().getWindow().setAttributes(wl);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                WindowManager.LayoutParams wl = getActivity().getWindow().getAttributes();
                wl.alpha = 1f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
                getActivity().getWindow().setAttributes(wl);
            }
        });
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        Glide.with(mContext)
                .load(mInviteInfoBean.getQrcode())
                .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                .into(mImg);

        // 保存
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GET_WRITE_EXTERNAL_STORAGE);
                } else {
                    DonwloadSaveImg.donwloadImg(mContext, mInviteInfoBean.getQrcode());
                }
                popupWindow.dismiss();
            }
        });

        // 分享
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GlobalParameterApplication.shareIntention = CommonParameters.MAKE_MONEY;
                WxShareUtils.shareWeb(mContext, mInviteInfoBean.getQrcode(), "美多惠赚钱攻略", " 使用美多惠APP首页扫码注册账号即可帮我赚取现金，分享邀请好友你也可以领现金红包哟~", null);
                popupWindow.dismiss();
            }
        });

        // 关闭
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow.dismiss();
            }
        });

    }


    /**
     * 生成二维码
     */
    private void generateQrCode() {
        if (mInviteInfoBean == null) {
            ToastUtil.show(mContext, "操作失败");
            return;
        }
        //        String date = CommonParameters.DOWNLOAD_URL + "_" + mUserBean.getMobile() + "_" + "1";
        //        mRvInvite.setVisibility(View.VISIBLE);
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

    // 邀请信息
    private void getInviteInfo() {

        String url = HttpURL.BASE_URL + HttpURL.MEM_INVITEINFO;
        LogUtils.i(TAG + "getInviteInfo url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "getInviteInfo result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "getInviteInfo msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {

                            String data = jsonResult.getString("data");
                            mInviteInfoBean = new Gson().fromJson(data, InviteInfoBean.class);

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            LogUtils.i(TAG + "getInviteInfo mInviteInfoBean " + mInviteInfoBean.getQrcode());

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
                LogUtils.e(TAG + "getInviteInfo volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_INVITEINFO + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getInviteInfo token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getInviteInfo json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
