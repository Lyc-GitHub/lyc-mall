package com.lyc.learn.common.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SecKillOrderVo {
    private Long id;

    private String orderNo;

    private Long userId;

    private Long productId;

    private BigDecimal payAmount;

    private Integer status;   // 0-待支付 1-已支付 2-已取消 3-已超时

    private LocalDateTime createTime;

    private LocalDateTime payTime;

    private LocalDateTime cancelTime;

    private BigDecimal productAmount;

    private BigDecimal shippingFee;

    private Integer quantity;

    private Long addressId;

    private String paymentChannel;
}
