package com.lyc.learn.product.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class AddSeckillProductVo {
    String productName;
    String summary;
    String description;
    BigDecimal originalPrice;
    BigDecimal seckillPrice;
    Integer initStock;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;
    
    List<String> images;
}
