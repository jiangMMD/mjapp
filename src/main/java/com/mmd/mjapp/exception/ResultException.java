package com.mmd.mjapp.exception;

/**
 * 自定义异常处理器
 */
public class ResultException extends RuntimeException {

    public ResultException() {

    }

    public ResultException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
