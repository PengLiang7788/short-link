package com.example.shortlink.account.service;

import com.example.shortlink.account.controller.request.TrafficPageRequest;
import com.example.shortlink.account.controller.request.UseTrafficRequest;
import com.example.shortlink.account.vo.TrafficVo;
import com.example.shortlink.common.model.EventMessage;
import com.example.shortlink.common.util.JsonData;

import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-11 20:06
 */
public interface TrafficService {
    /**
     * 处理流量包业务
     *
     * @param eventMessage
     */
    void handleTrafficMessage(EventMessage eventMessage);

    /**
     * 分页查询流量包列表，查询可用的流量包
     *
     * @param request
     * @return
     */
    Map<String, Object> pageAvailable(TrafficPageRequest request);

    /**
     * 查找流量包详情
     *
     * @param trafficId
     * @return
     */
    TrafficVo detail(long trafficId);

    /**
     * 删除过期流量包
     *
     * @return
     */
    boolean deleteExpireTraffic();

    /**
     * 扣减流量包
     *
     * @param usedTrafficRequest
     * @return
     */
    JsonData reduce(UseTrafficRequest usedTrafficRequest);

}
