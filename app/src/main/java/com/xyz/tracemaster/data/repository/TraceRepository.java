package com.xyz.tracemaster.data.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.xyz.tracemaster.TraceDatabase;
import com.xyz.tracemaster.data.bean.Trace;
import com.xyz.tracemaster.data.dao.TraceDao;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * <pre>
 *     author : xyz
 *     time   : 2022/4/19 22:14
 * </pre>
 */
public class TraceRepository {
    private TraceDao traceDao;

    public TraceRepository(Context context) {
        TraceDatabase traceDatabase = TraceDatabase.getDatabase(context);
        traceDao = traceDatabase.getTraceDao();
    }

    //插入接口
    public void insertTrace(Trace... traces) {
        new InsertAsyncTask(traceDao).execute(traces);
    }

    //清空全表接口
    public void deleteAllTrace(Void... voids) {
        new DeleteAllAsyncTask(traceDao).execute();
    }

    // 删除某个记录接口
    public void deleteTrace(int traceId) {
        new DeleteTraceAsyncTask(traceDao).execute(traceId);
    }

    //查询全表(LiveData)接口
    public LiveData<List<Trace>> getAllTraceLive(Void... voids) {
        LiveData<List<Trace>> traceListLiveData = null;
        AsyncTask<Void, Void, LiveData<List<Trace>>> execute = new SelectLiveDataAsyncTask(traceDao).execute();
        try {
            traceListLiveData = execute.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return traceListLiveData;

    }


    //插入AsyncTask
    static class InsertAsyncTask extends AsyncTask<Trace, Void, Void> {
        private TraceDao traceDao;

        public InsertAsyncTask(TraceDao traceDao) {
            this.traceDao = traceDao;
        }

        @Override
        protected Void doInBackground(Trace... traces) {
            traceDao.insertTrace(traces);
            return null;
        }
    }

    //清空全表AsyncTask
    static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private TraceDao traceDao;

        public DeleteAllAsyncTask(TraceDao traceDao) {
            this.traceDao = traceDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            traceDao.deleteAllTrace();
            return null;
        }
    }

    //查询(LiveData)AsyncTask
    static class SelectLiveDataAsyncTask extends AsyncTask<Void, Void, LiveData<List<Trace>>> {
        private TraceDao traceDao;

        public SelectLiveDataAsyncTask(TraceDao traceDao) {
            this.traceDao = traceDao;
        }

        @Override
        protected LiveData<List<Trace>> doInBackground(Void... voids) {
            return traceDao.getAllTraceLive();
        }
    }

    // 删除某个记录AsyncTask
    static class DeleteTraceAsyncTask extends AsyncTask<Integer, Void, Void> {
        private TraceDao traceDao;

        public DeleteTraceAsyncTask(TraceDao traceDao) {
            this.traceDao = traceDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            traceDao.deleteTrace(integers[0]);
            return null;
        }
    }

}
