package com.mmd.mjapp.model;

import lombok.Data;

/**
 * 操作实体类，主要用于验证码操作
 */
@Data
public class OperInfo {

    private String phone;
    private String yzmToken;
    private String yzmCode;
    private String token; //操作token(在进行发送验证码时检验);
    private String channelid;  //百度推送channelid
    private String deviceid; //设备id
    private String source; //1:ios 设备

}
