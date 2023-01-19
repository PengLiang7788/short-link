package com.example.shortlink.data.service;

import com.example.shortlink.data.controller.request.*;
import com.example.shortlink.data.vo.VisitStatsVo;
import com.example.shortlink.data.vo.VisitTrendVo;

import java.util.List;
import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-19 14:02
 */
public interface VisitStatsService {

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    Map<String, Object> pageVisitRecord(VisitRecordPageRequest request);

    /**
     * 根据时间范围查询地区访问分布
     *
     * @param request
     * @return
     */
    List<VisitStatsVo> queryRegionWithDay(RegionQueryRequest request);

    /**
     * 访问趋势图
     *
     * @param request
     * @return
     */
    List<VisitTrendVo> queryVisitTrend(VisitTrendRequest request);

    /**
     * 高频referer统计
     *
     * @param request
     * @return
     */
    List<VisitStatsVo> queryFrequentSource(FrequentSourceRequest request);

    /**
     * 查询设备访问分布情况
     *
     * @param request
     * @return
     */
    Map<String, List<VisitStatsVo>> queryDeviceInfo(QueryDeviceRequest request);
}
