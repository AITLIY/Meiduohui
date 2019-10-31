package com.meiduohui.groupbuying.UI.activitys.publishCoupon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.githang.statusbar.StatusBarCompat;
import com.lidroid.xutils.util.LogUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.UI.activitys.PlusImageActivity;
import com.meiduohui.groupbuying.UI.views.MyGridView;
import com.meiduohui.groupbuying.UI.views.SmartHintTextView;
import com.meiduohui.groupbuying.adapter.AddImgAdapter;
import com.meiduohui.groupbuying.application.GlobalParameterApplication;
import com.meiduohui.groupbuying.bean.UserBean;
import com.meiduohui.groupbuying.commons.CommonParameters;
import com.meiduohui.groupbuying.utils.PictureSelectorConfig;
import com.meiduohui.groupbuying.utils.TimeUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    @BindView(R.id.ll_add_video)
    LinearLayout mLlAddVideo;
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
    @BindView(R.id.ed_number)
    EditText mEdNumber;
    @BindView(R.id.ed_price)
    EditText mEdPrice;
    @BindView(R.id.ed_yxq)
    EditText mEdYxq;

    private ArrayList<String> mPicList = new ArrayList<>(); //上传的图片凭证的数据源
    private AddImgAdapter mAddImgAdapter; //展示上传的图片的适配器

    private long mStartTime;
    private long mEndTime;

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


                    break;

                case LOAD_DATA1_FAILE:

                    ToastUtil.show(mContext, "发布失败");
                    break;

                case NET_ERROR:

                    ToastUtil.show(mContext, "网络异常,请稍后重试");
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

        initData();
        initGridView();
    }

    private void initData() {
        mContext = this;
        requestQueue = GlobalParameterApplication.getInstance().getRequestQueue();
        mUserBean = GlobalParameterApplication.getInstance().getUserInfo();

    }

    //初始化展示上传图片的GridView
    private void initGridView() {
        mAddImgAdapter = new AddImgAdapter(mContext, mPicList);
        mGvImg.setAdapter(mAddImgAdapter);
        mGvImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPicList.remove(position);
                mAddImgAdapter.notifyDataSetChanged();
            }
        });
        mGvImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == parent.getChildCount() - 1) {

                    //如果“增加按钮形状的”图片的位置是最后一张，且添加了的图片的数量不超过8张，才能点击
                    if (mPicList.size() == CommonParameters.MAX_SELECT_PIC_NUM) { // 最多添加8张图片

                        viewPluImg(position);
                    } else {

                        selectPic(CommonParameters.MAX_SELECT_PIC_NUM - mPicList.size()); // 添加凭证图片
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

    // 打开相册或者照相机选择凭证图片
    private void selectPic(int maxTotal) {
        PictureSelectorConfig.initMultiConfig(this, maxTotal);
        //        PictureSelectorConfig.initSingleConfig(this);
    }

    // 处理选择的照片的地址
    private void refreshAdapter(List<LocalMedia> picList) {
        for (LocalMedia localMedia : picList) {
            //被压缩后的图片路径
            if (localMedia.isCompressed()) {
                String compressPath = localMedia.getCompressPath(); //压缩后的图片路径
                mPicList.add(compressPath); //把图片添加到将要上传的图片数组中
                mAddImgAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调

                    List<LocalMedia> localMedia =  PictureSelector.obtainMultipleResult(data);

                    if (localMedia.size() > 0) {
                        mLlAddImg.setVisibility(View.GONE);
                        mLlAddVideo.setVisibility(View.GONE);
                        mGvImg.setVisibility(View.VISIBLE);
                    }

                    refreshAdapter(localMedia);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    break;
            }
        }

    }

    @OnClick({R.id.iv_back, R.id.ll_add_img, R.id.rv_add_video, R.id.ll_cat, R.id.ll_start_time, R.id.ll_end_time, R.id.ll_type, R.id.tv_affirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.ll_add_img:
                selectPic(CommonParameters.MAX_SELECT_PIC_NUM - mPicList.size()); // 添加凭证图片
                break;

            case R.id.ll_cat:

                break;

            case R.id.rv_add_video:

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


                break;
        }
    }

    private void setTvStartTime() {

        TimePickerView pvTime = new TimePickerView.Builder(ComboActivity.this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                String time = TimeUtils.DateToString(date, "yyyy-MM-dd");
                mTvStartTime.setText(time);
                mStartTime = date.getTime();
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
                mEndTime = date.getTime();
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
        options1Items.add("会员券");

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
                        break;

                    case "折扣券":
                        mType = 2;
                        break;

                    case "会员券":
                        mType = 3;
                        break;
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



}
