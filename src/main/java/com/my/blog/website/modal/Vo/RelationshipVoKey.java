package com.my.blog.website.modal.Vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 
 */
@Data
@TableName("t_relationships")
public class RelationshipVoKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer cid;
    private Integer mid;
}