package com.lyc.learn.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("product_image")
public class ProductImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("image_url")
    private String imageUrl;

    /**
     * 图片类型：1-轮播图，2-详情图
     */
    @TableField("image_type")
    private Integer imageType;

    /**
     * 排序顺序，数值越小越靠前
     */
    @TableField("sort_order")
    private Integer sortOrder;
}
