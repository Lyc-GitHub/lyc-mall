package com.lyc.learn.auth.sdk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionInfo {
    private Long id;
    private String permCode;
    private String permName;
    private Long parentId;
    private Integer type;      // 1目录 2菜单 3按钮
    private String path;
    private String icon;
    private Integer sortOrder;
    private Integer status;
}
