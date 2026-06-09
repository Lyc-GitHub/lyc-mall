package com.lyc.learn.secKill.controller;

import com.lyc.learn.common.vo.CommonResult;
import com.lyc.learn.secKill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecKillController {
    
    @Autowired
    SecKillService secKillService;
    
    @PostMapping("/secKillProduct/{productId}")
    public CommonResult secKillProduct(@PathVariable long productId) {
        return secKillService.secKillProduct(productId);
    }
}
