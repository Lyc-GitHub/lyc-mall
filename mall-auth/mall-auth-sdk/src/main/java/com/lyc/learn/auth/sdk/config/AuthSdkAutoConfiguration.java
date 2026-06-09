package com.lyc.learn.auth.sdk.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
@ComponentScan({"com.lyc.learn.auth.sdk.**"})
@EnableFeignClients("com.lyc.learn.auth.sdk.client")
public class AuthSdkAutoConfiguration {
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 1. 从当前请求上下文中获取原始的 HttpServletRequest 对象
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes == null) {
                    // 当不在 Web 请求上下文中（比如单元测试）时，直接返回
                    return;
                }
                HttpServletRequest request = attributes.getRequest();

                // 2. 从原始请求中提取 Authorization 头的值
                String authHeader = request.getHeader("Authorization");

                // 3. 如果提取到，就将其设置到 Feign 即将发出的新请求的 Header 中
                if (authHeader != null && !authHeader.isEmpty()) {
                    // 关键一步：将请求头复制到 Feign 的请求模板中
                    template.header("Authorization", authHeader);
                }
            }
        };
    }
}
