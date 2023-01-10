package com.example.shortlink.shop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 彭亮
 * @create 2023-01-10 15:23
 */
@ConfigurationProperties(prefix = "pay.wechat")
@Configuration
@Data
public class WechatPayConfig {

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 公众号id，需要和商户号绑定
     */
    private String wxPayAppid;

    /**
     * 商户API证书序列号
     */
    private String mchSerialNo;

    /**
     * APIv3密钥
     */
    private String apiV3Key;

    /**
     * 商户私钥文件
     */
    private String privateKeyPath;

    /**
     * 支付成功页面跳转
     */
    private String successReturnUrl;

    /**
     * 接收结果通知地址
     */
    private String callbackUrl;


}
