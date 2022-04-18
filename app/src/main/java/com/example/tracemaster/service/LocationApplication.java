package com.example.tracemaster.service;

import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;

/**
 * <pre>
 *     author : wyx
 *     time   : 2022/4/16 16:21
 * </pre>
 */
public class LocationApplication extends Application {
    public LocationService locationService;
    public Vibrator mVibrator;

    /***
     * 初始化定位sdk，建议在Application中创建
     */
    @Override
    public void onCreate() {
        super.onCreate();
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        SDKInitializer.initialize(getApplicationContext());

    }
}
