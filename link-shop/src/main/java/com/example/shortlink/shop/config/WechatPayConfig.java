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

    public static class Url{
        /**
         * native下单接口
         */
        public static final String NATIVE_ORDER = "https://api.mch.weixin.qq.com/v3/pay/transactions/native";
        public static final String NATIVE_ORDER_PATH = "/v3/pay/transactions/native";


        /**
         * native订单查询接口，根据商户订单号查询
         * 一个是根据微信订单号，一个是根据商户订单号
         */
        public static final String NATIVE_QUERY = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s?mchid=%s";
        public static final String NATIVE_QUERY_PATH = "/v3/pay/transactions/out-trade-no/%s?mchid=%s";


        /**
         * native关闭订单接口
         */
        public static final String NATIVE_CLOSE = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/%s/close";
        public static final String NATIVE_CLOSE_PATH = "/v3/pay/transactions/out-trade-no/%s/close";

    }

}
