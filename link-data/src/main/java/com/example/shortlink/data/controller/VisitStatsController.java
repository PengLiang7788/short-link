package com.example.shortlink.data.controller;

import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.data.controller.request.RegionQueryRequest;
import com.example.shortlink.data.controller.request.VisitRecordPageRequest;
import com.example.shortlink.data.model.VisitStatsDo;
import com.example.shortlink.data.service.VisitStatsService;
import com.example.shortlink.data.vo.VisitStatsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 彭亮
 * @create 2023-01-19 14:01
 */
@RestController
@RequestMapping("/api/visit_stats/v1")
public class VisitStatsController {

    @Autowired
    private VisitStatsService visitStatsService;

    /**
     * 分页查找访问记录
     *
     * @param request
     * @return
     */
    @PostMapping("/page_record")
    public JsonData pageVisitRecord(@RequestBody VisitRecordPageRequest request) {
        // 条数限制
        int total = request.getSize() * request.getPage();
        if (total > 1000) {
            return JsonData.buildResult(BizCodeEnum.DATA_OUT_OF_LIMIT_SIZE);
        }

        Map<String, Object> pageResult = visitStatsService.pageVisitRecord(request);

        return JsonData.buildSuccess(pageResult);
    }

    /**
     * 根据时间范围查询地区访问分布
     *
     * @param request
     * @return
     */
    @PostMapping("/region_day")
    public JsonData queryRegionWithDay(@RequestBody RegionQueryRequest request) {
        List<VisitStatsVo> list = visitStatsService.queryRegionWithDay(request);

        return JsonData.buildSuccess(list);
    }


}
