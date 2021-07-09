package com.my.blog.website.exception;

import com.my.blog.website.enums.TipExceptionEnums;

public class TipException extends RuntimeException {
    private int code;
    private String msg;

    public TipException() {
    }

    public TipException(String message) {
        super(message);
    }

    public TipException(String message, Throwable cause) {
        super(message, cause);
    }

    public TipException(Throwable cause) {
        super(cause);
    }

    public TipException(int code, String message){
        this.code = code;
        this.msg = message;
    }

    public TipException(TipExceptionEnums exceptionEnums){
        super(exceptionEnums.getMsg());
        this.code = exceptionEnums.getCode();
        this.msg = exceptionEnums.getMsg();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
