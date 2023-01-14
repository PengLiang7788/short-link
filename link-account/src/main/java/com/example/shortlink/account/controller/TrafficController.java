package com.example.shortlink.account.controller;

import com.example.shortlink.account.controller.request.TrafficPageRequest;
import com.example.shortlink.account.controller.request.UseTrafficRequest;
import com.example.shortlink.account.service.TrafficService;
import com.example.shortlink.account.vo.TrafficVo;
import com.example.shortlink.common.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-12 9:09
 */
@RestController
@RequestMapping("/api/traffic/v1")
public class TrafficController {

    @Autowired
    private TrafficService trafficService;

    @Value("${rpc.token}")
    private String rpcToken;

    /**
     * 分页查询流量包列表，查询可用的流量包
     *
     * @param request
     * @return
     */
    @PostMapping("/page")
    public JsonData pageAvailable(@RequestBody TrafficPageRequest request) {
        Map<String, Object> map = trafficService.pageAvailable(request);
        return JsonData.buildSuccess(map);
    }

    /**
     * 查找流量包详情
     *
     * @param trafficId
     * @return
     */
    @GetMapping("/detail/{trafficId}")
    public JsonData detail(@PathVariable("trafficId") long trafficId) {
        TrafficVo trafficVo = trafficService.detail(trafficId);
        return JsonData.buildSuccess(trafficVo);
    }

    /**
     * 使用流量包api
     *
     * @param usedTrafficRequest
     * @param request
     * @return
     */
    @PostMapping("/reduce")
    public JsonData useTraffic(@RequestBody UseTrafficRequest usedTrafficRequest, HttpServletRequest request) {
        String requestToken = request.getHeader("rpc-token");

        if (rpcToken.equalsIgnoreCase(requestToken)) {
            // 具体使用流量包逻辑
            JsonData jsonData = trafficService.reduce(usedTrafficRequest);
            return jsonData;
        } else {
            return JsonData.buildError("非法访问");
        }
    }
}
