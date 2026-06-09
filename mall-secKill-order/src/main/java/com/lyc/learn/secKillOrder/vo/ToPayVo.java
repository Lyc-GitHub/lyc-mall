package com.lyc.learn.secKillOrder.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ToPayVo {
    String paymentMethod;
    Long addressId;
    BigDecimal shippingFee;
    String productName;
}
