package com.lyc.learn.auth.sdk.entity.vo;

import com.lyc.learn.auth.sdk.entity.PermissionInfo;
import com.lyc.learn.auth.sdk.entity.RoleInfo;
import com.lyc.learn.auth.sdk.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResultVo {
    private int code;
    private String errMsg;
    private UserInfo userInfo;
    private List<RoleInfo> roleInfos;
    private List<PermissionInfo> permissionInfos;
    private String newToken; // 新增字段：用于存储刷新后的token
    
    public static int NORMAL_ERROR_CODE = 500;
    
    public static int SUCCESS_CODE = 200;
    
    public static UserInfoResultVo error(String errMsg) {
        return error(NORMAL_ERROR_CODE, errMsg);
    }
    
    public static UserInfoResultVo error(int code , String errMsg) {
        UserInfoResultVo userInfoResultVo = new UserInfoResultVo();
        userInfoResultVo.setCode(code);
        userInfoResultVo.setErrMsg(errMsg);
        return userInfoResultVo;
    }
    
    public static UserInfoResultVo success(UserInfo userInfo, List<RoleInfo> roleInfos, List<PermissionInfo> permissionInfos, String newToken) {
        UserInfoResultVo userInfoResultVo = new UserInfoResultVo();
        userInfoResultVo.setCode(SUCCESS_CODE);
        userInfoResultVo.setUserInfo(userInfo);
        userInfoResultVo.setRoleInfos(roleInfos);
        userInfoResultVo.setPermissionInfos(permissionInfos);
        userInfoResultVo.setNewToken(newToken);
        return userInfoResultVo;
    }
    
    // 保持向后兼容的方法
    public static UserInfoResultVo success(UserInfo userInfo, List<RoleInfo> roleInfos, List<PermissionInfo> permissionInfos) {
        return success(userInfo, roleInfos, permissionInfos, null);
    }
}
