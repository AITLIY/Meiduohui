package com.meiduohui.groupbuying.UI.activitys.mine.setting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.UrlArrayBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.bean.UserInfoBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.MultiPartStack;
import com.meiduohui.groupbuying.utils.MultiPartStringRequest;
import com.meiduohui.groupbuying.utils.NetworkUtils;
import com.meiduohui.groupbuying.utils.PictureSelectorConfig;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtils;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopInfoActivity extends AppCompatActivity {

    private String TAG = "ShopInfoActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.civ_shop_img)
    CircleImageView mCivShopImg;
    @BindView(R.id.ed_name)
    EditText mEdName;
    @BindView(R.id.ed_sjh)
    EditText mEdSjh;

    private String mImg = "";
    private String mIntro = "";
    private String mCounty = "";
    private String mAddress = "";
    private String mLatitude = "";
    private String mLongitude = "";
    private boolean isChangePhono;
    private boolean isChangeName;
    private boolean isChangeSjh;
    private boolean isChangeIntro;
    private boolean isChangeAddress;
    private UserInfoBean.ShopInfoBean mShopInfoBean;
    private File mImgFile;              // 生成的文件

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILED = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILED = 202;
    private static final int NET_ERROR = 404;
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    mLoadingDailog.dismiss();
                    isChangePhono = true;
                    setCarHeader(mImgFile,mCivShopImg);

                    break;

                case LOAD_DATA1_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, "上传商户头像失败");
                    break;

                case LOAD_DATA2_SUCCESS:
                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, (String) msg.obj);
                    GlobalParameterApplication.getInstance().refeshHomeActivity(ShopInfoActivity.this);
                    break;

                case LOAD_DATA2_FAILED:

                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, (String) msg.obj);
                    break;

                case NET_ERROR:
                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, "网络异常,请稍后再试");
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);
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

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            UserInfoBean userInfoBean = (UserInfoBean) bundle.getSerializable("UserInfoBean");
            mShopInfoBean = userInfoBean.getShop_info();

            if (mShopInfoBean != null)
                setContent();
        }
    }

    private void setContent() {

        LogUtils.i(TAG + "initData mShopInfoBean " + mShopInfoBean.getId());

        mImg = mShopInfoBean.getImg();
        mIntro = mShopInfoBean.getIntro();
        mAddress = mShopInfoBean.getAddress();

        Glide.with(mContext)
                .load(mShopInfoBean.getImg())
                .apply(new RequestOptions().error(R.drawable.icon_tab_usericon))
                .into(mCivShopImg);
        mEdName.setText(mShopInfoBean.getName());
        mEdSjh.setText(mShopInfoBean.getSjh());
    }

    @OnClick({R.id.iv_back, R.id.civ_shop_img, R.id.ll_intro, R.id.ll_address, R.id.tv_commit})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:
                finish();
                break;

            case R.id.civ_shop_img:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    showSettingHeaderPic();
                else {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ShopInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GET_WRITE_EXTERNAL_STORAGE);
                    } else {
                        showSettingHeaderPic();
                    }
                }
                break;

            case R.id.ll_intro:

                Intent intent = new Intent(this, ShopIntroActivity.class);
                intent.putExtra("mIntro", mIntro);
                startActivityForResult(intent, INTRO_REQUEST_CODE);
                break;

            case R.id.ll_address:
                Intent intent2 = new Intent(this, ShopAddressActivity.class);
                intent2.putExtra("mAddress", mAddress);
                startActivityForResult(intent2, ADDRESS_REQUEST_CODE);
                break;

            case R.id.tv_commit:

                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtils.show(mContext,"网络异常,请稍后重试");
                    return;
                }

                String name = mEdName.getText().toString();
                String sjh = mEdSjh.getText().toString();

                if ("".equals(name) ) {
                    ToastUtils.show(mContext, "商户名称不能为空");
                    return;
                } else if ("".equals(sjh) ) {
                    ToastUtils.show(mContext, "联系电话不能为空");
                    return;
                }

                if (!mShopInfoBean.getName().equals(name)) {
                    isChangeName = true;
                }
                if (!mShopInfoBean.getSjh().equals(sjh)) {
                    isChangeSjh = true;
                }
                if (!mShopInfoBean.getIntro().equals(mIntro)) {
                    isChangeIntro = true;
                }
                if (!mShopInfoBean.getAddress().equals(mAddress)) {
                    isChangeAddress = true;
                }

                if (isChangeName || isChangePhono || isChangeSjh || isChangeIntro || isChangeAddress || !TextUtils.isEmpty(mCounty) && !TextUtils.isEmpty(mLongitude) && !TextUtils.isEmpty(mLatitude)){

                    mLoadingDailog.show();
                    changeInfo(name,sjh);
                } else {
                    Log.d(TAG, "tv_commit 未修改");
                    finish();
                }

                break;
        }
    }

    private static final int GET_WRITE_EXTERNAL_STORAGE = 2000;
    private static final int PERMISSION_CAMERA = 1000;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case GET_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showSettingHeaderPic();
                } else {

                    ToastUtils.show(mContext, "您已取消授权，设置失败");
                }
                break;

            case PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    PictureSelectorConfig.initSingleCameraConfig(ShopInfoActivity.this);
                } else {

                    ToastUtils.show(mContext, "您已取消授权，拍照失败");
                }
                break;
        }
    }

    private PopupWindow popupWindow;
    private static final int INTRO_REQUEST_CODE = 3000;
    private static final int ADDRESS_REQUEST_CODE = 4000;

    public void showSettingHeaderPic() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_select_pic, null);

        TextView mCamera = view.findViewById(R.id.tv_camera);
        TextView mAlbum = view.findViewById(R.id.tv_album);
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

        // 拍照
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){

                    PictureSelectorConfig.initSingleCameraConfig(ShopInfoActivity.this);
                } else {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ShopInfoActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                    } else {

                        PictureSelectorConfig.initSingleCameraConfig(ShopInfoActivity.this);
                    }
                }
                popupWindow.dismiss();

            }
        });

        // 选择照片
        mAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PictureSelectorConfig.initSingleConfig(ShopInfoActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case INTRO_REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    mIntro = data.getStringExtra("intro");
                    Log.d(TAG, "onActivityResult: intro " + mIntro);
                }
                break;

            case ADDRESS_REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    mCounty = data.getStringExtra("county");
                    mAddress = data.getStringExtra("address");
                    mLatitude = data.getStringExtra("Latitude");
                    mLongitude = data.getStringExtra("Longitude");
                    Log.d(TAG, "onActivityResult: mCounty " + mCounty
                            + " mAddress " + mAddress
                            + " mLatitude " + mLatitude
                            + " mLongitude " + mLongitude);
                }
                break;

            case  PictureConfig.CHOOSE_REQUEST:// 图片选择结果回调

                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);

                if (images.size() > 0) {

                    LogUtils.i(TAG + "onActivityResult getPath " + images.get(0).getPath());
                    LogUtils.i(TAG + "onActivityResult getCutPath " + images.get(0).getCutPath());
                    LogUtils.i(TAG + "onActivityResult getCompressPath " + images.get(0).getCompressPath());

                    mImgFile = new File(images.get(0).getCompressPath());

                    mLoadingDailog.show();
                    uploadFile(mImgFile);
                }

                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                break;
        }
    }

    // 设置头像
    public void setCarHeader(File file, ImageView imageView) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int height = options.outHeight;
        int width = options.outWidth;
        double scale;
        if (width > height) {
            scale = Math.floor(width / 96);
        } else {
            scale = Math.floor(height / 96);
        }
        options.inSampleSize = (int) scale;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        imageView.setImageBitmap(bitmap);
    }


    //--------------------------------------请求服务器数据-------------------------------------------

    // 上传头像
    private void uploadFile(final File file) {
        RequestQueue queue = Volley.newRequestQueue(this, new MultiPartStack());
        final String url = HttpURL.BASE_URL + HttpURL.UPLOAD_UPLOAD;
        LogUtils.i(TAG + "uploadFile url " + url);
        MultiPartStringRequest stringRequest = new MultiPartStringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "uploadFile result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "uploadFile msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {
                            String data = jsonResult.getString("data");
                            UrlArrayBean mUrlArrayBeans = new Gson().fromJson(data, UrlArrayBean.class);
                            List<String> urls = mUrlArrayBeans.getUrl();
                            mImg = urls.get(0);
                            LogUtils.i(TAG + "uploadFile mImg " + mImg);
                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            return;
                        }

                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILED);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILED);
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "uploadFile volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {

            @Override
            public Map<String, File> getFileUploads() {
                Map<String, File> files = new HashMap<String, File>();

                LogUtils.d(TAG + "uploadFile getFileUploads " + file.getAbsolutePath());
                files.put("file", file);

                return files;
            }

            @Override
            public Map<String, String> getStringUploads() {

                Map<String, String> map = new HashMap<String, String>();
                String token = HttpURL.UPLOAD_UPLOAD + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "uploadFile token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "uploadFile json " + map.toString());
                return map;
            }

        };
        queue.add(stringRequest);
    }

    // 修改资料
    private void changeInfo(final String name,final String sjh) {

        final String url = HttpURL.BASE_URL + HttpURL.SET_CHANGEINFO;
        LogUtils.i(TAG + "changeInfo url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "changeInfo result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "changeInfo msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {
                            mHandler.obtainMessage(LOAD_DATA2_SUCCESS, msg).sendToTarget();
                        } else {
                            mHandler.obtainMessage(LOAD_DATA2_FAILED, msg).sendToTarget();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtils.e(TAG + "changeInfo volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SET_CHANGEINFO + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "changeInfo token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("table", CommonParameters.TABLE_SHOP);
                map.put("id", mUserBean.getShop_id());

                if (isChangePhono)
                    map.put("img", mImg);
                if (isChangeName)
                    map.put("name", name);
                if (isChangeSjh)
                    map.put("sjh", sjh);
                if (isChangeIntro)
                    map.put("intro", mIntro);
                if (isChangeAddress)
                    map.put("address", mAddress);
                if (!TextUtils.isEmpty(mCounty) && !TextUtils.isEmpty(mLongitude) && !TextUtils.isEmpty(mLatitude)) {
                    map.put("county", mCounty);
                    map.put("jd", mLongitude);
                    map.put("wd", mLatitude);
                }

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "changeInfo json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
