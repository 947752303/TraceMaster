package com.example.tracemaster.data.bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * <pre>
 *     author : wyx
 *     time   : 2022/4/19 11:42
 * </pre>
 */
@Entity
public class UserInfo {
    @PrimaryKey
    @NonNull
    private String uname;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
