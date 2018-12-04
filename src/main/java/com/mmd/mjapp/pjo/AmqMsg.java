package com.mmd.mjapp.pjo;


import com.mmd.mjapp.utils.PublicUtil;

import java.io.Serializable;

/**
 * 消息队列实体
 */
public class AmqMsg implements Serializable{

    private String code;
    private String name;
    private String message;
    private String date = PublicUtil.getDateTime();

    public AmqMsg() {
    }

    public AmqMsg(AmqType amqType) {
        this.code = amqType.code;
        this.name = amqType.name;
    }

    public AmqMsg(AmqType amqType, String message) {
        this.code = amqType.code;
        this.name = amqType.name;
        this.message = message;
    }
    

    public AmqMsg(String code, String name, String message) {
        this.code = code;
        this.name = name;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
