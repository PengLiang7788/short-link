package com.example.shortlink.link.controller;


import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.link.controller.request.ShortLinkAddRequest;
import com.example.shortlink.link.controller.request.ShortLinkPageRequest;
import com.example.shortlink.link.service.ShortLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 彭亮
 * @since 2023-01-03
 */
@RestController
@RequestMapping("/api/link/v1")
public class ShortLinkController {

    @Autowired
    private ShortLinkService shortLinkService;

    /**
     * 创建短链
     *
     * @param shortLinkAddRequest
     * @return
     */
    @PostMapping("/add")
    public JsonData createShortLink(@RequestBody ShortLinkAddRequest shortLinkAddRequest) {
        JsonData result = shortLinkService.createShortLink(shortLinkAddRequest);
        return result;
    }

    /**
     * 分页查找短链
     */
    @RequestMapping("/page")
    public JsonData pageByGroupId(@RequestBody ShortLinkPageRequest request) {
        Map<String , Object> result = shortLinkService.pageByGroupId(request);
        return JsonData.buildSuccess(result);
    }

}

