package com.xyz.tracemaster.ui.trace;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xyz.tracemaster.R;
import com.xyz.tracemaster.data.bean.Trace;
import com.xyz.tracemaster.data.viewModel.TraceViewModel;

import java.util.List;

public class TraceFragment extends Fragment {
    private RecyclerView traceRecyclerView;
    public TraceViewModel traceViewModel;
    private TraceRecycleViewAdapter traceRecycleViewAdapter;
    private LiveData<List<Trace>> traceListLiveData;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.fragment_trace, container, false);
        findID(root);
        traceViewModel = new ViewModelProvider(requireActivity()).get(TraceViewModel.class);

        traceRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        traceRecycleViewAdapter = new TraceRecycleViewAdapter(traceViewModel);
        traceRecyclerView.setAdapter(traceRecycleViewAdapter);

        return root;
    }


    private void findID(View root) {
        traceRecyclerView = root.findViewById(R.id.traceRecyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        traceListLiveData = traceViewModel.getAllTraceLive();
        traceListLiveData.observe(requireActivity(), new Observer<List<Trace>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<Trace> traces) {
                traceRecycleViewAdapter.setTraceArrayList(traces);
                traceRecycleViewAdapter.notifyDataSetChanged();
            }
        });
    }
}