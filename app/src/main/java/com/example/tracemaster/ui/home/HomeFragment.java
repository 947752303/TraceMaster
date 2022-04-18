package com.example.tracemaster.ui.home;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.tracemaster.R;
import com.example.tracemaster.service.LocationApplication;
import com.example.tracemaster.service.LocationService;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;

    private Button start, finish;
    private TextView info;
    private RelativeLayout progressBarRl;

    private LocationService locationService;
    private HomeViewModel homeViewModel;
    float mCurrentZoom = 18f; //默认地图缩放比例值
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private MyLocationData locData;
    private int mCurrentDirection = 0;
    private MapStatus.Builder builder;
    double lastX;

    LatLng last = new LatLng(0, 0);//上一个定位点
    private List<LatLng> points = new ArrayList<LatLng>();//位置点集合
    boolean isFirstLoc = true; // 是否首次定位
    Polyline mPolyline;//运动轨迹图层


    //起点图标
    BitmapDescriptor startBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_startpoint);
    //终点图标
    BitmapDescriptor finishBD = BitmapDescriptorFactory.fromResource(R.drawable.ic_me_history_finishpoint);


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        findID(root);
        //开启搜索
        setHasOptionsMenu(true);
        //初始化ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        // 初始化Map
        initMapView();
        // 初始化监听按钮
        initBeginningAndEndListener();
        // 初始化定位服务
        initLocationService();
        return root;
    }


//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//        double x = sensorEvent.values[SensorManager.DATA_X];
//
//        if (Math.abs(x - lastX) > 1.0) {
//            mCurrentDirection = (int) x;
//
//            if (isFirstLoc) {
//                lastX = x;
//                return;
//            }
//
//            locData = new MyLocationData.Builder().accuracy(0)
//                    // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
//            mBaiduMap.setMyLocationData(locData);
//        }
//        lastX = x;
//
//    }
//
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_bar, menu);
    }


    private void findID(View root) {
        mMapView = (MapView) root.findViewById(R.id.bmapView);
        start = root.findViewById(R.id.buttonStart);
        finish = root.findViewById(R.id.buttonFinish);
        info = root.findViewById(R.id.info);
        progressBarRl = root.findViewById(R.id.progressBarRl);
    }

    private void initBeginningAndEndListener() {
        // 开始按钮监听
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (locationService != null && !locationService.serviceIsStarted()) {
                    locationService.start();
                    progressBarRl.setVisibility(View.VISIBLE);
                    info.setText("GPS信号搜索中，请稍后...");
                    mBaiduMap.clear();
                }
            }
        });

        // 结束按钮监听
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (locationService != null && locationService.serviceIsStarted()) {
                    locationService.stop();

                    progressBarRl.setVisibility(View.GONE);

                    if (isFirstLoc) {
                        points.clear();
                        last = new LatLng(0, 0);
                        return;
                    }

                    MarkerOptions oFinish = new MarkerOptions();// 地图标记覆盖物参数配置类
                    oFinish.position(points.get(points.size() - 1));
                    oFinish.icon(finishBD);// 设置覆盖物图片
                    mBaiduMap.addOverlay(oFinish); // 在地图上添加此图层

                    //复位
                    points.clear();
                    last = new LatLng(0, 0);
                    isFirstLoc = true;

                }
            }
        });
    }

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

        Intent intent = getActivity().getIntent();
        int type = intent.getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }

        //启动定位服务,此时百度地图开始每隔一定时间(setScanSpan)就发起一次定位请求
//        locationService.start();
//        // 定位一次
//        locationService.stop();
    }

    private void locateAndZoom(final BDLocation location, LatLng ll) {
        mCurrentLat = location.getLatitude();
        mCurrentLon = location.getLongitude();
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

    /**
     * 首次定位很重要，选一个精度相对较高的起始点
     * 注意：如果一直显示gps信号弱，说明过滤的标准过高了，
     * 你可以将location.getRadius()>25中的过滤半径调大，比如>40，
     * 并且将连续5个点之间的距离DistanceUtil.getDistance(last, ll ) > 5也调大一点，比如>10，
     * 这里不是固定死的，你可以根据你的需求调整，如果你的轨迹刚开始效果不是很好，你可以将半径调小，两点之间距离也调小，
     * gps的精度半径一般是10-50米
     */
    private LatLng getMostAccuracyLocation(BDLocation location) {

        if (location.getRadius() > 40) {//gps位置精度大于40米的点直接弃用
            return null;
        }

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

        if (DistanceUtil.getDistance(last, ll) > 10) {
            last = ll;
            points.clear();//有任意连续两点位置大于10，重新取点
            return null;
        }
        points.add(ll);
        last = ll;
        //有5个连续的点之间的距离小于10，认为gps已稳定，以最新的点为起始点
        if (points.size() >= 5) {
            points.clear();
            return ll;
        }
        return null;
    }

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null) {
                return;
            }
            System.out.println("getLocType:" + location.getLocType());
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                info.setText("GPS信号弱，请稍后...");

                if (isFirstLoc) {//首次定位
                    //第一个点很重要，决定了轨迹的效果，gps刚开始返回的一些点精度不高，尽量选一个精度相对较高的起始点
//                    LatLng ll = getMostAccuracyLocation(location);
                    LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                    System.out.println("位置：" + ll.latitude + "," + ll.longitude);

                    isFirstLoc = false;
                    points.add(ll);//加入集合
                    last = ll;

                    //显示当前定位点，缩放地图
                    locateAndZoom(location, ll);

                    //标记起点图层位置
                    MarkerOptions oStart = new MarkerOptions();// 地图标记覆盖物参数配置类
                    oStart.position(points.get(0));// 覆盖物位置点，第一个点为起点
                    oStart.icon(startBD);// 设置覆盖物图片
                    mBaiduMap.addOverlay(oStart); // 在地图上添加此图层

                    progressBarRl.setVisibility(View.GONE);

                    return;//画轨迹最少得2个点，首地定位到这里就可以返回了
                }

                //从第二个点开始
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                System.out.println("位置：" + ll.latitude + "\n" + ll.longitude);
                //sdk回调gps位置的频率是1秒1个，位置点太近动态画在图上不是很明显，可以设置点之间距离大于为5米才添加到集合中
                if (DistanceUtil.getDistance(last, ll) < 5) {
                    return;
                }

                points.add(ll);//如果要运动完成后画整个轨迹，位置点都在这个集合中

                last = ll;

                //显示当前定位点，缩放地图
                locateAndZoom(location, ll);

                //清除上一次轨迹，避免重叠绘画
                mMapView.getMap().clear();

                //起始点图层也会被清除，重新绘画
                MarkerOptions oStart = new MarkerOptions();
                oStart.position(points.get(0));
                oStart.icon(startBD);
                mBaiduMap.addOverlay(oStart);

                //将points集合中的点绘制轨迹线条图层，显示在地图上
                OverlayOptions ooPolyline = new PolylineOptions().width(13).color(0xAAFF0000).points(points);
                mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                Toast.makeText(requireContext().getApplicationContext(), "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因", Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                Toast.makeText(requireContext().getApplicationContext(), "网络不同导致定位失败，请检查网络是否通畅", Toast.LENGTH_LONG).show();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                Toast.makeText(requireContext().getApplicationContext(), "无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机", Toast.LENGTH_LONG).show();
            }

//            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
//                MyLocationData locData = new MyLocationData.Builder()
//                        .accuracy(location.getRadius())
//                        // 此处设置开发者获取到的方向信息，顺时针0-360
//                        .direction(100).latitude(location.getLatitude())
//                        .longitude(location.getLongitude()).build();
//
//                mBaiduMap.setMyLocationData(locData);
//                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//
//                builder.target(ll).zoom(mCurrentZoom);
//                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//
//                注意这里只接受gps点，需要在室外定位。
//
//                pointList.add(pnt);
//                PolylineOptions polyline = new PolylineOptions().width(10).color(Color.RED).points(pointList);
//                Overlay track = mBaiduMap.addOverlay(polyline);
//                if (last_track != null) { //每增加一个点就新绘制一条路径, 然后删除上一个点对应的路径
//                    last_track.remove();
//                }
//                last_track = track;
//
//            }
        }
    };

//    @Override
//    public void onStop() {
//        if (locationService != null) {
//            locationService.unregisterListener(mListener); //注销掉监听
//            locationService.stop(); //停止定位服务
//        }
//        super.onStop();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (locationService != null) {
//            locationService.stop(); //停止定位服务
//            mBaiduMap.setMyLocationEnabled(false);  // 关闭定位图层
//            mMapView.onDestroy();
//            mMapView = null;
//        }
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        mMapView.onResume(); //在activity执行onResume时执行mMapView.onResume()
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mMapView.onPause(); //在activity执行onPause时执行mMapView.onPause ()
//    }


}