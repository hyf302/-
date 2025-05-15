package com.scenic.entity;

import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

@Data
@TableName("sys_user")
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String realName;
    private String avatar;
    private Integer gender;
    @TableField("member_level")
    private Integer memberLevel;
    private Integer points;
    private Integer status;
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("update_time")
    private LocalDateTime updateTime;

    // 会员等级常量
    public static final int MEMBER_LEVEL_NORMAL = 0;  // 普通会员
    public static final int MEMBER_LEVEL_SILVER = 1;  // 银卡会员
    public static final int MEMBER_LEVEL_GOLD = 2;    // 金卡会员
    public static final int MEMBER_LEVEL_DIAMOND = 3; // 钻石会员
} 