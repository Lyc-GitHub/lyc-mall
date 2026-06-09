package com.lyc.learn.secKillOrder;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.lyc.learn.secKillOrder.client")  // 只扫描当前模块的 FeignClient
@EnableDiscoveryClient
@MapperScan("com.lyc.learn.secKillOrder.mapper")
public class SecKillOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecKillOrderApplication.class, args);
    }
}
