package com.my.blog.website.modal.Vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 
 */
@Data
@TableName("t_options")
public class OptionVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String value;
    private String description;
}