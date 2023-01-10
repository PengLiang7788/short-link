package com.example.shortlink.shop.config;

/**
 * @author 彭亮
 * @create 2023-01-10 17:39
 */
public class WechatPayApi {

    /**
     * 微信支付主机地址
     */
    public static final String HOST = "https://api.mch.weixin.qq.com";


    /**
     * Native下单
     */
    public static final String NATIVE_ORDER = HOST + "/v3/pay/transactions/native";

}
