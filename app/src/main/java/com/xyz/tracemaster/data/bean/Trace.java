package com.xyz.tracemaster.data.bean;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * <pre>
 *     author : xyz
 *     time   : 2022/4/19 20:44
 * </pre>
 */
@Entity
public class Trace {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer traceId;    //按时间顺序记录一次追踪
    private String latLngList;  //位置列表
    private Long startTime;     //开始时间
    private Long endTime;       //结束时间
    private String location;    //开始时候的位置中文

    public Trace(String latLngList, Long startTime, Long endTime, String location) {
        this.latLngList = latLngList;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    @NonNull
    public Integer getTraceId() {
        return traceId;
    }

    public void setTraceId(@NonNull Integer traceId) {
        this.traceId = traceId;
    }

    public String getLatLngList() {
        return latLngList;
    }

    public void setLatLngList(String latLngList) {
        this.latLngList = latLngList;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
