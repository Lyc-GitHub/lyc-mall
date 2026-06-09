package com.lyc.learn.common.vo;

import lombok.Data;

@Data
public class ProductInfoVo {
    // 商品标识
    Long id;
    // 商品名称
    String productName;
    // 图片
    String productImage;
    // 描述
    String summary;
}
