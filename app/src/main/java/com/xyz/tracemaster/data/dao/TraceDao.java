package com.xyz.tracemaster.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.xyz.tracemaster.data.bean.Trace;

import java.util.List;

@Dao
public interface TraceDao {
    // 插入
    @Insert
    void insertTrace(Trace... traces);

    //清空表
    @Query("DELETE FROM Trace")
    void deleteAllTrace();

    //查询全表
    @Query("SELECT * FROM Trace")
    LiveData<List<Trace>> getAllTraceLive();

    // 删除某个记录
    @Query("DELETE FROM Trace WHERE traceId = :traceId")
    void deleteTrace(int traceId);
}
