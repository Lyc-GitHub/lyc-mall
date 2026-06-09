package com.lyc.learn.common.vo;

import com.lyc.learn.common.dto.SecKillOrderVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecKillMQVo {
    
    long productId;
    
    long userId;
    
    String orderNo;

    SecKillOrderVo order;
}
