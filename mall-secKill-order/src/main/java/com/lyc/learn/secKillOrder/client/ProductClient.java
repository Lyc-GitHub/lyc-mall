package com.lyc.learn.secKillOrder.client;

import com.lyc.learn.common.dto.SecKillProductVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mall-product")
public interface ProductClient {
    
    @GetMapping("/product/service/getProductInfo")
    SecKillProductVo getProductInfo(@RequestParam(name = "productId") Long productId);

    @PutMapping("/product/service/decrementProductStock")
    int decrementProductStock(@RequestParam(name = "productId") Long productId);
}
