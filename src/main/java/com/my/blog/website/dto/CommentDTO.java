package com.my.blog.website.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentDTO implements Serializable {
    private Integer cid;
    private Integer coid;
    private String author;
    private String mail;
    private String url;
    private String text;
    private String _csrf_token;
}
