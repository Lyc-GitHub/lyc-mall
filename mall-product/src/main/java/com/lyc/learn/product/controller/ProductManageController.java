package com.lyc.learn.product.controller;

import com.lyc.learn.product.entity.SeckillProduct;
import com.lyc.learn.common.vo.CommonResult;
import com.lyc.learn.product.vo.AddSeckillProductVo;
import com.lyc.learn.product.vo.SeckillProductDetailVo;
import com.lyc.learn.product.vo.SeckillProductListVo;
import com.lyc.learn.product.service.OssClientService;
import com.lyc.learn.product.service.ProductManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manage")
public class ProductManageController {
    
    @Autowired
    OssClientService ossClientService;
    
    @Autowired
    ProductManageService productManageService;
    
    @GetMapping("/getAddProductPolicy")
    @PreAuthorize("hasRole('SECKILL_ADMIN')")
    public CommonResult getAddProductPolicy() {
        return CommonResult.success(ossClientService.getSts());
    }

    @PostMapping("/addSeckillProduct")
    @PreAuthorize("hasRole('SECKILL_ADMIN')")
    public CommonResult addSeckillProduct(@RequestBody AddSeckillProductVo seckillProduct) {
        SeckillProduct product = productManageService.addSeckillProduct(seckillProduct);
        return CommonResult.success("添加成功", product);
    }
    
    @GetMapping("/getSeckillProductList")
    public CommonResult getSeckillProductList() {
        List<SeckillProductListVo> result = productManageService.getSeckillProductList();
        return CommonResult.success(result);
    }
    
    @GetMapping("/getSeckillProductDetail")
    public CommonResult getSeckillProductDetail(Long productId) {
        SeckillProductDetailVo result = productManageService.getSeckillProductDetail(productId);
        return CommonResult.success(result);
    }
}
