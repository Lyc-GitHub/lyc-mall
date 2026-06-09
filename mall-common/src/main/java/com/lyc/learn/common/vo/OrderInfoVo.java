package com.lyc.learn.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderInfoVo {
    // 订单标识
    Long id;
    // 订单号
    String orderNo;
    // 订单状态
    Integer status;
    // 订单状态，前端展示
    String statusStr;
    // 数量
    Integer quantity;
    // 商品价格
    BigDecimal productAmount;
    // 运费
    BigDecimal shippingFee;
    // 支付价格
    BigDecimal payAmount;
    // 创建时间
    LocalDateTime createTime;
    LocalDateTime payTime;
    // 支付渠道
    String paymentChannel;
}
