package com.lyc.learn.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("product_detail")
public class ProductDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    /**
     * 商品详情内容（富文本HTML）
     */
    @TableField(value = "content")
    // LONGTEXT 类型无需特殊处理，MyBatis-Plus 默认支持 String 映射
    private String content;
    
}
