package com.lyc.learn.secKillOrder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("seckill_order")
public class SeckillOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long productId;

    private BigDecimal payAmount;

    private Integer status;   // 0-待支付 1-已支付 2-已取消 3-已超时

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime payTime;

    private LocalDateTime cancelTime;

    private BigDecimal productAmount;

    private BigDecimal shippingFee;

    private Integer quantity;

    private Long addressId;
    
    private String paymentChannel;
}
