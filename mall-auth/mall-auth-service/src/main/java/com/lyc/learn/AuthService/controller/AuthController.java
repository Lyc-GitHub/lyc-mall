package com.lyc.learn.AuthService.controller;

import com.lyc.learn.AuthService.entity.SysPermission;
import com.lyc.learn.AuthService.entity.SysRole;
import com.lyc.learn.AuthService.entity.SysUser;
import com.lyc.learn.AuthService.service.UserService;
import com.lyc.learn.AuthService.utils.JsonUtil;
import com.lyc.learn.AuthService.utils.JwtUtil;
import com.lyc.learn.AuthService.vo.LoginVo;
import com.lyc.learn.auth.sdk.entity.PermissionInfo;
import com.lyc.learn.auth.sdk.entity.RoleInfo;
import com.lyc.learn.auth.sdk.entity.UserInfo;
import com.lyc.learn.auth.sdk.entity.vo.UserInfoResultVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    UserService userService;

    @PostMapping("/loginInByPassword")
    public Map<String, Object> loginInByPassword(@RequestBody LoginVo vo) {
        Map<String, Object> result = new HashMap<>();
        
        SysUser user = null;
        try {
            user = userService.login(vo.getUsername(), vo.getRawPassword());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
            return result;
        }

        // 生成 JWT payload（存放必要信息，不宜过多）
        String token = generateToken(user.getId(), user.getUsername());
        result.put("data", token);
        result.put("code", 200);
        return result;
    }

    @GetMapping("/getUserInfoByToken")
    public UserInfoResultVo getUserInfoByToken(HttpServletRequest request, HttpServletResponse response) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return UserInfoResultVo.error(HttpStatus.UNAUTHORIZED.value(), "请先登录");
        }
        try {
            authorization = authorization.substring(7);
            Claims claims = jwtUtil.parseToken(authorization);
            Long userId = Long.parseLong(claims.getSubject());
            SysUser user = userService.getSysUserByUserId(userId);
            if (user == null) {
                return UserInfoResultVo.error("获取用户信息失败");
            } else {
                String userJsonStr = JsonUtil.toJson(user);
                UserInfo userInfo = JsonUtil.toObject(userJsonStr, UserInfo.class);
                String newToken = null;
                // 如果token快过期了，颁发一个新的token给前端
                if (jwtUtil.tokenIsApproachExpire(authorization)) {
                    newToken = generateToken(userInfo.getId(), user.getUsername());
                }
                String roleJson = JsonUtil.toJson(user.getRoles());
                List<RoleInfo> roleInfos = JsonUtil.toList(roleJson, RoleInfo.class);
                List<PermissionInfo> permissionInfos = new ArrayList<>();
                for (SysRole role : user.getRoles()) {
                    List<SysPermission> permissions = role.getPermissions();
                    String perJson = JsonUtil.toJson(role.getPermissions());
                    List<PermissionInfo> perInfos = JsonUtil.toList(perJson, PermissionInfo.class);
                    permissionInfos.addAll(perInfos);
                }
                return UserInfoResultVo.success(userInfo, roleInfos, permissionInfos, newToken);
            }
        } catch (ExpiredJwtException e) {
            // token 过期：返回 401，不继续执行
            return UserInfoResultVo.error(HttpStatus.UNAUTHORIZED.value(), "Token expired");
        } catch (MalformedJwtException e) {
            // token 格式错误：返回 401
            return UserInfoResultVo.error(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
        } catch (Exception e) {
            // 其他错误
            return UserInfoResultVo.error(HttpStatus.UNAUTHORIZED.value(), "Authentication failed");
        }
    }
    
    private String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return jwtUtil.generateToken(claims);
    }
}
