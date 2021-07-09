package com.my.blog.website.enums;

public enum TipExceptionEnums {

    /**
     * 用户注册
     */
    USERNAME_IS_NOT_NULL(1001, "用户名不能为空"),
    PASSWORD_IS_VALID(1002, "密码不能为空且不能小于6个字符，大于10个字符"),
    TWICE_PASSWORD_IS_NOT_SAME(1003, "两次密码输入不一致"),
    EMAIL_IS_VALID(1004, "邮箱格式不正确"),
    USERNAME_IS_JUST_ONE(1005, "用户名已被注册"),
    EMAIL_IS_JUST_ONE(1005, "邮箱已被注册"),


    /**
     * 用户id
     */
    USER_ID_IS_VALID(2001, "用户不存在");


    private int code;
    private String msg;

    TipExceptionEnums(int code, String msg) {
        this.code = code;
        this.msg = msg;
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
