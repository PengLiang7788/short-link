package com.example.shortlink.shop.service.impl;

import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.shop.controller.request.ConfirmOrderRequest;
import com.example.shortlink.shop.manager.ProductOrderManager;
import com.example.shortlink.shop.model.ProductOrderDo;
import com.example.shortlink.shop.service.ProductOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-09 13:26
 */
@Service
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderManager productOrderManager;

    /**
     * 分页接口
     *
     * @param page
     * @param size
     * @param state
     * @return
     */
    @Override
    public Map<String, Object> page(int page, int size, String state) {

        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        Map<String, Object> pageResult = productOrderManager.page(page, size, accountNo, state);
        return pageResult;
    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo
     * @return
     */
    @Override
    public String queryProductOrderState(String outTradeNo) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        ProductOrderDo productOrderDo = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);
        if (productOrderDo == null) {
            return "";
        } else {
            return productOrderDo.getState();
        }
    }

    /**
     * 确认订单
     * @param orderRequest
     * @return
     */
    @Override
    public JsonData confirmOrder(ConfirmOrderRequest orderRequest) {
        return null;
    }

}
