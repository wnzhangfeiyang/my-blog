package com.my.blog.website.kafklademo.demo;

import lombok.Data;

import java.util.Date;

/**
 * @author zfy
 **/
@Data
public class Message {


    private Long id;

    private String msg;

    private Date sendTime;
}
