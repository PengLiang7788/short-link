package com.example.shortlink.link.feign;

import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.link.controller.request.UseTrafficRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 彭亮
 * @create 2023-01-14 21:51
 */
@FeignClient(name = "link-account")
public interface TrafficFeignService {

    /**
     * 使用流量包
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/api/traffic/v1/reduce", headers = {"rpc-token=${rpc.token}"})
    JsonData useTraffic(@RequestBody UseTrafficRequest request);

}
