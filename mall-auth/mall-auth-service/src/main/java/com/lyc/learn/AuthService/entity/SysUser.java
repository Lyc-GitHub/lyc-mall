package com.lyc.learn.AuthService.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;

    @TableField("real_name")
    private String realName;

    private String email;
    private String mobile;
    private Integer status;          // 1启用 0禁用

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 非数据库字段，用于联查角色（MyBatis-Plus 需自己写 SQL 或使用 @TableField(exist = false)）
    @TableField(exist = false)
    private List<SysRole> roles;
}
