package com.lyc.learn.common.utils;

public class RabbitFieldUtil {
    
    // 死信配置前缀
    public static final String DLX_PREFIX = "dlx.";

    // 秒杀业务-交换机
    public static final String EXCHANGE_SECKILL = "exchange_secKill";
    // 秒杀业务-队列
    public static final String QUEUE_SECKILL = "queue_secKill";
    // 秒杀业务-路由key
    public static final String ROUTING_KEY_SECKILL = "routing_key_secKill";
    
}
