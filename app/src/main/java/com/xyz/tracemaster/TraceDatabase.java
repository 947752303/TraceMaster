package com.xyz.tracemaster;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.xyz.tracemaster.data.bean.Trace;
import com.xyz.tracemaster.data.dao.TraceDao;

/**
 * <pre>
 *     author : xyz
 *     time   : 2022/4/19 11:43
 * </pre>
 */
@Database(entities = {Trace.class}, version = 1, exportSchema = false)
public abstract class TraceDatabase extends RoomDatabase {
    private static TraceDatabase INSTANCE;

    // 单例模式
    public static synchronized TraceDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TraceDatabase.class, "trace_Database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract TraceDao getTraceDao();
}
