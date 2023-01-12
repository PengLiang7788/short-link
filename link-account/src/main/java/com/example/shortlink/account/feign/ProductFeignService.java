package com.example.shortlink.account.feign;

import com.example.shortlink.common.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 彭亮
 * @create 2023-01-12 11:11
 */
@FeignClient(name = "link-shop")
public interface ProductFeignService {

    /**
     * 获取流量包商品详情
     *
     * @param productId
     * @return
     */
    @GetMapping("/api/product/v1/detail/{product_id}")
    public JsonData detail(@PathVariable("product_id") long productId);

}
