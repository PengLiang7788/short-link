package com.example.shortlink.shop.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author 彭亮
 * @create 2023-01-09 14:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayInfoVo {

    private String outTradeNo;

    /**
     * 订单总金额 单位是 分
     */
    private BigDecimal payFee;

    /**
     * 支付类型 微信 支付宝
     */
    private String payType;

    /**
     * 端类型 app h5 pc
     */
    private String clientType;

    /**
     * 标题
     */
    private String title;

    /**
     * 详情
     */
    private String description;

    /**
     * 订单支付超时 单位 毫秒
     */
    private Long orderPayTimeoutMills;

    /**
     * 用户标识
     */
    private Long accountNo;
}
