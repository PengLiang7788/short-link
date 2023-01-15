package com.example.shortlink.account.feign;

import com.example.shortlink.common.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 彭亮
 * @create 2023-01-15 16:03
 */
@FeignClient(name = "link-link")
public interface ShortLinkFeignService {

    /**
     * 检查短链是否存在
     *
     * @param shortLinkCode
     * @return
     */
    @GetMapping(value = "/api/link/v1/check", headers = {"rpc-token=${rpc.token}"})
    JsonData check(@RequestParam("shortLinkCode") String shortLinkCode);

}
