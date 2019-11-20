package com.meiduohui.groupbuying.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import com.lidroid.xutils.util.LogUtils;

import java.util.List;

public class GPSUtils {

    @SuppressLint("StaticFieldLeak")
    private static GPSUtils instance;
    private Context mContext;
    private LocationManager locationManager;

    private GPSUtils(Context context) {
        this.mContext = context;
    }

    public static GPSUtils getInstance(Context context) {
        if (instance == null) {
            instance = new GPSUtils(context);
        }
        return instance;
    }

    /**
     * 获取经纬度
     *
     * @return
     */
    private String TAG = "GPSUtils: ";
    @SuppressLint("MissingPermission")
    public int getLngAndLat(OnLocationResultListener onLocationResultListener) {

        mOnLocationListener = onLocationResultListener;
        LogUtils.i(TAG + " getLocation 0");

        String locationProvider = null;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            LogUtils.i(TAG + " getLocation GPS_PROVIDER");
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            LogUtils.i(TAG + " getLocation NETWORK_PROVIDER");
        } else {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(i);
            LogUtils.i(TAG + " getLocation ACTION_LOCATION_SOURCE_SETTINGS");

            return 0;
        }
        LogUtils.i(TAG + " getLocation 1");

        LogUtils.i(TAG + " getLocation locationProvider " + locationProvider);

        if (!NetworkUtils.isConnected(mContext) && locationProvider.equals(LocationManager.NETWORK_PROVIDER)){
            LogUtils.i(TAG + " getLocation isConnected " + NetworkUtils.isConnected(mContext));
            mOnLocationListener.onLocationFaile();
            return 0;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            LogUtils.i(TAG + " getLocation location != null");
            //不为空,显示地理位置经纬度
            if (mOnLocationListener != null) {
                LogUtils.i(TAG + " getLocation mOnLocationListener != null");
                mOnLocationListener.onLocationResult(location);
            }
        }

        try {
            //监视地理位置变化
            locationManager.requestLocationUpdates(locationProvider, 60000, 1, locationListener);
        } catch (Exception e) {
            LogUtils.i(TAG + " getLocation e " + e.toString());
            mOnLocationListener.onLocationFaile();
            return 0;
        }

        return 1;
    }


    public LocationListener locationListener = new LocationListener() {

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {

        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {

        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (mOnLocationListener != null) {
                mOnLocationListener.OnLocationChange(location);
            }
        }
    };

    public void removeListener() {
        locationManager.removeUpdates(locationListener);
    }

    private OnLocationResultListener mOnLocationListener;

    public interface OnLocationResultListener {

        void onLocationResult(Location location);

        void OnLocationChange(Location location);

        void onLocationFaile();
    }

}
