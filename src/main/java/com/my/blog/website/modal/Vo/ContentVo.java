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
@TableName("t_contents")
public class ContentVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "cid", type = IdType.AUTO)
    private Integer cid;
    private String title;
    private String slug;
    private Integer created;
    private Integer modified;
    /**
     * 内容文字
     */
    private String content;
    private Integer authorId;
    private String type;
    private String status;
    private String tags;
    private String categories;
    private Integer hits;
    private Integer commentsNum;
    private Boolean allowComment;
    private Boolean allowPing;
    private Boolean allowFeed;
}