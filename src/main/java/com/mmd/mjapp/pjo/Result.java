package com.mmd.mjapp.pjo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {
    private int code = 1; //1成功 0失败
    private String message;
    private Object data;

    public Result() {}

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result success() {
        this.code = 1;
        this.message = "success";
        return this;
    }

    public Result success(Object data) {
        this.code = 1;
        this.message = "success";
        this.data = data;
        return this;
    }

    public Result fail() {
        this.code = 0;
        this.message = "error";
        return this;
    }

    public Result fail(String message) {
        this.code = 0;
        this.message = message;
        return this;
    }

    /**
     * 需要前端进行处理的。
     * @param message
     * @return
     */
    public Result bootbox(String message) {
        this.code = 2;
        this.message = message;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
