package com.lyc.learn.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillProductListVo {
    Long id;
    String productName;
    String summary;
    BigDecimal originalPrice;
    BigDecimal seckillPrice;
    BigDecimal discount;
    String startTime;
    String endTime;
    String status;
    String image;
}
