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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.githang.statusbar.StatusBarCompat;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.HomepageActivity;
import com.meiduohui.groupbuying.UI.views.CircleImageView;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.MD5Utils;
import com.meiduohui.groupbuying.utils.MultiPartStack;
import com.meiduohui.groupbuying.utils.MultiPartStringRequest;
import com.meiduohui.groupbuying.utils.SpUtils;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;
import com.meiduohui.groupbuying.utils.UnicodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VipInfoActivity extends AppCompatActivity {

    @BindView(R.id.civ_user_img)
    CircleImageView mCivUserImg;
    @BindView(R.id.ed_name)
    EditText mEdName;
    private String TAG = "GeneralQuanActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILE = 102;
    private static final int NET_ERROR = 404;

    private boolean isChangePhono;
    private boolean isChangeName;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    startActivity(new Intent(mContext, HomepageActivity.class));
                    finish();
                    ToastUtil.show(mContext, "修改成功");
                    break;

                case LOAD_DATA1_FAILE:

                    ToastUtil.show(mContext, "修改失败");
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
        setContentView(R.layout.activity_vip_info);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        initData();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

        //        Glide.with(mContext)
        //                .load(mUserInfo.getAvatar())
        //                .error(R.drawable.icon_tab_usericon)
        //                .into(userIcon);
        //        user_name_ed.setText(mUserInfo.getNickname());

    }


    @OnClick({R.id.iv_back, R.id.civ_user_img, R.id.tv_commit})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.iv_back:
                break;

            case R.id.civ_user_img:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    showSettingHeaderPic();
                else {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(VipInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GET_WRITE_EXTERNAL_STORAGE);
                    } else {
                        showSettingHeaderPic();
                    }
                }
                break;

            case R.id.tv_commit:
                uploadFile(file);
                break;
        }
    }

    private static final int PERMISSION_GRANTED = 1000;
    private static final int GET_WRITE_EXTERNAL_STORAGE = 2000;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSION_GRANTED:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    camera();
                } else {

                    ToastUtil.show(mContext, "您已取消授权，拍照失败");
                }
                break;

            case GET_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showSettingHeaderPic();
                } else {

                    ToastUtil.show(mContext, "您已取消授权，设置失败");
                }
                break;

        }
    }

    private PopupWindow popupWindow;
    private static final int REQUEST_CODE_PICK_IMAGE = 801;
    private static final int REQUEST_CODE_CAPTURE_CAMEIA = 802;
    private static final int PHOTO_REQUEST_CUT = 803;
    private static final String CAPTURE_PIC_TEMP_PATH = "capture_picture_temp_path";

    public void showSettingHeaderPic() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.pw_select_pic, null);

        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //拍照
        view.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    camera();
                else {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(VipInfoActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_GRANTED);
                    } else {
                        camera();
                    }
                }

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

            case REQUEST_CODE_PICK_IMAGE:

                if (resultCode == RESULT_OK && data != null) {

                    Uri uri = data.getData();
                    if (null == uri)
                        return;

                    Log.d(TAG, "PICK_IMAGE: 选择的图片" + uri.getPath());

                    creatFolder();
                    cropPic(uri);
                }
                break;

            case REQUEST_CODE_CAPTURE_CAMEIA:

                if (resultCode == RESULT_OK) {

                    String filepath = SpUtils.getSpString(mContext, CAPTURE_PIC_TEMP_PATH, "");
                    Log.d(TAG, "CAPTURE_CAMEIA: get imgpath :" + filepath);

                    File spath = new File(filepath);

                    Uri uri = Uri.fromFile(spath);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uri = getImageContentUri(spath);
                    }

                    Log.d(TAG, "CAPTURE_CAMEIA: 当前的uri " + uri.toString());

                    cropPic(uri);
                }
                break;

            case PHOTO_REQUEST_CUT:

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "PHOTO_REQUEST_CUT 得到裁剪后图片");
                    try {
                        isChangePhono = true;
                        //将Uri图片转换为Bitmap
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                        //                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(filepath));
                        file = new File(filepath);
                        //                        saveImage(bitmap,file);
                        setCarHeader(file);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void camera() {

        File file = null;

        try {
            creatFolder();

            String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + "Aobosh" + File.separator + "ico" + File.separator + System.currentTimeMillis() + ".jpg";
            SpUtils.put(mContext, CAPTURE_PIC_TEMP_PATH, filepath);

            Log.i(TAG, "CAPTURE_CAMEIA: put imgpath " + filepath);

            file = new File(filepath);

        } catch (Exception e) {
            Log.e(TAG, "CAPTURE_CAMEIA:  exception" + e.getMessage());
            e.printStackTrace();
        }

        Uri uri = getCameraContentUri(this, file);

        //调用系统的拍照权限进行拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);

        popupWindow.dismiss();
    }

    //创建图片存放文件夹
    File imgFolderFile;

    private void creatFolder() {

        String imgFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "Aobosh" + File.separator + "ico";
        imgFolderFile = new File(imgFolder);

        if (!(imgFolderFile).exists()) {
            imgFolderFile.mkdirs();
        }

        Log.d(TAG, "CREAT_FOLDER length:" + imgFolderFile.length());
    }

    private static Uri getCameraContentUri(Context context, File file) {

        Uri uri = Uri.fromFile(file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        }
        Log.d("File", "拍照保存到位置：" + uri.getPath());

        return uri;
    }

    private File file;
    private String filepath;
    private Uri uritempFile;

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
        System.out.println("-------------------------------------------cropPic");

        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        String time = df.format(new Date());

        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "Aobosh" + File.separator + "ico" + File.separator
                + "ico_" + time + ".jpg";
        uritempFile = Uri.parse("file://" + "/" + filepath);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, PHOTO_REQUEST_CUT);

    }

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

    public void setCarHeader(File file) {
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
        //        userIcon.setImageBitmap(bitmap);
    }

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

    //--------------------------------------请求服务器数据-------------------------------------------

    // 修改用户信息
    private void changeUserInfo(final int uid, final String appname, final File file) {

        final String url = HttpURL.BASE_URL + HttpURL.SHOP_ADDQUAN;
        LogUtils.i(TAG + "addGeneraQuan url " + url);
        MultiPartStringRequest stringRequest = new MultiPartStringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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

                            //                            String data = jsonResult.getString("data");
                            //                            JSONObject object = new JSONObject(data);
                            //
                            //                            if (isChangeName) {
                            //                                LogUtils.i("UserInfoActivity: isChangeName ");
                            //                                mUserBean.setName(data.e("appname"));
                            //                            }
                            //
                            //                            if (isChangePhono) {
                            //                                LogUtils.i("UserInfoActivity: isChangePhono ");
                            //                                mUserBean.set(data.getString("appavatar"));
                            //                            }
                            //
                            //                            GlobalParameterApplication.getInstance().setUserInfo(mUserBean);
                            //
                            //                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);

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
                LogUtils.e(TAG + "getOrderList volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {

            @Override
            public Map<String, File> getFileUploads() {
                Map<String, File> files = new HashMap<String, File>();

                if (isChangePhono) {
                    LogUtils.d(TAG + "getOrderList getFileUploads " + file.getAbsolutePath());
                    files.put("avatar", file);
                }
                return files;
            }

            @Override
            public Map<String, String> getStringUploads() {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.MEM_ORDERLIST + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "getOrderList token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("shop_id", mUserBean.getShop_id());
                //                map.put("page", mPage + "");
                //                map.put("state", state + "");

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "getOrderList json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

    // 修改用户信息
    private void uploadFile(final File file) {
        requestQueue = Volley.newRequestQueue(this, new MultiPartStack());
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

//                if (isChangePhono) {
                    LogUtils.d(TAG + "uploadFile getFileUploads " + file.getAbsolutePath());
                    files.put("file", file);
//                }
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
        requestQueue.add(stringRequest);
    }

}
