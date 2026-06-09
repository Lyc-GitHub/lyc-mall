package com.lyc.learn.common.utils;

public class RedisKeyUtil {

    // 秒杀商品信息key
    public static final String secKillProductInfoKey = "secKill:product:info";

    // 秒杀商品库存信息key
    public static final String secKillProductStockKey = "secKill:product:stock";
    
    // 获取商品已秒杀用户列表key
    public static String getSecKilledUserKey(Long productId) {
        return "secKill:product:killed:" + productId + ":userId";
    }
    
    // 获取秒杀商品的订单信息key
    public static String getSecKillProductOrderInfoKey(Long productId) {
        return "secKill:product:orderInfo:" + productId;
    }
}
