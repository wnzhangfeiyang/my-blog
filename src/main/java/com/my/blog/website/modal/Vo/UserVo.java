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
@TableName("t_users")
public class UserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "uid", type = IdType.AUTO)
    private Integer uid;
    private String username;
    private String password;
    private String email;
    private String homeUrl;
    private String screenName;
    private Integer created;
    private Integer activated;
    private Integer logged;
    private String groupName;

}