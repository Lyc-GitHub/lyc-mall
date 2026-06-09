package com.lyc.learn.auth.sdk.config;

import com.lyc.learn.auth.sdk.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class AuthSecurityConfig {

    @Value("${auth.sso.interceptorsb:}")
    private String interceptorsb;
    
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if (interceptorsb != null && !interceptorsb.isEmpty()) {
            List<String> whiteUrls = new ArrayList<>(Arrays.asList(interceptorsb.split(",")));
            AntPathRequestMatcher[] requestMatchers = new AntPathRequestMatcher[whiteUrls.size()];
            int i = 0;
            for (String whiteUrl : whiteUrls) {
                AntPathRequestMatcher matcher = new AntPathRequestMatcher(whiteUrl);
                requestMatchers[i++] = matcher;
            }
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态session
                    .authorizeHttpRequests(authz -> authz.requestMatchers(requestMatchers).permitAll() // 配置放行请求
                            .anyRequest().authenticated())
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 触发spring security登录验证前，先设置登录用户信息
        } else {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态session
                    .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 触发spring security登录验证前，先设置登录用户信息
        }
        return http.build();
    }
}
