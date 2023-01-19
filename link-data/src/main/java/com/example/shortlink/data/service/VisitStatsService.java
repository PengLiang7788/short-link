package com.example.shortlink.data.service;

import com.example.shortlink.data.controller.request.RegionQueryRequest;
import com.example.shortlink.data.controller.request.VisitRecordPageRequest;
import com.example.shortlink.data.vo.VisitStatsVo;

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

}
