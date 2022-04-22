package com.xyz.tracemaster.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.xyz.tracemaster.R;
import com.xyz.tracemaster.data.bean.Trace;
import com.xyz.tracemaster.data.viewModel.TraceViewModel;
import com.xyz.tracemaster.service.LocationApplication;
import com.xyz.tracemaster.service.LocationService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Button start, finish;
    private TextView info;
    private RelativeLayout progressBarRl;
    private BitmapDescriptor startBD, finishBD;

    private LocationService locationService;
    private TraceViewModel traceViewModel;
    private MyLocationData locData;
    private MapStatus.Builder builder;
    private float mCurrentZoom = 18f; //默认地图缩放比例值
    private int mCurrentDirection = 0;

    private LatLng last = new LatLng(0, 0);//上一个定位点
    private LatLng currentLat;
    private LatLng firstLat;
    private String locationChinese;
    private boolean isFirstLat = true;
    private boolean startRecord = false;
    //开始记录后的位置点集合
    private List<LatLng> currentLatList = new ArrayList<LatLng>();
    Polyline mPolyline;//运动轨迹图层

    private Long startTime;
    private Long endTime;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        findID(root);
        // 开启搜索
        setHasOptionsMenu(true);
        // 初始化ViewModel
        traceViewModel = new ViewModelProvider(requireActivity()).get(TraceViewModel.class);
        // 初始化定位服务
        initLocationService();
        // 初始化Map
        initMapView();
        // 初始化方位角
//        initOrientation();
        // 初始化监听按钮
        initBeginningAndEndListener();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_bar, menu);
    }


    private void findID(View root) {
        mMapView = root.findViewById(R.id.bmapView);
        start = root.findViewById(R.id.buttonStart);
        finish = root.findViewById(R.id.buttonFinish);
        info = root.findViewById(R.id.info);
        progressBarRl = root.findViewById(R.id.progressBarRl);
        //起点图标
        startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_startpoint);
        //终点图标
        finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_finishpoint);
    }

    private void initBeginningAndEndListener() {
        // 开始按钮监听
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationService != null) {
                    mBaiduMap.clear();
                    //标记起点图层位置
                    MarkerOptions oStart = new MarkerOptions();// 地图标记覆盖物参数配置类
                    oStart.position(last);// 覆盖物位置点，第一个点为起点
                    oStart.icon(startBD);// 设置覆盖物图片
                    mBaiduMap.addOverlay(oStart); // 在地图上添加此图层

                    startTime = Calendar.getInstance().getTimeInMillis();
                    startRecord = true;
                }
            }
        });
        // 结束按钮监听
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationService != null) {
                    MarkerOptions oFinish = new MarkerOptions();// 地图标记覆盖物参数配置类
                    if (currentLatList.size() > 1) {
                        oFinish.position(currentLatList.get(currentLatList.size() - 1));
                    } else {
                        oFinish.position(last);
                    }

                    oFinish.icon(finishBD);// 设置覆盖物图片
                    mBaiduMap.addOverlay(oFinish); // 在地图上添加此图层

                    startRecord = false;
                    isFirstLat = true;
                    endTime = Calendar.getInstance().getTimeInMillis();

                    // 将追踪记录存入数据库
                    Trace trace = new Trace(currentLatList.toString(), startTime, endTime, locationChinese);
                    traceViewModel.insertTrace(trace);
                    currentLatList.clear();

                }
            }
        });
    }

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }
            initBeginningAndEndListener();
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {

                // 获取到位置隐藏文字
                progressBarRl.setVisibility(View.GONE);

                // 第一个定位点
                if (isFirstLat) {
                    firstLat = new LatLng(location.getLatitude(), location.getLongitude());
                    locationChinese = location.getAddrStr().substring(2);
//                    System.out.println("locationChinese" + locationChinese);
                    // 路径列表里加入第一个点
                    currentLatList.add(firstLat);
                    //显示当前定位点，缩放地图
                    locateAndZoom(location, firstLat);
                    last = firstLat;
                    Log.d("firstLatLngLocate", firstLat.toString());
                    isFirstLat = false;
                }
                // 获取第二个点
                currentLat = new LatLng(location.getLatitude(), location.getLongitude());
                locateAndZoom(location, currentLat);

                //sdk回调gps位置的频率是1秒1个，位置点太近动态画在图上不是很明显，可以设置点之间距离大于为6米才添加到集合中
                if (DistanceUtil.getDistance(last, currentLat) < 0) {
//                    System.out.println("小于6米");
                    return;
                }
                if (startRecord) {
                    //如果要运动完成后画整个轨迹，位置点都在这个集合中
                    currentLatList.add(currentLat);
                }

                last = currentLat;

                //清除上一次轨迹，避免重叠绘画
//                mMapView.getMap().clear();
                if (currentLatList.size() > 1) {
                    //将points集合中的点绘制轨迹线条图层，显示在地图上
                    OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(currentLatList);
                    mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
                }


                //从第二个点开始
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                Toast.makeText(requireContext().getApplicationContext(), "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因", Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                Toast.makeText(requireContext().getApplicationContext(), "网络不同导致定位失败，请检查网络是否通畅", Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Toast.makeText(requireContext().getApplicationContext(), "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void initMapView() {
        // 初始化百度SDK
        SDKInitializer.setAgreePrivacy(requireContext().getApplicationContext(), true);
        SDKInitializer.initialize(requireContext().getApplicationContext());

        mBaiduMap = mMapView.getMap();
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        /**添加地图缩放状态变化监听，当手动放大或缩小地图时，拿到缩放后的比例，然后获取到下次定位，
         *  给地图重新设置缩放比例，否则地图会重新回到默认的mCurrentZoom缩放比例
         */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                mCurrentZoom = arg0.zoom;//获取手指缩放地图后的值
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {
            }
        });
        //设置定位图标类型为跟随模式
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.FOLLOWING, true, null));
    }

    private void initLocationService() {
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用
        //可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService = ((LocationApplication) requireActivity().getApplication()).locationService;
        locationService.registerListener(mListener); //注册监听
        Intent intent = requireActivity().getIntent();
        int type = intent.getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        // 显示正在搜索文字
        progressBarRl.setVisibility(View.VISIBLE);
        info.setText(R.string.search_gps);

        //启动定位服务,此时百度地图开始每隔一定时间(setScanSpan)就发起一次定位请求
        locationService.start();
    }

    private void locateAndZoom(final BDLocation location, LatLng ll) {
        locData = new MyLocationData.Builder().accuracy(0)
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(mCurrentDirection)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
        mBaiduMap.setMyLocationData(locData);

        builder = new MapStatus.Builder();
        builder.target(ll).zoom(mCurrentZoom);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }
}