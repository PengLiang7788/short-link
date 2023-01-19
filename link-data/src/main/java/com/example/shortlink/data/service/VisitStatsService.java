package com.example.shortlink.data.service;

import com.example.shortlink.data.controller.request.VisitRecordPageRequest;

import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-19 14:02
 */
public interface VisitStatsService {

    /**
     * 分页查询
     * @param request
     * @return
     */
    Map<String, Object> pageVisitRecord(VisitRecordPageRequest request);
}
