package com.lyc.learn.AuthService.service;

import com.lyc.learn.AuthService.entity.SysPermission;
import com.lyc.learn.AuthService.entity.SysRole;
import com.lyc.learn.AuthService.mapper.SysUserMapper;
import com.lyc.learn.AuthService.utils.JsonUtil;
import com.lyc.learn.AuthService.utils.JwtUtil;
import com.lyc.learn.AuthService.utils.PasswordEncoderUtil;
import com.lyc.learn.auth.sdk.context.SecurityUser;
import com.lyc.learn.AuthService.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private PasswordEncoderUtil passwordEncoder;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    final String userCacheKeyPrefix = "user:info:";

    public SysUser login(String username, String password) {
        SysUser user = userMapper.selectUserWithRolesAndPerms(username);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 存入用户信息到redis
        String key = userCacheKeyPrefix + user.getId();
        redisTemplate.opsForValue().set(key, user, jwtUtil.getExpire(), TimeUnit.MILLISECONDS);
        
        return user;
    }
    
    public SysUser getSysUserByUserId(Long userId) {
        String key = userCacheKeyPrefix + userId;
        Map map = (Map) redisTemplate.opsForValue().get(key);
        SysUser user = null;
        if (map != null) {
            user = JsonUtil.map2obj(map, SysUser.class);
        } else {
            user = userMapper.selectUserWithRolesAndPermsWithId(userId);
        }
        return user;
    }
}
