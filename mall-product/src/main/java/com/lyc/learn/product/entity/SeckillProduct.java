package com.lyc.learn.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("seckill_product")
public class SeckillProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String productName;

    private BigDecimal originalPrice;

    private BigDecimal seckillPrice;

    private Integer stockCount;

    private Integer initStock;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;   // 0-禁用 1-启用 2-已结束

    @Version  // 乐观锁版本号字段，MyBatis-Plus 自动处理
    private Integer version;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    private String summary;
    
    private String mainImgUrl;
}
