package com.lyc.learn.product.net;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lyc.learn.common.dto.SecKillProductVo;
import com.lyc.learn.common.exception.MallException;
import com.lyc.learn.common.utils.JsonUtil;
import com.lyc.learn.product.entity.SeckillProduct;
import com.lyc.learn.product.mapper.SeckillProductMapper;
import com.lyc.learn.product.service.OssClientService;
import com.lyc.learn.product.service.ProductManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequestMapping("/service")
public class ProductServiceNet {

    @Autowired
    SeckillProductMapper productMapper;

    @Autowired
    OssClientService ossClientService;
    
    @GetMapping("/getProductInfo")
    public SecKillProductVo getProductInfo(@RequestParam(name = "productId") Long productId) {
        SeckillProduct product = productMapper.selectById(productId);
        // 获取图片临时查看权限
        try {
            String url = ossClientService.generatePresignedUrl(product.getMainImgUrl());
            product.setMainImgUrl(url);
        } catch (URISyntaxException e) {
            throw new MallException(e.getMessage());
        }
        return JsonUtil.obj2obj(product, SecKillProductVo.class);
    }
    
    @PutMapping("/decrementProductStock")
    public int decrementProductStock(@RequestParam(name = "productId") Long productId) {
        LambdaUpdateWrapper<SeckillProduct> proUpWrapper = new LambdaUpdateWrapper<>();
        proUpWrapper.setSql("stock_count = stock_count - 1, version = version + 1, update_time = now()")
                .eq(SeckillProduct::getId, productId).gt(SeckillProduct::getStockCount, 0);
        return productMapper.update(proUpWrapper);
    }
    
}
