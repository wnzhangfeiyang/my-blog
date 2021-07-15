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
@TableName("t_metas")
public class MetaVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "mid", type = IdType.AUTO)
    private Integer mid;
    private String name;
    private String slug;
    private String type;
    private String description;
    private Integer sort;
    private Integer parent;
}