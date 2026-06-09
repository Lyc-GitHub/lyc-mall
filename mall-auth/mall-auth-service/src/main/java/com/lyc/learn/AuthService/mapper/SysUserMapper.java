package com.lyc.learn.AuthService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lyc.learn.AuthService.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户及其角色和权限（用于登录后构建权限集合）
     */
    SysUser selectUserWithRolesAndPerms(@Param("username") String username);

    /**
     * 根据用户名查询用户及其角色和权限（用于登录后构建权限集合）
     */
    SysUser selectUserWithRolesAndPermsWithId(@Param("userId") Long userId);
}
