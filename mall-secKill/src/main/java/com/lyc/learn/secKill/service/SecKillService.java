package com.lyc.learn.secKill.service;

import com.lyc.learn.auth.sdk.utils.UserContextUtil;
import com.lyc.learn.common.dto.SecKillOrderVo;
import com.lyc.learn.common.dto.SecKillProductVo;
import com.lyc.learn.common.enums.OrderStatusEnums;
import com.lyc.learn.common.utils.JsonUtil;
import com.lyc.learn.common.utils.RabbitFieldUtil;
import com.lyc.learn.common.utils.RedisKeyUtil;
import com.lyc.learn.common.vo.CommonResult;
import com.lyc.learn.common.vo.SecKillMQVo;
import com.lyc.learn.secKill.utils.RedisLuaScriptUtil;
import com.lyc.learn.secKill.vo.SecKillResultVo;
import org.redisson.api.RMap;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Service
public class SecKillService {

    Logger logger = LoggerFactory.getLogger(SecKillService.class);

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    public CommonResult secKillProduct(long productId) {
        // 获取登录用户
        Long userId = UserContextUtil.getUserId();
        
        // 已秒杀用户信息key
        String secKilledUserKey = RedisKeyUtil.getSecKilledUserKey(productId);
        
        // 调用lua脚本，秒杀
        String secKillLua;
        try {
            secKillLua = RedisLuaScriptUtil.getSecKillLua();
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("获取秒杀脚本失败！", e);
            return CommonResult.error("获取秒杀脚本失败");
        }
        RScript script = redissonClient.getScript(StringCodec.INSTANCE);
        String resultJson = script.eval(RScript.Mode.READ_WRITE, secKillLua, RScript.ReturnType.VALUE,
                Arrays.asList(RedisKeyUtil.secKillProductInfoKey, RedisKeyUtil.secKillProductStockKey, secKilledUserKey),
                productId, userId);
        SecKillResultVo resultObj = JsonUtil.toObject(resultJson, SecKillResultVo.class);
        if (resultObj.getCode() != 200) {
            return CommonResult.error(resultObj.getMsg());
        }

        // TODO 订单号怎么保证全局唯一
        String orderNo = productId + ":" + userId + ":" + UUID.randomUUID();

        /**
         * 创建订单信息，给MQ和redis都发一份
         * 这样MQ来不及消费创建订单时，可以先查redis里的订单信息，快速返回订单信息给用户支付
         */
        SecKillProductVo productInfo = resultObj.getProductInfo();
        SecKillOrderVo seckillOrder = new SecKillOrderVo();
        seckillOrder.setOrderNo(orderNo);
        seckillOrder.setUserId(userId);
        seckillOrder.setProductId(productInfo.getId());
        seckillOrder.setProductAmount(productInfo.getSeckillPrice());
        seckillOrder.setPayAmount(productInfo.getSeckillPrice());
        seckillOrder.setStatus(OrderStatusEnums.BEPAY.getCode());
        seckillOrder.setCreateTime(LocalDateTime.now());
        RMap<Long, Object> cacheOrderInfo = redissonClient.getMap(RedisKeyUtil.getSecKillProductOrderInfoKey(productId), StringCodec.INSTANCE);
        cacheOrderInfo.put(userId, JsonUtil.toJson(seckillOrder));

        /**
         * 创建订单信息，但不入库，发给MQ先
         * 让MQ来异步执行订单信息入库，扣减库存等操作
         * 秒杀结果快速返回
         */
        SecKillMQVo secKillMQVo = new SecKillMQVo(productId, userId, orderNo, seckillOrder);
        rabbitTemplate.convertAndSend(RabbitFieldUtil.EXCHANGE_SECKILL, RabbitFieldUtil.ROUTING_KEY_SECKILL, JsonUtil.toJson(secKillMQVo));
        return CommonResult.success("秒杀成功", orderNo);
    }
}
