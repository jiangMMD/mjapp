package com.mmd.mjapp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.crypto.Data;

/**
 * 线下商家评论
 */

public class OfflineEvaluate {

    private Long id;
    @JsonIgnore
    private Long bid; //线下商家订单ID
    private Long pid; //产品ID
    @JsonIgnore
    private Long merId; //线下商家ID
    private Long uid; //用户ID
    private Long swId; //评论词ID
    private Long starlevel; //星级1-5
    private Data evdate; //评论时间
    @JsonIgnore
    private Long state; //评语标志

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBid() {
        return bid;
    }

    public void setBid(Long bid) {
        this.bid = bid;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getMerId() {
        return merId;
    }

    public void setMerId(Long merId) {
        this.merId = merId;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getSwId() {
        return swId;
    }

    public void setSwId(Long swId) {
        this.swId = swId;
    }

    public Long getStarlevel() {
        return starlevel;
    }

    public void setStarlevel(Long starlevel) {
        this.starlevel = starlevel;
    }

    public Data getEvdate() {
        return evdate;
    }

    public void setEvdate(Data evdate) {
        this.evdate = evdate;
    }

    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
    }
}
