package com.example.shortlink.link.controller;


import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.link.controller.request.ShortLinkAddRequest;
import com.example.shortlink.link.service.ShortLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 *
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
     * @param shortLinkAddRequest
     * @return
     */
    @PostMapping("/add")
    public JsonData createShortLink(@RequestBody ShortLinkAddRequest shortLinkAddRequest) {
        JsonData result = shortLinkService.createShortLink(shortLinkAddRequest);
        return result;
    }

}

