package com.lyc.learn.auth.sdk.utils;

import com.lyc.learn.auth.sdk.context.SecurityUser;
import com.lyc.learn.auth.sdk.entity.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContextUtil {

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    public static UserInfo getCurrentUser() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof SecurityUser) {
            SecurityUser securityUser = (SecurityUser) principal;
            return securityUser.getUser();
        }
        return null;
    }
    
    public static Long getUserId() {
        UserInfo user = getCurrentUser();
        return  user == null ? null : user.getId();
    }
}
