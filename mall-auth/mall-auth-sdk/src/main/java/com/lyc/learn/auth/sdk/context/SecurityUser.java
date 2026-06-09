package com.lyc.learn.auth.sdk.context;

import com.lyc.learn.auth.sdk.entity.PermissionInfo;
import com.lyc.learn.auth.sdk.entity.RoleInfo;
import com.lyc.learn.auth.sdk.entity.UserInfo;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
public class SecurityUser implements UserDetails {
    private UserInfo user;
    private List<RoleInfo> roles;
    private List<PermissionInfo> permissions;
    private List<GrantedAuthority> authorities;
    
    public SecurityUser(UserInfo user, List<RoleInfo> roles, List<PermissionInfo> permissions, List<GrantedAuthority> authorities) {
        this.user = user;
        this.roles = roles;
        this.permissions = permissions;
        this.authorities = authorities;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == 1;
    }
    
    public Long getUserId() {
        return user.getId();
    }
}
