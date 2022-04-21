package com.xyz.tracemaster.data.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.xyz.tracemaster.data.bean.Trace;
import com.xyz.tracemaster.data.repository.TraceRepository;

import java.util.List;

public class TraceViewModel extends AndroidViewModel {
    private final TraceRepository traceRepository;

    public TraceViewModel(@NonNull Application application) {
        super(application);
        traceRepository = new TraceRepository(application);
    }


    public LiveData<List<Trace>> getAllTraceLive() {
        return traceRepository.getAllTraceLive();
    }

    public void insertTrace(Trace... traces) {
        traceRepository.insertTrace(traces);
    }

    public void deleteAllTrace(Void... voids) {
        traceRepository.deleteAllTrace();
    }

    public void deleteTrace(int traceId) {
        traceRepository.deleteTrace(traceId);
    }
}