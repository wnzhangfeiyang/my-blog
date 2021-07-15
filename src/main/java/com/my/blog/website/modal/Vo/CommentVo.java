package com.my.blog.website.modal.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 
 */
@Data
@TableName("t_comments")
public class CommentVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "coid", type = IdType.AUTO)
    private Integer coid;
    private Integer cid;
    private Integer created;
    private String author;
    private Integer authorId;
    private Integer ownerId;
    private String mail;
    private String url;
    private String ip;
    private String agent;
    private String content;
    private String type;
    private String status;
    private Integer parent;
}