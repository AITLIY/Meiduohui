package com.meiduohui.groupbuying.UI.activitys.mine;

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
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.mine.setting.ShopAddressActivity;
import com.meiduohui.groupbuying.UI.activitys.mine.setting.ShopIntroActivity;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.UrlArrayBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.MultiPartStack;
import com.meiduohui.groupbuying.utils.MultiPartStringRequest;
import com.meiduohui.groupbuying.utils.NetworkUtils;
import com.meiduohui.groupbuying.utils.PictureSelectorConfig;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
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

public class ApplyShopActivity extends AppCompatActivity {

    private String TAG = "ApplyShopActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.ed_name)
    EditText mEdName;
    @BindView(R.id.civ_shop_img)
    CircleImageView mCivShopImg;
    @BindView(R.id.ed_lxr)
    EditText mEdLxr;
    @BindView(R.id.ed_sjh)
    EditText mEdSjh;
    @BindView(R.id.ed_sfz)
    EditText mEdSfz;
    @BindView(R.id.iv_add_yyzz)
    ImageView mIvAddYyzz;
    @BindView(R.id.iv_add_sfz)
    ImageView mIvAddSfz;
    @BindView(R.id.iv_add_xkz)
    ImageView mIvAddXkz;

    private String mImg = "";
    private String mYyzz = "";
    private String mSfz = "";
    private String mXkz = "";
    private String mIntro = "";
    private String mCounty = "";
    private String mAddress = "";
    private String mLatitude = "";
    private String mLongitude = "";

    private int mImgType;
    private File mImgFile;              // 生成的文件

    private static final int IMG = 0;
    private static final int YYZZ = 1;
    private static final int SFZ = 2;
    private static final int XKZ = 3;
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

                    switch (mImgType) {

                        case IMG:
                            mLoadingDailog.dismiss();
                            setCarHeader(mImgFile,mCivShopImg);
                            break;

                        case YYZZ:
                            mLoadingDailog.dismiss();
                            setCarHeader(mImgFile,mIvAddYyzz);
                            break;

                        case SFZ:
                            mLoadingDailog.dismiss();
                            setCarHeader(mImgFile,mIvAddSfz);
                            break;

                        case XKZ:
                            mLoadingDailog.dismiss();
                            setCarHeader(mImgFile,mIvAddXkz);
                            break;
                    }
                    break;

                case LOAD_DATA1_FAILED:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, "上传失败");
                    break;

                case LOAD_DATA2_SUCCESS:
                    mLoadingDailog.dismiss();
                    ToastUtil.show(mContext, (String) msg.obj);
                    GlobalParameterApplication.getInstance().refeshHomeActivity(ApplyShopActivity.this);
                    break;

                case LOAD_DATA2_FAILED:
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
        setContentView(R.layout.activity_apply_shop);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
        initDailog();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();
    }

    private LoadingDailog mLoadingDailog;
    private void initDailog() {
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("加载中...")
                .setCancelable(false)
                .setCancelOutside(false);
        mLoadingDailog = loadBuilder.create();
    }

    @OnClick({R.id.btn_back, R.id.civ_shop_img,R.id.ll_intro, R.id.ll_address, R.id.ll_add_yyzz, R.id.ll_add_sfz, R.id.ll_add_xkz, R.id.tv_affirm})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_back:
                finish();
                break;

            case R.id.civ_shop_img:
                setImg(IMG);
                break;

            case R.id.ll_intro:
                Intent intent = new Intent(this, ShopIntroActivity.class);
                intent.putExtra("mIntro", mIntro);
                startActivityForResult(intent, RECORD_REQUEST_CODE);
                break;

            case R.id.ll_address:
                Intent intent2 = new Intent(this, ShopAddressActivity.class);
                intent2.putExtra("mAddress", mAddress);
                startActivityForResult(intent2, RECORD_REQUEST_CODE2);
                break;

            case R.id.ll_add_yyzz:
                setImg(YYZZ);
                break;
            case R.id.ll_add_sfz:
                setImg(SFZ);
                break;
            case R.id.ll_add_xkz:
                setImg(XKZ);
                break;
            case R.id.tv_affirm:
                if (!NetworkUtils.isConnected(mContext)){
                    ToastUtil.show(mContext,"当前无网络");
                    return;
                }

                String name = mEdName.getText().toString();
                String lxr = mEdLxr.getText().toString();
                String sjh = mEdSjh.getText().toString();
                String sfz = mEdSfz.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    ToastUtil.show(mContext, "商户名称不能为空");
                    return;
                } else if (TextUtils.isEmpty(mIntro)) {
                    ToastUtil.show(mContext, "商户简介不能为空");
                    return;
                } else if (TextUtils.isEmpty(mAddress)) {
                    ToastUtil.show(mContext, "商户地址不能为空");
                    return;
                } else if (TextUtils.isEmpty(mCounty)) {
                    ToastUtil.show(mContext, "位置错误，请重新定位");
                    return;
                } else if (TextUtils.isEmpty(lxr)) {
                    ToastUtil.show(mContext, "联系人姓名不能为空");
                    return;
                } else if (TextUtils.isEmpty(sjh)) {
                    ToastUtil.show(mContext, "手机号码不能为空");
                    return;
                }
//                else if ("".equals(sfz)) {
//                    ToastUtil.show(mContext, "身份证不能为空");
//                    return;
//                }
                else if (TextUtils.isEmpty(mImg)) {
                    ToastUtil.show(mContext, "请上传商户头像");
                    return;
                } else if (TextUtils.isEmpty(mYyzz)) {
                    ToastUtil.show(mContext, "请上传营业执照");
                    return;
                } else if (TextUtils.isEmpty(mSfz)) {
                    ToastUtil.show(mContext, "请上传身份证");
                    return;
                } else if (TextUtils.isEmpty(mXkz)) {
                    ToastUtil.show(mContext, "请上传许可证");
                    return;
                }

                mLoadingDailog.show();
                shopApply(name,lxr,sjh,sfz);
                break;
        }
    }

    private void setImg(int type) {

        mImgType = type;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            showSettingHeaderPic();
        else {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ApplyShopActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GET_WRITE_EXTERNAL_STORAGE);
            } else {
                showSettingHeaderPic();
            }
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

                    ToastUtil.show(mContext, "您已取消授权，设置失败");
                }
                break;

            case PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (mImgType==IMG) {
                        PictureSelectorConfig.initSingleCameraConfig(ApplyShopActivity.this);
                    } else {
                        PictureSelectorConfig.initEntirelySingleCameraConfig(ApplyShopActivity.this);
                    }
                } else {

                    ToastUtil.show(mContext, "您已取消授权，拍照失败");
                }
                break;
        }
    }

    private PopupWindow popupWindow;
    private static final int RECORD_REQUEST_CODE = 3000;
    private static final int RECORD_REQUEST_CODE2 = 4000;
    private static final int REQUEST_CODE_PICK_IMAGE = 801;
    private static final int REQUEST_CODE_CAPTURE_CAMERA = 802;
    private static final int PHOTO_REQUEST_CUT = 803;

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
                    if (mImgType==IMG) {
                        PictureSelectorConfig.initSingleCameraConfig(ApplyShopActivity.this);
                    } else {
                        PictureSelectorConfig.initEntirelySingleCameraConfig(ApplyShopActivity.this);
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ApplyShopActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
                    } else {
                        if (mImgType==IMG) {
                            PictureSelectorConfig.initSingleCameraConfig(ApplyShopActivity.this);
                        } else {
                            PictureSelectorConfig.initEntirelySingleCameraConfig(ApplyShopActivity.this);
                        }
                    }
                }
                popupWindow.dismiss();

            }
        });

        // 选择照片
        mAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mImgType==IMG) {
                    PictureSelectorConfig.initSingleConfig(ApplyShopActivity.this);
                } else {
                    PictureSelectorConfig.initEntirelySingleConfig(ApplyShopActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case RECORD_REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    mIntro = data.getStringExtra("intro");
                    Log.d(TAG, "onActivityResult: intro " + mIntro);
                }
                break;

            case RECORD_REQUEST_CODE2:

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

                    if (mImgType==IMG){
                        mImgFile = new File(images.get(0).getCutPath());
                    } else {
                        mImgFile = new File(images.get(0).getPath());
                    }

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
    public void setCarHeader(File file,ImageView imageView) {
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


    // 图片视频上传
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

                            switch (mImgType) {

                                case IMG:
                                    mImg = urls.get(0);
                                    break;

                                case YYZZ:
                                    mYyzz = urls.get(0);
                                    break;

                                case SFZ:
                                    mSfz = urls.get(0);
                                    break;

                                case XKZ:
                                    mXkz = urls.get(0);
                                    break;
                            }

                            LogUtils.i(TAG + "uploadFile mImgType " + mImgType + " url  " + urls.get(0));
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

    // 商户申请
    private void shopApply(final String name,final String lxr,final String sjh,final String sfzh) {

        final String url = HttpURL.BASE_URL + HttpURL.SHOP_APPLY;
        LogUtils.i(TAG + "shopApply url " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "shopApply result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "shopApply msg " + msg);
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
                LogUtils.e(TAG + "shopApply volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.SHOP_APPLY + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "shopApply token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());
                map.put("name", name);
                map.put("img", mImg);
                map.put("intro", mIntro);
                map.put("county", mCounty);
                map.put("address", mAddress);
                map.put("lxr", lxr);
                map.put("sjh", sjh);
                map.put("yyzz", mYyzz);
                map.put("sfz", mSfz);
                map.put("xkz", mXkz);
                if (!TextUtils.isEmpty(mLongitude) && !TextUtils.isEmpty(mLatitude)) {
                    map.put("jd", mLongitude);
                    map.put("wd", mLatitude);
                }

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "shopApply json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }


}
