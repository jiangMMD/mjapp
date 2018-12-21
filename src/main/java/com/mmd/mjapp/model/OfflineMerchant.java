package com.mmd.mjapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * 线下商家
 */
public class OfflineMerchant {

    private Long merId;
    @JsonIgnore
    private String merType;
    private String merName;
    private String merDesc;
    private String merLand;
    private String merAddress;
    private String merRange;
    private String merTime;
    private String merHomeimg;
    private String merInsideimg;
    private String merHomeicon;
    private String merLinkman;
    private String merLinkphone;
    private String merWeixin;
    private String merQq;

    @JsonIgnore
    private Date crtdate;
    @JsonIgnore
    private String crtuser;
    @JsonIgnore
    private Date update;
    @JsonIgnore
    private String upduser;
    @JsonIgnore
    private String recsts;

    private Integer averageScore;

    private Integer score;

    private Integer totalComment;

    private Integer goodComment;

    private Integer middlingComment;

    private Integer badComment;

    private String endTime;

    private String businessLicense;
    @JsonIgnore
    private Integer hotval;
    @JsonIgnore
    private Integer hits;
    @JsonIgnore
    private Integer isHot;

    public String getMerTime() {
        return merTime;
    }

    public void setMerTime(String merTime) {
        this.merTime = merTime;
    }

    public Integer getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Integer averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(Integer totalComment) {
        this.totalComment = totalComment;
    }

    public Integer getGoodComment() {
        return goodComment;
    }

    public void setGoodComment(Integer goodComment) {
        this.goodComment = goodComment;
    }

    public Integer getMiddlingComment() {
        return middlingComment;
    }

    public void setMiddlingComment(Integer middlingComment) {
        this.middlingComment = middlingComment;
    }

    public Integer getBadComment() {
        return badComment;
    }

    public void setBadComment(Integer badComment) {
        this.badComment = badComment;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public Long getMerId() {
        return merId;
    }

    public void setMerId(Long merId) {
        this.merId = merId;
    }

    public String getMerType() {
        return merType;
    }

    public void setMerType(String merType) {
        this.merType = merType;
    }

    public String getMerName() {
        return merName;
    }

    public void setMerName(String merName) {
        this.merName = merName;
    }

    public String getMerDesc() {
        return merDesc;
    }

    public void setMerDesc(String merDesc) {
        this.merDesc = merDesc;
    }

    public String getMerLand() {
        return merLand;
    }

    public void setMerLand(String merLand) {
        this.merLand = merLand;
    }

    public String getMerAddress() {
        return merAddress;
    }

    public void setMerAddress(String merAddress) {
        this.merAddress = merAddress;
    }

    public String getMerRange() {
        return merRange;
    }

    public void setMerRange(String merRange) {
        this.merRange = merRange;
    }

    public String getMerHomeimg() {
        return merHomeimg;
    }

    public void setMerHomeimg(String merHomeimg) {
        this.merHomeimg = merHomeimg;
    }

    public String getMerInsideimg() {
        return merInsideimg;
    }

    public void setMerInsideimg(String merInsideimg) {
        this.merInsideimg = merInsideimg;
    }

    public String getMerHomeicon() {
        return merHomeicon;
    }

    public void setMerHomeicon(String merHomeicon) {
        this.merHomeicon = merHomeicon;
    }

    public String getMerLinkman() {
        return merLinkman;
    }

    public void setMerLinkman(String merLinkman) {
        this.merLinkman = merLinkman;
    }

    public String getMerLinkphone() {
        return merLinkphone;
    }

    public void setMerLinkphone(String merLinkphone) {
        this.merLinkphone = merLinkphone;
    }

    public String getMerWeixin() {
        return merWeixin;
    }

    public void setMerWeixin(String merWeixin) {
        this.merWeixin = merWeixin;
    }

    public String getMerQq() {
        return merQq;
    }

    public void setMerQq(String merQq) {
        this.merQq = merQq;
    }

    public Date getCrtdate() {
        return crtdate;
    }

    public void setCrtdate(Date crtdate) {
        this.crtdate = crtdate;
    }

    public String getCrtuser() {
        return crtuser;
    }

    public void setCrtuser(String crtuser) {
        this.crtuser = crtuser;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    public String getUpduser() {
        return upduser;
    }

    public void setUpduser(String upduser) {
        this.upduser = upduser;
    }

    public String getRecsts() {
        return recsts;
    }

    public void setRecsts(String recsts) {
        this.recsts = recsts;
    }

    public Integer getHotval() {
        return hotval;
    }

    public void setHotval(Integer hotval) {
        this.hotval = hotval;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public Integer getIsHot() {
        return isHot;
    }

    public void setIsHot(Integer isHot) {
        this.isHot = isHot;
    }
}
