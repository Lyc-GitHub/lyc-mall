package com.lyc.learn.secKill.config;

import com.lyc.learn.common.utils.RabbitFieldUtil;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // 声明交换机
    @Bean
    public DirectExchange secKillExchange() {
        return new DirectExchange(RabbitFieldUtil.EXCHANGE_SECKILL);
    }
    
    // 声明队列
    @Bean
    public Queue secKillQueue() {
        // 声明队列并绑定死信交换机
        return QueueBuilder.durable(RabbitFieldUtil.QUEUE_SECKILL)
                .withArgument("x-dead-letter-exchange", RabbitFieldUtil.DLX_PREFIX + RabbitFieldUtil.EXCHANGE_SECKILL)
                .withArgument("x-dead-letter-routing-key", RabbitFieldUtil.DLX_PREFIX +RabbitFieldUtil.ROUTING_KEY_SECKILL)
                .build();
    }
    
    // 绑定交换机与队列
    @Bean
    public Binding secKillBinding() {
        return BindingBuilder.bind(secKillQueue()).to(secKillExchange()).with(RabbitFieldUtil.ROUTING_KEY_SECKILL);
    }
    
    // 死信交换机
    @Bean
    public DirectExchange dlxSecKillExchange() {
        return new DirectExchange(RabbitFieldUtil.DLX_PREFIX + RabbitFieldUtil.EXCHANGE_SECKILL);
    }
    
    // 死信队列
    @Bean
    public Queue dlxSecKillQueue() {
        return new Queue(RabbitFieldUtil.DLX_PREFIX + RabbitFieldUtil.QUEUE_SECKILL);
    }
    
    // 绑定死信交换机与死信对垒
    @Bean
    public Binding dlxSecKillBinding() {
        return BindingBuilder.bind(dlxSecKillQueue()).to(dlxSecKillExchange())
                .with(RabbitFieldUtil.DLX_PREFIX + RabbitFieldUtil.ROUTING_KEY_SECKILL);
    }
    
}
