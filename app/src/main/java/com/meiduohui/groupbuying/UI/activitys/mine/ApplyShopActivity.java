package com.meiduohui.groupbuying.UI.activitys.mine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

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
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private String mAddress = "";
    private String mLatitude = "";
    private String mLongitude = "";

    private int mImgType;

    private static final int IMG = 0;
    private static final int YYZZ = 1;
    private static final int SFZ = 2;
    private static final int XKZ = 3;
    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILE = 202;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    if (new File(mFilePath).exists())
                        new File(mFilePath).delete();

                    break;

                case LOAD_DATA1_FAILE:
                    ToastUtil.show(mContext, "上传失败");
                    break;

                case LOAD_DATA2_SUCCESS:

                    GlobalParameterApplication.getInstance().refeshHomeActivity(ApplyShopActivity.this);
                    break;

                case LOAD_DATA2_FAILE:

                    String text = (String) msg.obj;
                    LogUtils.i("LoginActivity: text " + text);
                    ToastUtil.show(mContext, text);
                    break;

                case NET_ERROR:

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
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

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

                if ("".equals(name)) {
                    ToastUtil.show(mContext, "商户名称不能为空");
                    return;
                } else if ("".equals(lxr)) {
                    ToastUtil.show(mContext, "联系人姓名不能为空");
                    return;
                } else if ("".equals(sjh)) {
                    ToastUtil.show(mContext, "手机号码不能为空");
                    return;
                }
//                else if ("".equals(sfz)) {
//                    ToastUtil.show(mContext, "身份证不能为空");
//                    return;
//                }
                else if (TextUtils.isEmpty(mYyzz)) {
                    ToastUtil.show(mContext, "请上传营业执照");
                    return;
                } else if (TextUtils.isEmpty(mSfz)) {
                    ToastUtil.show(mContext, "请上传身份证");
                    return;
                } else if (TextUtils.isEmpty(mXkz)) {
                    ToastUtil.show(mContext, "请上传许可证");
                    return;
                }

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
    private static final int PERMISSION_GRANTED = 1000;
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

            case PERMISSION_GRANTED:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    camera();
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

        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.alpha = 0.6f;   //这句就是设置窗口里崆件的透明度的．0全透明．1不透明．
        window.setAttributes(wl);

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_select_pic, null);

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

        //拍照
        view.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    camera();
                else {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ApplyShopActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_GRANTED);
                    } else {
                        camera();
                    }
                }
                popupWindow.dismiss();

            }
        });

        //选择照片
        view.findViewById(R.id.album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                popupWindow.dismiss();

            }
        });

        //取消
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, -view.getHeight());
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
                    mAddress = data.getStringExtra("address");
                    mLatitude = data.getStringExtra("Latitude");
                    mLongitude = data.getStringExtra("Longitude");
                    Log.d(TAG, "onActivityResult: address " + mAddress
                            + " mLatitude " + mLatitude
                            + " mLongitude " + mLongitude);
                }
                break;

            case REQUEST_CODE_PICK_IMAGE:

                if (resultCode == RESULT_OK && data != null) {

                    Uri uri = data.getData();
                    if (uri == null)
                        return;

                    Log.d(TAG, "onActivityResult: PICK_IMAGE Uri " + uri.getPath());

                    creatFolder();
                    cropPic(uri);
                }
                break;

            case REQUEST_CODE_CAPTURE_CAMERA:

                if (resultCode == RESULT_OK) {

                    Uri uri = getFileUri(mContext,new File(mFilePath));

                    Log.d(TAG, "onActivityResult: CAPTURE_CAMERA Uri " + uri.toString());

                    cropPic(uri);
                }
                break;

            case PHOTO_REQUEST_CUT:

                if (resultCode == RESULT_OK) {

                    Log.d(TAG, "onActivityResult PHOTO_REQUEST_CUT");

                    try {

                        //将Uri图片转换为Bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mCropUri));
                        //                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(filepath));
                        //                        saveImage(bitmap,mImgFile);
                        Log.d(TAG, "onActivityResult: mCropUri Uri " + mCropUri.toString());

                        mImgFile = new File(mFilePath);

                        uploadFile(mImgFile);

                        switch (mImgType) {

                            case IMG:
                                setCarHeader(mImgFile,mCivShopImg);
                                break;

                            case YYZZ:
                                setCarHeader(mImgFile,mIvAddYyzz);
                                break;

                            case SFZ:
                                setCarHeader(mImgFile,mIvAddSfz);
                                break;

                            case XKZ:
                                setCarHeader(mImgFile,mIvAddXkz);
                                break;
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    // 拍照
    private void camera() {

        File file = null;

        try {
            creatFolder();
            file = new File(mFilePath);

        } catch (Exception e) {
            Log.e(TAG, "camera Exception" + e.getMessage());
            e.printStackTrace();
        }

        Uri uri = getFileUri(this, file);

        //调用系统的拍照权限进行拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMERA);
    }

    private static final String imgFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
            + "Mei" + File.separator + "icon";
    private String mFilePath = imgFolder + File.separator + "icon_img" + ".jpg"; // 图片路径
    private File mImgFile;              // 生成的文件
    private Uri mCropUri;              // 裁剪图片的uri


    // 裁剪图片
    private void cropPic(Uri uri) {

        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");// Uri是已经选择的图片Uri
        intent.putExtra("return-data", true);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 320);// 输出图片大小 不能输出过大,否则小米手机会出现问题
        intent.putExtra("outputY", 320);

        mCropUri = Uri.parse("file://" + "/" + mFilePath);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    // 创建图片存放文件夹
    private void creatFolder() {

        File imgFolderFile = new File(imgFolder);
        if (!(imgFolderFile).exists()) {
            imgFolderFile.mkdirs();
        }

        Log.d(TAG, "creatFolder length:" + imgFolderFile.length());
    }

    // file转uri
    private Uri getFileUri(Context context, File file) {

        Uri uri = Uri.fromFile(file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }

        Log.d(TAG, "getFileUri：uri " + uri.getPath());
        return uri;
    }

    // 获取uri
    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);

        } else {

            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            } else {
                return null;
            }
        }
    }

    // 保存头像
    public void saveImage(Bitmap photo, File spath) {

        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
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

                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILE);
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
                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);
                            return;
                        }

                        mHandler.obtainMessage(LOAD_DATA2_FAILE, msg).sendToTarget();

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

                map.put("id", mUserBean.getId());
                map.put("name", name);
                map.put("img", mImg);
                map.put("intro", mIntro);
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
