package com.meiduohui.groupbuying.UI.activitys.mine.setting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.tu.loadingdialog.LoadingDailog;
import com.githang.statusbar.StatusBarCompat;
import com.lidroid.xutils.util.LogUtils;
import com.meiduohui.groupbuying.R;
import com.meiduohui.groupbuying.utils.GPSUtils;
import com.meiduohui.groupbuying.utils.ToastUtil;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopAddressActivity extends AppCompatActivity implements GPSUtils.OnLocationResultListener, AMapLocationListener {

    private String TAG = "ShopIntroActivity: ";
    private Context mContext;
    private String mCounty = "";
    private String mAddress = "";
    private String mLatitude = "";
    private String mLongitude = "";
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @BindView(R.id.tv_address1)
    TextView mTvAddress1;
    @BindView(R.id.et_address2)
    EditText mEtAddress2;

    private final int UPDATA_ADDRESS = 66;      // 更新地址
    private final int GET_LOCATION = 67;         // 获取地址
    private final int STOP_LOCATION = 68;         // 获取地址

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case GET_LOCATION:
                    getLocation();
                    break;

                case STOP_LOCATION:
                    mLoadingDailog.dismiss();
                    break;

                case UPDATA_ADDRESS:
                    mTvAddress1.setText(mAddress);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_address);
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
        Intent intent = getIntent();
        if (intent != null) {
            String address = intent.getStringExtra("mAddress");
            mEtAddress2.setText(address);
        }
        initLocation();

        getLocation();
    }

    private void initLocation() {

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(8000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //        //启动定位
        //        mLocationClient.startLocation();
    }

    @OnClick({R.id.iv_back, R.id.iv_alocation, R.id.tv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_alocation:
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
                } else {
                    mLoadingDailog.show();
                    mHandler.sendEmptyMessageDelayed(GET_LOCATION,500);
                    mHandler.sendEmptyMessageDelayed(STOP_LOCATION,5000);
                }

                break;

            case R.id.tv_save:

                String address = mEtAddress2.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("county", mCounty);
                intent.putExtra("address", address);
                intent.putExtra("Latitude", mLatitude);
                intent.putExtra("Longitude", mLongitude);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private static final int ACCESS_FINE_LOCATION = 1000;
    //获取Location
    private void getLocation() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
        } else {
//            int result = GPSUtils.getInstance(mContext).getLngAndLat(this);
//            if (result==0) {
//                mLoadingDailog.dismiss();
//            }

            //启动定位
            mLocationClient.startLocation();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LogUtils.i(TAG + " getLocation SUCCESS");
                    mLoadingDailog.show();
//                    int result = GPSUtils.getInstance(mContext).getLngAndLat(this);
//                    if (result==0) {
//                        mLoadingDailog.dismiss();
//                    }

                    //启动定位
                    mLocationClient.startLocation();

                }else {
                    LogUtils.i(TAG + " getLocation FAILED");
                    ToastUtil.show(mContext,"您已取消授权，定位无法使用");

                    mTvAddress1.setText("定位失败");
                }
                break;

        }
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        mLoadingDailog.dismiss();

        mLatitude = aMapLocation.getLatitude() + "";
        mLongitude = aMapLocation.getLongitude() + "";

        mAddress = aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict();
        mCounty = aMapLocation.getDistrict();
        mHandler.sendEmptyMessage(UPDATA_ADDRESS);
    }

    @Override
    public void onLocationResult(Location location) {
        LogUtils.i(TAG + " getLocation onLocationResult()");
        mLoadingDailog.dismiss();
        getAddress(location);
    }

    @Override
    public void OnLocationChange(Location location) {
        LogUtils.i(TAG + " getLocation OnLocationChange()");
        mLoadingDailog.dismiss();
        getAddress(location);
    }

    @Override
    public void onLocationFailed() {
        mLoadingDailog.dismiss();
        mTvAddress1.setText( "定位失败");
    }

    private void getAddress(Location location){

        String address = "";
        String county = "";
        LogUtils.i(TAG + " getLocation address1 " + "纬度：" + location.getLatitude() + " 经度：" + location.getLongitude());

        List<Address> addList = null;
        Geocoder ge = new Geocoder(mContext);
        try {
            addList = ge.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {

            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address ad = addList.get(i);
                address = ad.getAdminArea() + ad.getLocality() + ad.getSubLocality();
//                address = ad.getLocality() + ad.getSubLocality();
                county = ad.getSubLocality();

                mLatitude = ad.getLatitude() + "";
                mLongitude = ad.getLongitude() + "";
            }
        }

        mAddress = address;
        mCounty = county;
        mHandler.sendEmptyMessage(UPDATA_ADDRESS);

        LogUtils.i(TAG + " getLocation address2 " + address);
    }

}
