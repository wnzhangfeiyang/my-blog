package com.my.blog.website.controller;

import com.google.common.collect.Maps;
import com.my.blog.website.exception.TipException;
import com.my.blog.website.modal.Bo.RestResponseBo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;
import java.util.Map;

/**
 * Created by BlueT on 2017/3/4.
 */
@RestControllerAdvice
@Slf4j
@Order
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = TipException.class)
    @ResponseStatus(HttpStatus.OK)
    public RestResponseBo tipException(TipException e) {
        LOGGER.info("数据验证异常:e={}",e.getMessage());
        int code = e.getCode();
        String message = e.getMessage();
        return RestResponseBo.fail(code, message);
    }


    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public RestResponseBo exception(Exception e){
        LOGGER.info("数据验证异常:e={}",e.getMessage());
        return RestResponseBo.fail(e);
    }
}
