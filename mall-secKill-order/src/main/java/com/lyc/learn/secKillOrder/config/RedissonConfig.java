package com.lyc.learn.secKillOrder.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 单机或集群配置
        config.useSingleServer()
                .setAddress("redis://192.168.40.128:6379").setPassword("Lyc123456.").setTimeout(60000);

        // 配置 JSON 序列化
        JsonJacksonCodec jsonCodec = new JsonJacksonCodec();
        // 支持 Java 8 日期类型
        jsonCodec.getObjectMapper().registerModule(new JavaTimeModule());
        jsonCodec.getObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        config.setCodec(jsonCodec);

        return Redisson.create(config);
    }
}
