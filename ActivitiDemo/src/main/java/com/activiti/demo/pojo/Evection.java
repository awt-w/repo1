package com.activiti.demo.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * create 2020-12-08 10:31
 */
public class Evection implements Serializable {
//    id
    private Integer id;
//    出差名称
    private String evectionName;
//    天数
    private Double days;
//    开始时间
    private Date startTime;
//    结束时间
    private Date endTime;
//    目的地
    private String destination;
//    原因
    private String reason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEvectionName() {
        return evectionName;
    }

    public void setEvectionName(String evectionName) {
        this.evectionName = evectionName;
    }

    public Double getDays() {
        return days;
    }

    public void setDays(Double days) {
        this.days = days;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
