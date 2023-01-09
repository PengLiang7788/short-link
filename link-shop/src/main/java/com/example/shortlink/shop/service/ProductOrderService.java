package com.example.shortlink.shop.service;

import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.shop.controller.request.ConfirmOrderRequest;

import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-09 13:26
 */
public interface ProductOrderService {
    /**
     * 分页接口
     * @param page
     * @param size
     * @param state
     * @return
     */
    Map<String, Object> page(int page, int size, String state);

    /**
     * 查询订单状态
     * @param outTradeNo
     * @return
     */
    String queryProductOrderState(String outTradeNo);

    /**
     * 确认订单
     * @param orderRequest
     * @return
     */
    JsonData confirmOrder(ConfirmOrderRequest orderRequest);
}