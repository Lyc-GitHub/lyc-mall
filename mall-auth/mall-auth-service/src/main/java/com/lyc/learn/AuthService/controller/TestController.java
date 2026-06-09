package com.lyc.learn.AuthService.controller;

import com.lyc.learn.auth.sdk.entity.UserInfo;
import com.lyc.learn.auth.sdk.utils.UserContextUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/echo/{message}")
    public String echo(@PathVariable String message) {
        UserInfo user = UserContextUtil.getCurrentUser();
        String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return "服务器接收到message：" + message + "，当前时间：" + nowDate + "，当前登录用户为：" + user.getRealName();
    }
    
    @GetMapping("/checkServiceStatue")
    public String checkServiceStatue() {
        String nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return "服务器正常工作，当前时间：" + nowDate;
    }
    
    @GetMapping("/test1")
    @PreAuthorize("hasRole('ADMIN')")
    public String test1() {
        return "这是一个测试权限的接口，访问该接口，要求用户必须具有分配权限";
    }
}
