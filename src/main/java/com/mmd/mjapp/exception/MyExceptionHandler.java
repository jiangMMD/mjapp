package com.mmd.mjapp.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    @ExceptionHandler(value = {Exception.class})
    public void exceptionHandler(Exception e) throws Exception{
        //记录错误信息
        e.printStackTrace();
        logger.error(e.getMessage());
        throw new Exception(e);
    }
}
