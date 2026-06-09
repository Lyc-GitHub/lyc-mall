package com.lyc.learn.auth.sdk.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String email;
    private String mobile;
    private Integer status;
}
