package com.lyc.learn.auth.sdk.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyc.learn.auth.sdk.context.SecurityUser;
import com.lyc.learn.auth.sdk.entity.PermissionInfo;
import com.lyc.learn.auth.sdk.entity.RoleInfo;
import com.lyc.learn.auth.sdk.entity.vo.UserInfoResultVo;
import com.lyc.learn.auth.sdk.client.AuthServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    AuthServiceInterface authServiceInterface;
    
    @Value("${auth.sso.interceptorsb:}")
    private String interceptorsb;
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 检查是否是白名单路径，如果是则直接放行
        String requestUri = request.getRequestURI();
        if (isWhiteListPath(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        UserInfoResultVo resultVo = authServiceInterface.getUserInfoByToken();
        if (resultVo.getCode() != UserInfoResultVo.SUCCESS_CODE) {
            if (resultVo.getCode() == UserInfoResultVo.NORMAL_ERROR_CODE) {
                sendErrorResponse(response, resultVo.getCode(), resultVo.getErrMsg());
            } else {
                // TODO 其他错误需要跳到登录页面，先暂时返回错误信息
                sendErrorResponse(response, resultVo.getCode(), resultVo.getErrMsg());
            }
        } else {
            // 如果响应中包含新的token，将其设置到响应头中
            if (resultVo.getNewToken() != null && !resultVo.getNewToken().isEmpty()) {
                response.setHeader("x-access-token", resultVo.getNewToken());
            }
            
            // 将用户信息设置到上下文
            List<GrantedAuthority> authorities = new ArrayList<>();
            List<RoleInfo> roleInfos = resultVo.getRoleInfos();
            if (roleInfos != null && !roleInfos.isEmpty()) {
                for (RoleInfo roleInfo : roleInfos) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + roleInfo.getRoleCode()));
                }
            }
            List<PermissionInfo> permissionInfos = resultVo.getPermissionInfos();
            if (permissionInfos != null && !permissionInfos.isEmpty()) {
                for (PermissionInfo permissionInfo : permissionInfos) {
                    authorities.add(new SimpleGrantedAuthority(permissionInfo.getPermCode()));
                }
            }
            SecurityUser securityUser = new SecurityUser(resultVo.getUserInfo(), roleInfos, permissionInfos, authorities);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        // 直接在此处判断路径是否在白名单中
        return isWhiteListPath(path);
    }

    /**
     * 判断请求路径是否在白名单中
     */
    private boolean isWhiteListPath(String requestUri) {
        if (interceptorsb == null || interceptorsb.isEmpty()) {
            return false;
        }
        String[] whiteList = interceptorsb.split(",");
        for (String path : whiteList) {
            if (pathMatcher.match(path.trim(), requestUri)) {
                return true;
            }
        }
        return false;
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> error = new HashMap<>();
        error.put("code", status);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
