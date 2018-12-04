package com.mmd.mjapp.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;


public class User {

  private Long uId;
  private String uName;
  private String uPhone;
  private String uNick;
  private String uSex;
  private String uHeadicon;
  private Long uAge;
  private Long uIntegral; //用户剩余积分
  @JsonIgnore
  private String uPassword;
  private String uIdentity;
  private String uAddress; //用户家庭住址
  private String uMmdNo; //关联的MMD账户
  @JsonIgnore
  private String uMmdPassword; //关联的MMD密码
  private String uMmdMoney;
  @JsonIgnore
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
  private Date uRelevancyDate; //关联的MMD的日期
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")

  private String deviceId; //设备ID
  private String channelId; //推送ID
  @JsonIgnore
  private String token; //最后一次登录的token;
  private String source; //用户来源 1：IOS 2:安卓
  private String uQrcode; //用户二维码信息
  @JsonIgnore
  private Date uCrtdate;
  @JsonIgnore
  private String uCrtuser;
  @JsonIgnore
  private String recsts;

  public Long getuId() {
    return uId;
  }

  public void setuId(Long uId) {
    this.uId = uId;
  }

  public String getuName() {
    return uName;
  }

  public void setuName(String uName) {
    this.uName = uName;
  }

  public String getuPhone() {
    return uPhone;
  }

  public void setuPhone(String uPhone) {
    this.uPhone = uPhone;
  }

  public String getuNick() {
    return uNick;
  }

  public void setuNick(String uNick) {
    this.uNick = uNick;
  }

  public String getuSex() {
    return uSex;
  }

  public void setuSex(String uSex) {
    this.uSex = uSex;
  }

  public Long getuAge() {
    return uAge;
  }

  public void setuAge(Long uAge) {
    this.uAge = uAge;
  }

  public Long getuIntegral() {
    return uIntegral;
  }

  public void setuIntegral(Long uIntegral) {
    this.uIntegral = uIntegral;
  }

  public String getuPassword() {
    return uPassword;
  }

  public void setuPassword(String uPassword) {
    this.uPassword = uPassword;
  }

  public String getuIdentity() {
    return uIdentity;
  }

  public void setuIdentity(String uIdentity) {
    this.uIdentity = uIdentity;
  }

  public String getuAddress() {
    return uAddress;
  }

  public void setuAddress(String uAddress) {
    this.uAddress = uAddress;
  }

  public String getuMmdNo() {
    return uMmdNo;
  }

  public void setuMmdNo(String uMmdNo) {
    this.uMmdNo = uMmdNo;
  }

  public String getuMmdPassword() {
    return uMmdPassword;
  }

  public void setuMmdPassword(String uMmdPassword) {
    this.uMmdPassword = uMmdPassword;
  }

  public String getuMmdMoney() {
    return uMmdMoney;
  }

  public void setuMmdMoney(String uMmdMoney) {
    this.uMmdMoney = uMmdMoney;
  }

  public Date getuRelevancyDate() {
    return uRelevancyDate;
  }

  public void setuRelevancyDate(Date uRelevancyDate) {
    this.uRelevancyDate = uRelevancyDate;
  }

  public Date getuCrtdate() {
    return uCrtdate;
  }

  public void setuCrtdate(Date uCrtdate) {
    this.uCrtdate = uCrtdate;
  }

  public String getuCrtuser() {
    return uCrtuser;
  }

  public void setuCrtuser(String uCrtuser) {
    this.uCrtuser = uCrtuser;
  }

  public String getRecsts() {
    return recsts;
  }

  public void setRecsts(String recsts) {
    this.recsts = recsts;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getuQrcode() {
    return uQrcode;
  }

  public void setuQrcode(String uQrcode) {
    this.uQrcode = uQrcode;
  }

  public String getuHeadicon() {
    return uHeadicon;
  }

  public void setuHeadicon(String uHeadicon) {
    this.uHeadicon = uHeadicon;
  }

  @Override
  public String toString() {
    return "User{" +
            "uId=" + uId +
            ", uName='" + uName + '\'' +
            ", uPhone='" + uPhone + '\'' +
            ", uNick='" + uNick + '\'' +
            ", uSex='" + uSex + '\'' +
            ", uAge=" + uAge +
            ", uIntegral=" + uIntegral +
            ", uPassword='" + uPassword + '\'' +
            ", uIdentity='" + uIdentity + '\'' +
            ", uAddress='" + uAddress + '\'' +
            ", uMmdNo='" + uMmdNo + '\'' +
            ", uMmdPassword='" + uMmdPassword + '\'' +
            ", uMmdMoney='" + uMmdMoney + '\'' +
            ", uRelevancyDate=" + uRelevancyDate +
            ", deviceId='" + deviceId + '\'' +
            ", channelId='" + channelId + '\'' +
            ", token='" + token + '\'' +
            ", source='" + source + '\'' +
            ", uQrcode='" + uQrcode + '\'' +
            ", uCrtdate=" + uCrtdate +
            ", uCrtuser='" + uCrtuser + '\'' +
            ", recsts='" + recsts + '\'' +
            '}';
  }
}
