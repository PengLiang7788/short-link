package com.example.shortlink.data.service.impl;

import com.example.shortlink.common.enums.DateTimeFieldEnum;
import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.data.controller.request.FrequentSourceRequest;
import com.example.shortlink.data.controller.request.RegionQueryRequest;
import com.example.shortlink.data.controller.request.VisitRecordPageRequest;
import com.example.shortlink.data.controller.request.VisitTrendRequest;
import com.example.shortlink.data.mapper.VisitStatsMapper;
import com.example.shortlink.data.model.VisitStatsDo;
import com.example.shortlink.data.service.VisitStatsService;
import com.example.shortlink.data.vo.VisitStatsVo;
import com.example.shortlink.data.vo.VisitTrendVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.auth.Login;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 彭亮
 * @create 2023-01-19 14:02
 */
@Service
@Slf4j
public class VisitStatsServiceImpl implements VisitStatsService {

    @Autowired
    private VisitStatsMapper visitStatsMapper;

    /**
     * 分页查询
     *
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> pageVisitRecord(VisitRecordPageRequest request) {

        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        Map<String, Object> data = new HashMap<>(16);
        String code = request.getCode();
        int page = request.getPage();
        int size = request.getSize();

        int count = visitStatsMapper.countTotal(code, accountNo);
        int from = (page - 1) * size;

        List<VisitStatsDo> list = visitStatsMapper.pageVisitRecord(code, accountNo, from, size);

        List<VisitStatsVo> result = list.stream().map(item -> {
            VisitStatsVo visitStatsVo = new VisitStatsVo();
            BeanUtils.copyProperties(item, visitStatsVo);
            return visitStatsVo;
        }).collect(Collectors.toList());

        data.put("total", count);
        data.put("current_page", page);

        /**
         * 计算总页数
         */
        int totalPage = 0;
        if (count % size == 0) {
            totalPage = count / size;
        } else {
            totalPage = count / size + 1;
        }
        data.put("total_page", totalPage);

        data.put("data", result);

        return data;
    }

    /**
     * 根据时间范围查询地区访问分布
     *
     * @param request
     * @return
     */
    @Override
    public List<VisitStatsVo> queryRegionWithDay(RegionQueryRequest request) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        List<VisitStatsDo> list = visitStatsMapper
                .queryRegionVisitStartsWithDay(request.getCode(), request.getStartTime(), request.getEndTime(), accountNo);

        List<VisitStatsVo> result = list.stream().map(item -> {
            VisitStatsVo visitStatsVo = new VisitStatsVo();
            BeanUtils.copyProperties(item, visitStatsVo);
            return visitStatsVo;
        }).collect(Collectors.toList());

        return result;
    }

    /**
     * 访问趋势图
     *
     * @param request
     * @return
     */
    @Override
    public List<VisitTrendVo> queryVisitTrend(VisitTrendRequest request) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        String code = request.getCode();
        String type = request.getType();
        String startTime = request.getStartTime();
        String endTime = request.getEndTime();
        List<VisitStatsDo> list = null;
        if (DateTimeFieldEnum.DAY.name().equalsIgnoreCase(type)) {
            list = visitStatsMapper.queryVisitTrendWithMultiDay(code, accountNo, startTime, endTime);

        } else if (DateTimeFieldEnum.HOUR.name().equalsIgnoreCase(type)) {
            list = visitStatsMapper.queryVisitTrendWithMultiHour(code, accountNo, startTime);
        } else if (DateTimeFieldEnum.MINUTE.name().equalsIgnoreCase(type)) {
            list = visitStatsMapper.queryVisitTrendWithMultiMinute(code, accountNo, startTime, endTime);
        }

        List<VisitTrendVo> result = list.stream().map(item -> {
            VisitTrendVo visitTrendVo = new VisitTrendVo();
            BeanUtils.copyProperties(item, visitTrendVo);
            return visitTrendVo;
        }).collect(Collectors.toList());

        return result;
    }

    /**
     * 高频referer统计
     *
     * @param request
     * @return
     */
    @Override
    public List<VisitStatsVo> queryFrequentSource(FrequentSourceRequest request) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        String code = request.getCode();
        String startTime = request.getStartTime();
        String endTime = request.getEndTime();

        List<VisitStatsDo> list = visitStatsMapper.queryFrequentSource(code, accountNo, startTime, endTime, 10);

        List<VisitStatsVo> result = list.stream().map(item -> {
            VisitStatsVo visitStatsVo = new VisitStatsVo();
            BeanUtils.copyProperties(item, visitStatsVo);
            return visitStatsVo;
        }).collect(Collectors.toList());

        return result;
    }
}
