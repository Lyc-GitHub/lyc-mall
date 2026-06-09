package com.lyc.learn.secKillOrder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("seckill_result")
public class SeckillResult {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    private Integer state;   // 0-排队中 1-成功 2-失败

    private String orderNo;

    private String failReason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
