package com.mmd.mjapp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Merchant {

  private Long merId;
  @JsonIgnore
  private String merType;
  private String merShortname;
  private String merEname; //英文简称
  private String merName;
  private String merDesc;
  private String merAddress;
  private String merHomeimg;
  private String merHomeicon;
  private String merLinkman;
  private String merLinkphone;
  private String merWeixin;
  private String merQq;
  @JsonIgnore
  private Long hotval;
  @JsonIgnore
  private Long hits;
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

  public String getMerShortname() {
    return merShortname;
  }

  public void setMerShortname(String merShortname) {
    this.merShortname = merShortname;
  }

  public String getMerName() {
    return merName;
  }

  public void setMerName(String merName) {
    this.merName = merName;
  }

  public String getMerEname() {
    return merEname;
  }

  public void setMerEname(String merEname) {
    this.merEname = merEname;
  }

  public String getMerDesc() {
    return merDesc;
  }

  public void setMerDesc(String merDesc) {
    this.merDesc = merDesc;
  }

  public String getMerAddress() {
    return merAddress;
  }

  public void setMerAddress(String merAddress) {
    this.merAddress = merAddress;
  }

  public String getMerHomeimg() {
    return merHomeimg;
  }

  public void setMerHomeimg(String merHomeimg) {
    this.merHomeimg = merHomeimg;
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

  public Long getHotval() {
    return hotval;
  }

  public void setHotval(Long hotval) {
    this.hotval = hotval;
  }

  public Long getHits() {
    return hits;
  }

  public void setHits(Long hits) {
    this.hits = hits;
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
}
