package com.lyc.learn.secKillOrder.mqListener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lyc.learn.secKillOrder.client.ProductClient;
import com.lyc.learn.common.dto.SecKillOrderVo;
import com.lyc.learn.secKillOrder.entity.SeckillOrder;
import com.lyc.learn.secKillOrder.mapper.SeckillOrderMapper;
import com.lyc.learn.common.utils.JsonUtil;
import com.lyc.learn.common.utils.RabbitFieldUtil;
import com.lyc.learn.common.vo.SecKillMQVo;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;

@Service
public class SecKillConsumer {
    
    Logger logger = LoggerFactory.getLogger(SecKillConsumer.class);
    
    @Autowired
    SeckillOrderMapper seckillOrderMapper;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    ProductClient productClient;
    
    @Autowired
    PlatformTransactionManager transactionManager;
    
    @RabbitListener(queues = RabbitFieldUtil.QUEUE_SECKILL)
    public void consumerSecKillProduct(String secKillVoMsg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        // 开启事务
        DefaultTransactionDefinition dtd = new DefaultTransactionDefinition();  
        dtd.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus transaction = transactionManager.getTransaction(dtd);
        try {
            SecKillMQVo secKillMQVo = JsonUtil.toObject(secKillVoMsg, SecKillMQVo.class);

            // 用户是否已经创建了订单
            SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>().eq(SeckillOrder::getOrderNo, secKillMQVo.getOrderNo()));
            if (seckillOrder == null) {
                // 减库存
                int upStockResult = productClient.decrementProductStock(secKillMQVo.getProductId());
                if (upStockResult == 1) {
                    // 创建订单
                    SecKillOrderVo orderVo = secKillMQVo.getOrder();
                    seckillOrder = JsonUtil.obj2obj(orderVo, SeckillOrder.class);
                    seckillOrderMapper.insert(seckillOrder);
                } else {
                    // TODO 更新库存失败，出现超卖或者其他未知错误，记录数据库
                }
            }
            // 确认消息已消费
            channel.basicAck(deliveryTag, false);
            transactionManager.commit(transaction);
        } catch (Exception e) {
            transactionManager.rollback(transaction);
            // 消息处理失败，重新入队
            try {
                channel.basicReject(deliveryTag, false);
            } catch (IOException ex) {
                logger.error("消息重新入队失败！先持久化", ex);
                // TODO 持久化到数据库先
            }
        }
    }
}
