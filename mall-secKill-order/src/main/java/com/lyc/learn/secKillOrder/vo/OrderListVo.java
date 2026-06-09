package com.lyc.learn.secKillOrder.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderListVo {
    Long id;

    String orderNo;
    
    String userName;
    
    String productName;

    BigDecimal payAmount;

    Integer quantity;

    String statusStr;
    
    String statusVal;

    LocalDateTime createTime;
    
    public static String convertStatus(Integer status) {
        if (status == 0) {
            return "pending";
        } else if (status == 1) {
            return "paid";
        } else {
            return "unkown";
        }
    }
}
