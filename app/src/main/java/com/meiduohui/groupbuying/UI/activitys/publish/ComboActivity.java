package com.meiduohui.groupbuying.UI.activitys.publish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDailog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.githang.statusbar.StatusBarCompat;
import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.categorys.SelCatActivity;
import com.meiduohui.groupbuying.UI.activitys.coupons.PayOrderActivity;
import com.meiduohui.groupbuying.UI.activitys.main.PlusImageActivity;
import com.meiduohui.groupbuying.UI.activitys.publish.videoCompress.CompressListener;
import com.meiduohui.groupbuying.UI.activitys.publish.videoCompress.Compressor;
import com.meiduohui.groupbuying.UI.activitys.publish.videoCompress.CustomProgressDialog;
import com.meiduohui.groupbuying.UI.activitys.publish.videoCompress.GetPathFromUri;
import com.meiduohui.groupbuying.UI.activitys.publish.videoCompress.InitListener;
import com.meiduohui.groupbuying.UI.views.CustomDialog;
import com.meiduohui.groupbuying.UI.views.MyGridView;
import com.meiduohui.groupbuying.UI.views.SmartHintTextView;
import com.meiduohui.groupbuying.adapter.AddImgAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.AddMsgBean;
import com.meiduohui.groupbuying.bean.UrlArrayBean;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.commons.HttpURL;
import com.meiduohui.groupbuying.utils.FileUtils;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComboActivity extends AppCompatActivity {

    private String TAG = "ComboActivity: ";
    private Context mContext;
    private RequestQueue requestQueue;
    private UserBean mUserBean;

    @BindView(R.id.ed_title)
    EditText mEdTitle;
    @BindView(R.id.ed_intro)
    EditText mEdIntro;
    @BindView(R.id.tv_cat)
    SmartHintTextView mTvCat;
    @BindView(R.id.gv_img)
    MyGridView mGvImg;
    @BindView(R.id.ll_add_img)
    LinearLayout mLlAddImg;
    @BindView(R.id.iv_video_thumb)
    ImageView mIvVideoThumb;
    @BindView(R.id.rv_video_complete)
    RelativeLayout mRvVideoComplete;
    @BindView(R.id.rv_add_video)
    RelativeLayout mRvAddVideo;
    @BindView(R.id.ed_m_price)
    EditText mEdMPrice;
    @BindView(R.id.ed_m_old_price)
    EditText mEdMOldPrice;
    @BindView(R.id.tv_start_time)
    SmartHintTextView mTvStartTime;
    @BindView(R.id.tv_end_time)
    SmartHintTextView mTvEndTime;
    @BindView(R.id.tv_type)
    SmartHintTextView mTvType;
    @BindView(R.id.ll_coupon_content)
    LinearLayout mLlCouponContent;
    @BindView(R.id.ed_number)
    EditText mEdNumber;
    @BindView(R.id.ed_price)
    EditText mEdPrice;
    @BindView(R.id.ed_beizhu)
    EditText mEdBeiZhu;
    @BindView(R.id.tv_length)
    TextView mTvLength;
    @BindView(R.id.ed_yxq)
    EditText mEdYxq;
    @BindView(R.id.sw_yuding)
    Switch mSwYuDing;

    private AddMsgBean mAddMsgBean = new AddMsgBean();
    private AddImgAdapter mAddImgAdapter;
    private ArrayList<String> mPicList = new ArrayList<>(); // 上传的图片凭证的数据源
    private List<String> mUrlList = new ArrayList<>();      // 上传的图片成功的url
    private String mVideoUrl; // 视频url

    private String cat_id1;
    private String cat_id2;
    private long mStartTime;
    private long mEndTime;
    private int mTotalDay;
    private int mYuding = 0;

    private static final int LOAD_DATA1_SUCCESS = 101;
    private static final int LOAD_DATA1_FAILED = 102;
    private static final int LOAD_DATA2_SUCCESS = 201;
    private static final int LOAD_DATA2_FAILED = 202;
    private static final int LOAD_DATA3_SUCCESS = 301;
    private static final int LOAD_DATA3_FAILED = 302;
    private static final int NET_ERROR = 404;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOAD_DATA1_SUCCESS:

                    mLoadingDailog.dismiss();
                    break;

                case LOAD_DATA1_FAILED:

                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, "上传失败");
                    break;

                case LOAD_DATA2_SUCCESS:

                    mLoadingDailog.dismiss();
                    mRvVideoComplete.setVisibility(View.VISIBLE);
                    LogUtils.i(TAG + "uploadFile mVideoUrlImg " + mVideoUrl + CommonParameters.VIDEO_END);
                    Glide.with(mContext)
                            .load(mVideoUrl + CommonParameters.VIDEO_END)
                            .apply(new RequestOptions().error(R.drawable.icon_bg_default_img))
                            .into(mIvVideoThumb);
                    break;

                case LOAD_DATA2_FAILED:

                    mVideoUrl = null;
                    mLlAddImg.setVisibility(View.VISIBLE);
                    mRvVideoComplete.setVisibility(View.GONE);

                    mLoadingDailog.dismiss();
                    ToastUtils.show(mContext, "上传失败");
                    break;

                case LOAD_DATA3_SUCCESS:

                    mLoadingDailog.dismiss();

                    double totlePrice = mAddMsgBean.getPay_price();

                    if (totlePrice == 0) {
                        ToastUtils.show(mContext, "发布成功");

                    } else {

                        String priceMsg = "发布成功，需支付费用" + totlePrice + "元，是否现在支付？";
                        new CustomDialog(mContext).builder()
                                .setTitle("提示")
                                .setMessage(priceMsg)
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        GlobalParameterApplication.getInstance().setPayIntention(CommonParameters.PUBLISH_MSG);
                                        Intent intent = new Intent(ComboActivity.this, PayOrderActivity.class);
                                        intent.putExtra("OrderID", mAddMsgBean.getOrder_id());
                                        intent.putExtra("table", mAddMsgBean.getTable());
                                        intent.putExtra("notify", mAddMsgBean.getNotify());
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .setCancelable(false).show();
                    }
                    break;

                case LOAD_DATA3_FAILED:

                    mLoadingDailog.dismiss();
                    String text = (String) msg.obj;
                    LogUtils.i("LoginActivity: text " + text);
                    ToastUtils.show(mContext, text);
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
        setContentView(R.layout.activity_combo);
        ButterKnife.bind(this);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.app_title_bar), true);

        init();
    }

    private void init() {
        initDailog();
        initData();
        initGridView();

        initFile();
        initVideo();
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

        mSwYuDing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mYuding = 1;
                } else {
                    mYuding = 0;
                }
            }
        });

        mEdBeiZhu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: s " + s);

                mTvLength.setText(s.length() + "/" + 300);
                if (s.length() > 300)
                    mEdBeiZhu.setError("输入超长");
            }
        });
    }

    //初始化展示上传图片的GridView
    private void initGridView() {
        mAddImgAdapter = new AddImgAdapter(mContext, mPicList);
        mAddImgAdapter.setOnItemClickListener(new AddImgAdapter.OnItemClickListener() {
            @Override
            public void onDelImg(int position) {

                mPicList.remove(position);
                if (mUrlList.get(position) != null)
                    mUrlList.remove(position);
                mAddImgAdapter.notifyDataSetChanged();

                if (mPicList.size() == 0) {
                    mLlAddImg.setVisibility(View.VISIBLE);
                    mRvAddVideo.setVisibility(View.VISIBLE);
                    mGvImg.setVisibility(View.GONE);
                }

            }
        });
        mGvImg.setAdapter(mAddImgAdapter);
        mGvImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == parent.getChildCount() - 1) {

                    //如果“增加按钮形状的”图片的位置是最后一张，且添加了的图片的数量不超过8张，才能点击
                    if (mPicList.size() == CommonParameters.MAX_SELECT_PIC_NUM) { // 最多添加8张图片

                        viewPluImg(position);
                    } else {

                        selectPic(CommonParameters.MAX_SELECT_PIC_NUM - mPicList.size()); // 添加图片
                    }
                } else {
                    viewPluImg(position);
                }
            }
        });
    }

    // 查看大图
    private void viewPluImg(int position) {
        Intent intent = new Intent(mContext, PlusImageActivity.class);
        intent.putStringArrayListExtra(CommonParameters.IMG_LIST, mPicList);
        intent.putExtra(CommonParameters.POSITION, position);
        startActivity(intent);
    }

    // 打开相册选择
    private void selectPic(int maxTotal) {
        PictureSelectorConfig.initMultiConfig(this, maxTotal);
        //        PictureSelectorConfig.initSingleConfig(this);
    }

    // 打开视频选择
    private void selectVideo() {
        PictureSelectorConfig.initVideoConfig(this);
    }

    // 删除视频
    private void delVideo() {
        mVideoUrl = null;
        mLlAddImg.setVisibility(View.VISIBLE);
        mRvVideoComplete.setVisibility(View.GONE);
    }

    // 处理选择的照片的地址
    private void refreshAdapter(List<LocalMedia> picList) {
        for (LocalMedia localMedia : picList) {

            LogUtils.i(TAG + "refreshAdapter getPath " + localMedia.getPath());
            LogUtils.i(TAG + "refreshAdapter getCutPath " + localMedia.getCutPath());
            LogUtils.i(TAG + "refreshAdapter getCompressPath " + localMedia.getCompressPath());

            // 例如 LocalMedia 里面返回三种path
            // 1.media.getPath(); 为原图path
            // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
            // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
            // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

            if (localMedia.isCompressed()) {
                String compressPath = localMedia.getCompressPath(); //压缩后的图片路径
                mPicList.add(compressPath); //把图片添加到将要上传的图片数组中
                mAddImgAdapter.notifyDataSetChanged();
            }
        }
        mLoadingDailog.show();
        uploadFile();
    }

    // 处理选择的视频的地址
    private void refreshVideo(String path) {

        LogUtils.i(TAG + " COMPRESS length=" + new File(path).length());

        if (new File(path).length() < 1024*1024*3) {
            mLoadingDailog.show();
            uploadFile(new File(path));

        } else {
            mVideoPath = path;
            getVideoTime();
            startCompress();
        }

    }

    private static final int CATEGORY_REQUEST_CODE = 3000;
    private static final int COMPRESS_REQUEST_CODE = 2000;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调

                    List<LocalMedia> localMedias = PictureSelector.obtainMultipleResult(data);

                    if (localMedias.size() > 0) {
                        mLlAddImg.setVisibility(View.GONE);
                        mRvAddVideo.setVisibility(View.GONE);
                        mGvImg.setVisibility(View.VISIBLE);
                        refreshAdapter(localMedias);
                    }

                    break;

                case PictureConfig.SINGLE:
                    // 视频选择结果回调

                    List<LocalMedia> localMedias2 = PictureSelector.obtainMultipleResult(data);

                    if (localMedias2.size() > 0) {
                        mLlAddImg.setVisibility(View.GONE);
                        LogUtils.i(TAG + "onActivityResult uploadFile getPath " + localMedias2.get(0).getPath());
                        LogUtils.i(TAG + "onActivityResult uploadFile getCutPath " + localMedias2.get(0).getCutPath());
                        LogUtils.i(TAG + "onActivityResult uploadFile getCompressPath " + localMedias2.get(0).getCompressPath());
                        refreshVideo(localMedias2.get(0).getPath());
                    }

                    break;

                case CATEGORY_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {

                        cat_id1 = data.getStringExtra("cat_id1");
                        cat_id2 = data.getStringExtra("cat_id2");
                        String catName = data.getStringExtra("catName");

                        LogUtils.i(TAG + "onActivityResult cat_id1 " + cat_id1
                                + " cat_id2 " + cat_id2
                                + " catName " + catName);
                        mTvCat.setText(catName);
                    }
                    break;

                case COMPRESS_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
//                        mVideoPath = GetPathFromUri.getPath(mContext, data.getData());
//                        Log.i(TAG, "COMPRESS onActivityResult mVideoPath: " + mVideoPath);

//                        getVideoTime();
                    }
                    break;
            }
        }

    }

    //----------------------------------------视频压缩-----------------------------------------------

    private Compressor mCompressor;
    private String currentOutputVideoPath = "";//压缩后的视频地址

    private String videoTime = "";//获取视频时长
    private int videoWidth = 0;//获取视频的宽度
    private int videoHeight = 0;//获取视频的高度
    private int videoGotation = 0;//获取视频的角度
    private Bitmap mBitMap;
    private Double videoLength = 0.00;//视频时长 s

    private CustomProgressDialog mProcessingDialog;

    private String mVideoPath = "";//原视频地址

    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mei/";

    private void initFile() {
        makeRootDirectory(PATH);
        currentOutputVideoPath = PATH + GetPathFromUri.getVideoFileName();
    }

    private void initVideo() {

        mProcessingDialog = new CustomProgressDialog(this);
        mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                LogUtils.i(TAG + " COMPRESS Cancel onDismiss");//如果取消压缩，那么需要销毁
                if (mCompressor != null) {
                    mCompressor.destory();
                }
            }
        });

        mCompressor = new Compressor(this);
        mCompressor.loadBinary(new InitListener() {
            @Override
            public void onLoadSuccess() {
                LogUtils.i(TAG + " COMPRESS loadBinary succeed");
            }

            @Override
            public void onLoadFail(String reason) {
                LogUtils.i(TAG + " COMPRESS loadBinary fail:" + reason);
            }
        });
    }

    /**
     * 导入视频
     */
    private void addLoacalVideo() {
        Intent intentvideo = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intentvideo.setAction(Intent.ACTION_GET_CONTENT);
            intentvideo.setType("video/*");
        } else {
            intentvideo.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intentvideo.addCategory(Intent.CATEGORY_OPENABLE);
            intentvideo.setType("video/*");
        }
        startActivityForResult(Intent.createChooser(intentvideo, "选择要导入的视频"), COMPRESS_REQUEST_CODE);
    }

    /**
     * 视频压缩开始
     */
    private void startCompress() {
        try {
            if (TextUtils.isEmpty(mVideoPath)) {
                ToastUtils.show(mContext, "请重新选择视频");

            } else {
                File file = new File(currentOutputVideoPath);
                if (file.exists()) {
                    file.delete();
                }

                String cmd = "";
                LogUtils.i(TAG + " COMPRESS startCompress=mVideoPath=" + mVideoPath);

                if (videoGotation == 90 || videoGotation == 270) {//之前以为和旋转的角度有关系，原来

                    LogUtils.i(TAG + " COMPRESS videoGotation=90");
                    cmd = "-y -i " + mVideoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
                            "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 480x800 -aspect 9:16 " + currentOutputVideoPath;
                } else {

                    LogUtils.i(TAG + " COMPRESS videoGotation=0");
                    if (videoWidth > videoHeight) {
                        cmd = "-y -i " + mVideoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
                                "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 800x480 -aspect 16:9 " + currentOutputVideoPath;
                    } else {
                        cmd = "-y -i " + mVideoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
                                "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 480x800 -aspect 9:16 " + currentOutputVideoPath;
                    }
                }

                mProcessingDialog.show();
                mProcessingDialog.setProgress(0);

                execCommand(cmd);
            }
        } catch (Exception e) {

            LogUtils.e(TAG + " COMPRESS startCompress=e=" + e.getMessage());
        }

    }

    /**
     * 执行压缩命令
     */
    private void execCommand(final String cmd) {
        File mFile = new File(currentOutputVideoPath);
        if (mFile.exists()) {
            mFile.delete();
        }

        LogUtils.i(TAG + " COMPRESS execCommand cmd= " + cmd);

        mCompressor.execCommand(cmd, new CompressListener() {
            @Override
            public void onExecSuccess(String message) {
                mProcessingDialog.dismiss();
                mLoadingDailog.show();
                String result = getString(R.string.compress_result_input_output, mVideoPath
                        , getFileSize(mVideoPath), currentOutputVideoPath, getFileSize(currentOutputVideoPath));
                LogUtils.i(TAG + " COMPRESS execCommand success " + result);
                ToastUtils.show(mContext, "压缩成功，开始上传");
                uploadFile(new File(currentOutputVideoPath));
            }

            @Override
            public void onExecFail(String reason) {
                mProcessingDialog.dismiss();
                LogUtils.i(TAG + " COMPRESS execCommand fail " + reason);
                ToastUtils.show(mContext, "压缩失败");
            }

            @Override
            public void onExecProgress(String message) {
                try {
                    LogUtils.i(TAG + " COMPRESS execCommand progress " + message);
                    double switchNum = getProgress(message);
                    if (switchNum == 10000) {
                        //如果找不到压缩的片段，返回为10000
                        LogUtils.i(TAG + " execCommand 10000");
                        mProcessingDialog.setProgress(0);
                    } else {
                        mProcessingDialog.setProgress((int) (getProgress(message) / 10));
                    }
                } catch (Exception e) {
                    mProcessingDialog.dismiss();
                    LogUtils.e(TAG + " COMPRESS execCommand e=" + e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompressor != null) {
            mCompressor.destory();
        }
    }



    double getProgressNum = 0.0;
    /**
     * 进度条，只能是整形，所以max为1000，最少为0
     *
     * @param source
     * @return
     */
    private double getProgress(String source) {
        if (source.contains("too large")) {//当文件过大的时候，会会出现 Past duration x.y too large
            LogUtils.i(TAG + " too large");
            return getProgressNum;
        }
        Pattern p = Pattern.compile("00:\\d{2}:\\d{2}");
        Matcher m = p.matcher(source);
        if (m.find()) {
            //00:00:00
            String result = m.group(0);
            String temp[] = result.split(":");
            double seconds = Double.parseDouble(temp[1]) * 60 + Double.parseDouble(temp[2]);
            if (0 != videoLength) {
                getProgressNum = seconds / videoLength * 1000;
                return seconds / videoLength * 1000;
            }
            if (seconds == videoLength) {
                return 1000;
            }
        }
        //        MyLog.i(TAG, "!m.find()="+getProgressNum);
        return 10000;//出现异常的时候，返回为10000
        //      return 0;//出现异常的时候，显示上一个进度条
    }

    /**
     * 获取视频的大小
     */
    private String getFileSize(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return "0 MB";
        } else {
            long size = f.length();
            return (size / 1024f) / 1024f + "MB";
        }
    }

    /**
     * 获取视频的时长
     */
    void getVideoTime() {
        try {
            MediaMetadataRetriever retr = new MediaMetadataRetriever();
            retr.setDataSource(mVideoPath);
            videoTime = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);//获取视频时长
            videoWidth = Integer.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));//获取视频的宽度
            videoHeight = Integer.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));//获取视频的高度
            videoGotation = Integer.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));//获取视频的角度

            LogUtils.i(TAG + " COMPRESS videoWidth=" + videoWidth);
            LogUtils.i(TAG + " COMPRESS videoHeight=" + videoHeight);

            //            MyLog.i(TAG, "videoTime=" + videoTime);
            //            mBitMap = retr.getFrameAtTime();
            videoLength = Double.parseDouble(videoTime) / 1000.00;
            LogUtils.i(TAG + " COMPRESS videoLength=" + videoLength);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG + " e=" + e.getMessage());
            videoLength = 0.00;
            finish();
            ToastUtils.show(mContext, "异常错误");
        }

    }

    /**
     * 没有文件夹 创建文件夹
     *
     * @param filePath
     */
    public void makeRootDirectory(String filePath) {
        LogUtils.i(TAG + " COMPRESS makeRootDirectory=");
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                Boolean isTrue = file.mkdir();
                LogUtils.i(TAG + " COMPRESS istrue=" + isTrue + "");
            }
        } catch (Exception e) {
            LogUtils.e(TAG + " COMPRESS istrue=" + e.toString());
        }
    }


    //----------------------------------------------------------------------------------------------

    @OnClick({R.id.iv_back, R.id.ll_add_img, R.id.rv_add_video, R.id.iv_del_video, R.id.ll_cat, R.id.ll_start_time, R.id.ll_end_time, R.id.ll_type, R.id.tv_affirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.ll_add_img:
                selectPic(CommonParameters.MAX_SELECT_PIC_NUM - mPicList.size()); // 添加图片
                break;

            case R.id.rv_add_video:
                selectVideo(); // 添加视频
                break;

            case R.id.iv_del_video:
                delVideo();
                break;

            case R.id.ll_cat:
                Intent intent = new Intent(this, SelCatActivity.class);
                startActivityForResult(intent, CATEGORY_REQUEST_CODE);
                break;

            case R.id.ll_start_time:
                setTvStartTime();
                break;

            case R.id.ll_end_time:
                setTvEndTime();
                break;

            case R.id.ll_type:
                setType();
                break;

            case R.id.tv_affirm:

                if (!NetworkUtils.isConnected(mContext)) {
                    ToastUtils.show(mContext, "网络异常,请稍后重试");
                    return;
                }

                String title = mEdTitle.getText().toString();
                String intro = mEdIntro.getText().toString();
                String cat = mTvCat.getText().toString();
                String m_price = mEdMPrice.getText().toString();
                String m_old_price = mEdMOldPrice.getText().toString();
                String startTime = mTvStartTime.getText().toString();
                String endTime = mTvEndTime.getText().toString();
                String beizhu = mEdBeiZhu.getText().toString();

                String number = mEdNumber.getText().toString();
                String price = mEdPrice.getText().toString();
                String yxq = mEdYxq.getText().toString();

                long totalTime = mEndTime - mStartTime;
                mTotalDay = (int) (totalTime / (24 * 60 * 60));

                if ("".equals(title)) {
                    ToastUtils.show(mContext, "标题不能为空");
                    return;

                } else if ("".equals(intro)) {
                    ToastUtils.show(mContext, "活动内容不能为空");
                    return;

                } else if (TextUtils.isEmpty(mVideoUrl) && mUrlList.size() < 1) {
                    ToastUtils.show(mContext, "请上传图片或视频");
                    return;

                } else if ("".equals(cat)) {
                    ToastUtils.show(mContext, "请选择商品类别");
                    return;

                } else if (TextUtils.isEmpty(m_price)) {
                    ToastUtils.show(mContext, "优惠价不能为空");
                    return;

                } else if (TextUtils.isEmpty(m_old_price)) {
                    ToastUtils.show(mContext, "原价不能为空");
                    return;

                } else if (TextUtils.isEmpty(startTime)) {
                    ToastUtils.show(mContext, "请选择开始时间");
                    return;

                } else if (TextUtils.isEmpty(endTime)) {
                    ToastUtils.show(mContext, "请选择结束时间");
                    return;

                } else if (mTotalDay < 0) {
                    ToastUtils.show(mContext, "结束日期必须大于开始日期");
                    return;

                } else if (mType != 0) {

                    if (TextUtils.isEmpty(number)) {
                        ToastUtils.show(mContext, "卡券数量不能为空");
                        return;
                    } else if (TextUtils.isEmpty(price)) {
                        ToastUtils.show(mContext, "卡券价格不能为空");
                        return;
                    }
//                    else if (TextUtils.isEmpty(yxq)) {
//                        ToastUtil.show(mContext, "有效期不能为空");
//                        return;
//                    }

                } else if (TextUtils.isEmpty(beizhu)) {
                    ToastUtils.show(mContext, "请填写使用规则");
                    return;
                }

                mLoadingDailog.show();
                addMessage(title, intro, m_price, m_old_price, beizhu, price, number, yxq);
                break;
        }
    }

    private void setTvStartTime() {

        TimePickerView pvTime = new TimePickerView.Builder(ComboActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                String time = TimeUtils.DateToString(date, "yyyy-MM-dd");
                mTvStartTime.setText(time);
                mStartTime = date.getTime() / 1000;
                LogUtils.i(TAG + "setTvEndTime mStartTime " + mStartTime);
            }
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(20)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                //                        .setTitleText("请选择时间")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.app_title_bar))//确定按钮文字颜色
                .setCancelColor(Color.GRAY)//取消按钮文字颜色
                //                .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                //                .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                //                .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年
                //                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                //                .setRangDate(startDate,endDate)//起始终止年月日设定
                //                .setLabel("年","月","日","时","分","秒")
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                //                .isDialog(true)//是否显示为对话框样式
                .build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();
    }

    private void setTvEndTime() {

        TimePickerView pvTime = new TimePickerView.Builder(ComboActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                String time = TimeUtils.DateToString(date, "yyyy-MM-dd");
                mTvEndTime.setText(time);
                mEndTime = date.getTime() / 1000;
                LogUtils.i(TAG + "setTvEndTime mEndTime " + mEndTime);
            }
        })
                .setType(TimePickerView.Type.YEAR_MONTH_DAY)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(20)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                //                        .setTitleText("请选择时间")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.app_title_bar))//确定按钮文字颜色
                .setCancelColor(Color.GRAY)//取消按钮文字颜色
                //                .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                //                .setBgColor(0xFF333333)//滚轮背景颜色 Night mode
                //                .setRange(calendar.get(Calendar.YEAR) - 20, calendar.get(Calendar.YEAR) + 20)//默认是1900-2100年
                //                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                //                .setRangDate(startDate,endDate)//起始终止年月日设定
                //                .setLabel("年","月","日","时","分","秒")
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                //                .isDialog(true)//是否显示为对话框样式
                .build();
        pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
        pvTime.show();
    }

    private int mType;
    private static final List<String> options1Items = new ArrayList<>();

    private void setType() {

        options1Items.clear();
        options1Items.add("代金券");
        options1Items.add("折扣券");
        options1Items.add("无");
        //        options1Items.add("会员券");

        OptionsPickerView pvOptions = new OptionsPickerView.Builder(ComboActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String s = options1Items.get(options1);
                mTvType.setText(s);
                LogUtils.i(TAG + "setType s " + s);
                switch (s) {

                    case "代金券":
                        mType = 1;
                        mLlCouponContent.setVisibility(View.VISIBLE);
                        mEdPrice.setHint("请输入代金券的价格");
                        break;

                    case "折扣券":
                        mType = 2;
                        mLlCouponContent.setVisibility(View.VISIBLE);
                        mEdPrice.setHint("请输入折扣(例如0.8,0.9)");
                        break;

                    case "无":
                        mType = 0;
                        mLlCouponContent.setVisibility(View.GONE);
                        break;

                    //                    case "会员券":
                    //                        mType = 3;
                    //                        break;
                }
            }
        })
                //                .setSubmitText("确定")//确定按钮文字
                //                .setCancelText("取消")//取消按钮文字
                //                .setTitleText("城市选择")//标题
                .setSubCalSize(20)//确定和取消文字大小
                //                .setTitleSize(20)//标题文字大小
                //                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.app_title_bar))//确定按钮文字颜色
                .setCancelColor(Color.GRAY)//取消按钮文字颜色
                //                .setTitleBgColor(0xFF333333)//标题背景颜色 Night mode
                //                .setBgColor(0xFF000000)//滚轮背景颜色 Night mode
                //                .setContentTextSize(18)//滚轮文字大小
                //                .setTextColorCenter(Color.BLUE)//设置选中项的颜色
                .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                //                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                //                .setLinkage(false)//设置是否联动，默认true
                //                .setLabels("省", "市", "区")//设置选择的三级单位
                //                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                //                .setCyclic(false, false, false)//循环与否
                //                .setSelectOptions(1, 1, 1)  //设置默认选中项
                //                .setOutSideCancelable(false)//点击外部dismiss default true
                //                .isDialog(true)//是否显示为对话框样式
                .build();
        pvOptions.setPicker(options1Items);
        pvOptions.show();
    }

    //--------------------------------------请求服务器数据-------------------------------------------


    // 图片上传
    private void uploadFile() {
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

                            mUrlList = mUrlArrayBeans.getUrl();
                            for (String url : mUrlList) {
                                LogUtils.i(TAG + "uploadFile url " + url);
                            }

                            mHandler.sendEmptyMessage(LOAD_DATA1_SUCCESS);
                            return;
                        }

                        mHandler.sendEmptyMessage(LOAD_DATA1_FAILED);

                    } catch (JSONException e) {
                        e.printStackTrace();
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

                for (int i = 0; i < mPicList.size(); i++) {

                    files.put("file" + i, new File(mPicList.get(i)));
                    LogUtils.d(TAG + "uploadFile getFileUploads " + new File(mPicList.get(i)).getAbsolutePath());
                }

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

    // 视频上传
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

                            mVideoUrl = urls.get(0);
                            LogUtils.i(TAG + "uploadFile url  " + mVideoUrl);
                            FileUtils.delAllFile(PATH);
                            mHandler.sendEmptyMessage(LOAD_DATA2_SUCCESS);
                            return;
                        }

                        mHandler.sendEmptyMessage(LOAD_DATA2_FAILED);

                    } catch (JSONException e) {
                        e.printStackTrace();
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

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 15000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 15000;
            }

            @Override
            public void retry(VolleyError volleyError) throws VolleyError {
                LogUtils.e(TAG + "uploadFile retry volleyError " + volleyError.toString());
            }
        });
        queue.add(stringRequest);
    }


    // 发布信息
    private void addMessage(final String title, final String intro, final String m_price, final String m_old_price,
                            final String beizhu,
                            final String price, final String number, final String yxq) {

        final String url = HttpURL.BASE_URL + HttpURL.ORDER_ADDMESSAGE;
        LogUtils.i(TAG + "addMessage url " + url);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)) {
                    LogUtils.i(TAG + "addMessage result " + s);

                    try {
                        JSONObject jsonResult = new JSONObject(s);
                        String msg = UnicodeUtils.revert(jsonResult.getString("msg"));
                        LogUtils.i(TAG + "addMessage msg " + msg);
                        String status = jsonResult.getString("status");

                        if ("0".equals(status)) {
                            String data = jsonResult.getString("data");
                            mAddMsgBean = new Gson().fromJson(data, AddMsgBean.class);
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
                LogUtils.e(TAG + "addMessage volleyError " + volleyError.toString());
                mHandler.sendEmptyMessage(NET_ERROR);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<String, String>();

                String token = HttpURL.ORDER_ADDMESSAGE + TimeUtils.getCurrentTime("yyyy-MM-dd") + CommonParameters.SECRET_KEY;
                LogUtils.i(TAG + "addMessage token " + token);
                String md5_token = MD5Utils.md5(token);

                map.put("mem_id", mUserBean.getId());

                if (mPicList.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < mUrlList.size(); i++) {

                        if (i == (mUrlList.size() - 1)) {
                            builder.append(mUrlList.get(i));
                        } else {
                            builder.append(mUrlList.get(i) + ",");
                        }
                    }
                    String urls = builder.toString();
                    map.put("img", urls);
                } else {
                    map.put("video", mVideoUrl);
                }

                map.put("title", title);
                map.put("intro", intro);
                map.put("cat_id1", cat_id1 + "");
                map.put("cat_id2", cat_id2 + "");
                map.put("start_time", mStartTime + "");
                map.put("end_time", mEndTime + "");
                map.put("m_price", m_price);
                map.put("m_old_price", m_old_price);
                map.put("yuding", mYuding + "");
                map.put("beizhu", beizhu);

                if (mType != 0) {

                    String str = "";
                    String con = "";

                    if (mType == 1) {
                        double pri = Double.parseDouble(price);
                        //                        str = String.format("%.2f", pri) + "元";
                        str = price + "元";
                        con = "代金券";

                    } else if (mType == 2) {
                        double cut = Double.parseDouble(price) * 10;
                        //                        str = String.format("%.1f", cut) + "折";
                        str = (int) cut + "折";
                        con = "折扣券";

                    } else if (mType == 3) {
                        double pri = Double.parseDouble(price);
                        //                        str = String.format("%.2f", pri) + "元";
                        str = price + "元";
                        con = "会员券";
                    }

                    map.put("type", mType + "");
                    map.put("content", str + con);
                    map.put("number", number);
                    map.put("price", price);
//                    map.put("yxq", yxq);
                }

                map.put(CommonParameters.ACCESS_TOKEN, md5_token);
                map.put(CommonParameters.DEVICE, CommonParameters.ANDROID);

                LogUtils.i(TAG + "addMessage json " + map.toString());
                return map;
            }

        };
        requestQueue.add(stringRequest);
    }

}
