package com.mmd.mjapp.exception;

import com.mmd.mjapp.pjo.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class MyExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    @ExceptionHandler(value = {ResultException.class})
    public Result exceptionHandler(ResultException e) {
        //进行事物回滚
        logger.error(e.getMessage());
        //记录错误信息
        return new Result().fail(e.getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    public void exceptionHandler(Exception e) throws Exception{
        //记录错误信息
        e.printStackTrace();
        logger.error(e.getMessage());
        throw new Exception(e);
    }

}
