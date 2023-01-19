package com.example.shortlink.data.service.impl;

import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.data.controller.request.VisitRecordPageRequest;
import com.example.shortlink.data.mapper.VisitStatsMapper;
import com.example.shortlink.data.model.VisitStatsDo;
import com.example.shortlink.data.service.VisitStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        int count = visitStatsMapper.countTotal(code,accountNo);
        int from = (page - 1) * size;

        List<VisitStatsDo> list = visitStatsMapper.pageVisitRecord(code,accountNo,from,size);
        data.put("total",count);
        data.put("current_page",page);

        /**
         * 计算总页数
         */
        int totalPage = 0;
        if (count % size == 0){
            totalPage = count / size;
        }else {
            totalPage = count / size + 1;
        }
        data.put("total_page",totalPage);

        data.put("data",list);

        return data;
    }
}
