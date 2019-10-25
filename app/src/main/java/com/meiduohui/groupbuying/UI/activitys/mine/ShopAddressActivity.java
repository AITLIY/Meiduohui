package com.meiduohui.groupbuying.UI.activitys.mine;

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

public class ShopAddressActivity extends AppCompatActivity implements GPSUtils.OnLocationResultListener {

    private String TAG = "ShopIntroActivity: ";
    private Context mContext;
    private String mAddress = "";
    private String mLatitude = "";
    private String mLongitude = "";

    @BindView(R.id.tv_address1)
    TextView mTvAddress1;
    @BindView(R.id.et_address2)
    EditText mEtAddress2;

    private final int UPDATA_ADDRESS = 66;      // 更新地址

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

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

        initDate();
    }

    private void initDate() {
        mContext = this;
        Intent intent = getIntent();
        if (intent != null) {
            String address = intent.getStringExtra("mAddress");
            mEtAddress2.setText(address);
        }

        getLocation();
    }


    @OnClick({R.id.iv_back, R.id.iv_alocation, R.id.tv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_alocation:
                getLocation();
                break;

            case R.id.tv_save:

               String shopAddress = mEtAddress2.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("address", shopAddress);
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
            GPSUtils.getInstance(mContext).getLngAndLat(this);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    LogUtils.i(TAG + " getLocation SUCCESS");
                    GPSUtils.getInstance(mContext).getLngAndLat(this);

                }else {
                    LogUtils.i(TAG + " getLocation FAILED");
                    ToastUtil.show(mContext,"您已取消授权，定位无法使用");

                    mTvAddress1.setText("定位失败");
                }
                break;

        }
    }

    @Override
    public void onLocationResult(Location location) {
        LogUtils.i(TAG + " getLocation onLocationResult()");
        getAddress(location);
    }

    @Override
    public void OnLocationChange(Location location) {
        LogUtils.i(TAG + " getLocation OnLocationChange()");
        getAddress(location);
    }

    private void getAddress(Location location){

        String address = "";
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
                address = ad.getAdminArea() + ad.getLocality();
//                address = ad.getLocality(); // 拿到城市

                mLongitude = ad.getLongitude() + "";
                mLatitude = ad.getLatitude() + "";
            }
        }

        mAddress = address;
        mHandler.sendEmptyMessage(UPDATA_ADDRESS);

        LogUtils.i(TAG + " getLocation address2 " + address);
    }


}
