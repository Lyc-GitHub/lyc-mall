package com.lyc.learn.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lyc.learn.product.entity.SeckillProduct;
import com.lyc.learn.common.utils.JsonUtil;
import com.lyc.learn.common.utils.RedisKeyUtil;
import com.lyc.learn.common.utils.TimestampUtil;
import com.lyc.learn.product.mapper.SeckillProductMapper;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ProductCacheService {
    
    @Autowired
    SeckillProductMapper seckillProductMapper;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 预热秒杀商品到缓存中，每半小时执行一次
     */
    @Scheduled(fixedRate = 1800000)
    public void preheatProductToCache() {
        // 获取快进入和已在秒杀时间段的商品
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);
        List<SeckillProduct> productList = seckillProductMapper.selectList(new LambdaQueryWrapper<SeckillProduct>()
                .gt(SeckillProduct::getStockCount, 0)
                .eq(SeckillProduct::getStatus, 1)
                .and(
                        w -> w.lt(SeckillProduct::getStartTime, oneHourLater)
                                .or(sub -> sub.lt(SeckillProduct::getStartTime, now)
                                        .gt(SeckillProduct::getEndTime, now))
                 ));
        for (SeckillProduct productInfo : productList) {
            Long productId = productInfo.getId();
            // 查询缓存是否存在
            RMap<Object, Object> productInfoCacheMap = redissonClient.getMap(RedisKeyUtil.secKillProductInfoKey, StringCodec.INSTANCE);
            boolean hasInfo = productInfoCacheMap.containsKey(productId);
            if (!hasInfo) {
                Map<String, Object> productMap = JsonUtil.toMap(JsonUtil.toJson(productInfo));
                // 设置开始时间和结束时间的时间戳
                LocalDateTime startTime = productInfo.getStartTime();
                long startTimestamp = TimestampUtil.convertLocalTimeToTimestamp(startTime);
                LocalDateTime endTime = productInfo.getEndTime();
                long endTimestamp = TimestampUtil.convertLocalTimeToTimestamp(endTime);
                productMap.put("start_timestamp", startTimestamp);
                productMap.put("end_timestamp", endTimestamp);
                productInfoCacheMap.put(productId, JsonUtil.toJson(productMap));
            }
            RMap<Object, Object> productStockCacheMap = redissonClient.getMap(RedisKeyUtil.secKillProductStockKey, StringCodec.INSTANCE);
            boolean hasStock = productStockCacheMap.containsKey(productId);
            if (!hasStock) {
                productStockCacheMap.put(productId, productInfo.getStockCount());
            }
        }
    }
}
