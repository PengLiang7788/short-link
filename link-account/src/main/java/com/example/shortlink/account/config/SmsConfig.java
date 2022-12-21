package com.example.shortlink.account.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 彭亮
 * @create 2022-12-21 15:33
 */
@ConfigurationProperties(prefix = "sms")
@Configuration
@Data
public class SmsConfig {

    private String templateId;
    private String appCode;

}
