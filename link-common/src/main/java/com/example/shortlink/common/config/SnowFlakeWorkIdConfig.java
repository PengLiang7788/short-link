package com.example.shortlink.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author 彭亮
 * @create 2023-01-03 14:31
 */
@Configuration
@Slf4j
public class SnowFlakeWorkIdConfig {

    static {
        try {
            InetAddress inetAddress = Inet4Address.getLocalHost();

            String hostAddressIp = inetAddress.getHostAddress();

            String workId = Math.abs(hostAddressIp.hashCode()) % 1024 + "";

            System.setProperty("workId", workId);
            log.info("workId:{}",workId);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

}
