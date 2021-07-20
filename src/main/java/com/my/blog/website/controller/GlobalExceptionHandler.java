package com.my.blog.website.controller;

import com.google.common.collect.Maps;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Bo.RestResponseBo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Created by BlueT on 2017/3/4.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = TipException.class)
    public RestResponseBo tipException(TipException e) {
        LOGGER.error("数据验证异常:e={}",e.getMessage());
        e.printStackTrace();
        int code = e.getCode();
        String message = e.getMessage();
        return RestResponseBo.fail(code, message);
    }


    @ExceptionHandler(value = Exception.class)
    public RestResponseBo exception(Exception e){
        LOGGER.info("数据验证异常:e={}",e.getMessage());
        e.printStackTrace();
        return RestResponseBo.fail(e);
    }
}
