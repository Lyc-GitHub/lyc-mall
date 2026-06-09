package com.lyc.learn.auth.sdk.client;

import com.lyc.learn.auth.sdk.entity.vo.UserInfoResultVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "mall-auth")
public interface AuthServiceInterface {
    
    @GetMapping("/auth/login/getUserInfoByToken")
    UserInfoResultVo getUserInfoByToken();
}
