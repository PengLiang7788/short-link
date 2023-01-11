package com.example.shortlink.shop.component;

import com.example.shortlink.shop.vo.PayInfoVo;

/**
 * @author 彭亮
 * @create 2023-01-11 10:49
 */
public interface PayStrategy {
    /**
     * 统一下单接口
     *
     * @param payInfoVo
     * @return
     */
    String unifiedOrder(PayInfoVo payInfoVo);

    /**
     * 退款接口
     *
     * @param payInfoVo
     * @return
     */
    default String refund(PayInfoVo payInfoVo) {return "";}

    /**
     * 查询支付状态
     *
     * @param payInfoVo
     * @return
     */
    default String queryPayStatus(PayInfoVo payInfoVo){return "";}

    /**
     * 关闭订单
     *
     * @param payInfoVo
     * @return
     */
    default String closeOrder(PayInfoVo payInfoVo){return "";}

}
