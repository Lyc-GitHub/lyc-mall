package com.lyc.learn.common.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SecKillProductVo {
    private Long id;

    private String productName;

    private BigDecimal originalPrice;

    private BigDecimal seckillPrice;

    private Integer stockCount;

    private Integer initStock;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;   // 0-禁用 1-启用 2-已结束

    private Integer version;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String summary;

    private String mainImgUrl;
}
