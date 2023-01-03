package com.example.shortlink.link;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 彭亮
 * @create 2023-01-03 18:37
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableTransactionManagement
@MapperScan("com.example.shortlink.link.mapper")
public class LinkServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkServiceApplication.class);
    }

}
