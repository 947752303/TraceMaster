package com.xyz.tracemaster.ui.trace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.xyz.tracemaster.R;
import com.xyz.tracemaster.app.Constant;
import com.xyz.tracemaster.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoryFragment extends Fragment {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private BitmapDescriptor startBD, finishBD;
    public String locate;
    private List latList = new ArrayList();
    private List<LatLng> currentLatList = new ArrayList<LatLng>();
    Polyline mPolyline;//运动轨迹图层
    private MyLocationData locData;
    private int mCurrentDirection = 0;
    private MapStatus.Builder builder;
    private float mCurrentZoom = 20f; //默认地图缩放比例值

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        findID(root);
        // 开启搜索
        setHasOptionsMenu(true);
        // 初始化Map
        initMapView();
        initLocationData();
        initShowLine();
        return root;
    }

    private void initLocationData() {
        locate = PreferencesUtils.getString(Constant.LOC, null);
        locate = locate.substring(1, locate.length() - 1);
        System.out.println("HistoryFragment:" + locate);

        latList = Arrays.asList(locate.split(","));
        System.out.println("HistoryFragment" + latList);

        for (int i = 0; i < latList.size(); i = i + 2) {
            String latitude = latList.get(i).toString().substring(10);
            String longitude = latList.get(i + 1).toString().substring(11);
            LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            currentLatList.add(location);
        }
    }

    private void initShowLine() {
        locateAndZoom(currentLatList.get(0));

        MarkerOptions oStart = new MarkerOptions();// 地图标记覆盖物参数配置类
        MarkerOptions oFinish = new MarkerOptions();// 地图标记覆盖物参数配置类
        OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(currentLatList);
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);

        oStart.icon(startBD);// 设置覆盖物图片
        oFinish.icon(finishBD);// 设置覆盖物图片

        oStart.position(currentLatList.get(0));// 覆盖物位置点，第一个点为起点
        oFinish.position(currentLatList.get(currentLatList.size() - 1));
        mBaiduMap.addOverlay(oStart); // 在地图上添加此图层
        mBaiduMap.addOverlay(oFinish); // 在地图上添加此图层
    }


    private void findID(View root) {
        mMapView = root.findViewById(R.id.bmap);
        //起点图标
        startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_startpoint);
        //终点图标
        finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_finishpoint);
    }


    private void initMapView() {
        // 初始化百度SDK
        SDKInitializer.setAgreePrivacy(requireContext().getApplicationContext(), true);
        SDKInitializer.initialize(requireContext().getApplicationContext());

        mBaiduMap = mMapView.getMap();
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void locateAndZoom(LatLng ll) {
        locData = new MyLocationData.Builder().accuracy(0)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection)
                .latitude(ll.latitude)
                .longitude(ll.longitude)
                .build();
        mBaiduMap.setMyLocationData(locData);

        builder = new MapStatus.Builder();
        builder.target(ll).zoom(mCurrentZoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

}
