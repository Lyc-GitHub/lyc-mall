package com.lyc.learn.product.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SeckillProductDetailVo {
    private Long id;
    String productName;
    String summary;
    String description;
    BigDecimal originalPrice;
    BigDecimal seckillPrice;
    Integer stock;
    Integer sales;
    String startTimeStr;
    String endTimeStr;
    List<String> images;
    BigDecimal discount;
    String status;
}
