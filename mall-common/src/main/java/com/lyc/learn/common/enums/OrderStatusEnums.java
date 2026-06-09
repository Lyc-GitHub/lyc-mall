package com.lyc.learn.common.enums;

public enum OrderStatusEnums {
    BEPAY(0, "待支付"),
    CPMPLETE(1, "已支付"),
    CANCAL(2, "已取消"),
    TIMEOUT(3, "已超时");
    
    int code;
    String desc;
    OrderStatusEnums(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static OrderStatusEnums convertOrderStatus(int code) {
        for (OrderStatusEnums orderStatusEnums : OrderStatusEnums.values()) {
            if (orderStatusEnums.code == code) {
                return orderStatusEnums;
            }
        }
        return null;
    }
    
}
