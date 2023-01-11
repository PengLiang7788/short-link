package com.example.shortlink.shop.component;

import com.example.shortlink.shop.vo.PayInfoVo;

/**
 * @author 彭亮
 * @create 2023-01-11 10:56
 */
public class PayStrategyContext {

    private PayStrategy payStrategy;

    public PayStrategyContext(PayStrategy payStrategy){
        this.payStrategy = payStrategy;
    }

    /**
     * 根据策略对象,执行不同的下单接口
     * @return
     */
    public String executeUnifiedOrder(PayInfoVo payInfoVo){
        return payStrategy.unifiedOrder(payInfoVo);
    }

    /**
     * 根据策略对象,执行不同的退款接口
     * @return
     */
    public String executeRefund(PayInfoVo payInfoVo){
        return payStrategy.refund(payInfoVo);
    }

    /**
     * 根据策略对象,执行不同的关单接口
     * @return
     */
    public String executeCloseOrder(PayInfoVo payInfoVo){
        return payStrategy.closeOrder(payInfoVo);
    }

    /**
     * 根据策略对象,执行不同的查询订单状态接口
     * @return
     */
    public String executeQueryPayStatus(PayInfoVo payInfoVo){
        return payStrategy.queryPayStatus(payInfoVo);
    }

}
