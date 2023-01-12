package com.example.shortlink.account.service;

import com.example.shortlink.common.model.EventMessage;

/**
 * @author 彭亮
 * @create 2023-01-11 20:06
 */
public interface TrafficService {
    /**
     * 处理流量包业务
     * @param eventMessage
     */
    void handleTrafficMessage(EventMessage eventMessage);
}
