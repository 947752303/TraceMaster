package com.xyz.tracemaster.ui.trace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.xyz.tracemaster.R;
import com.xyz.tracemaster.data.viewModel.TraceViewModel;

public class HistoryFragment extends Fragment {
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private TraceViewModel traceViewModel;
    private String locate;
    private BitmapDescriptor startBD, finishBD;
    private TraceRecycleViewAdapter traceRecycleViewAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        findID(root);
        // 开启搜索
        setHasOptionsMenu(true);
        // 初始化ViewModel
        traceViewModel = new ViewModelProvider(requireActivity()).get(TraceViewModel.class);
        // 初始化Map
        initMapView();

        traceRecycleViewAdapter = new TraceRecycleViewAdapter(new TraceRecycleViewAdapter.Interface() {
            @Override
            public void onWork(View view, String loc) {
                locate = loc;
            }
        });

        System.out.println("locate:" + locate);

        return root;
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


}
