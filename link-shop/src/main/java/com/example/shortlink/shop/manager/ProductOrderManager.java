package com.example.shortlink.shop.manager;

import com.example.shortlink.shop.model.ProductOrderDo;

import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-08 17:08
 */
public interface ProductOrderManager {

    /**
     * 新增
     *
     * @param productOrderDo
     * @return
     */
    int add(ProductOrderDo productOrderDo);

    /**
     * 通过订单号和账号查询
     *
     * @param outTraderNo
     * @param accountNo
     * @return
     */
    ProductOrderDo findByOutTradeNoAndAccountNo(String outTraderNo, Long accountNo);

    /**
     * 更新订单状态
     *
     * @param outTraderNo
     * @param accountNo
     * @param newState
     * @param oldState
     * @return
     */
    int updateOrderPayState(String outTraderNo, Long accountNo, String newState, String oldState);

    /**
     * 分页查找订单列表
     *
     * @param page
     * @param size
     * @param accountNo
     * @param state
     * @return
     */
    Map<String, Object> page(int page, int size, Long accountNo, String state);

    /**
     * 删除
     * @param productOrderId
     * @param accountNo
     * @return
     */
    int del(Long productOrderId,Long accountNo);
}
