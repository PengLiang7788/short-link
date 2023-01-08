package com.example.shortlink.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 彭亮
 * @create 2023-01-08 14:39
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@MapperScan("com.example.shortlink.shop.mapper")
@ComponentScan("com.example")
public class ShopServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopServiceApplication.class);
    }

}
