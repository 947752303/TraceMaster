package com.example.tracemaster;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tracemaster.data.bean.UserInfo;

/**
 * <pre>
 *     author : wyx
 *     time   : 2022/4/19 11:43
 * </pre>
 */
@Database(entities = {UserInfo.class}, version = 1, exportSchema = false)
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

}
