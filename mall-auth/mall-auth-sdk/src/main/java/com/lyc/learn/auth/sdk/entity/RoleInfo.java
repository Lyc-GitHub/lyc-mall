package com.lyc.learn.auth.sdk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleInfo {
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private Integer status;
}
