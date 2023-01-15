package com.example.shortlink.link.controller;


import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.link.controller.request.ShortLinkAddRequest;
import com.example.shortlink.link.controller.request.ShortLinkDelRequest;
import com.example.shortlink.link.controller.request.ShortLinkPageRequest;
import com.example.shortlink.link.controller.request.ShortLinkUpdateRequest;
import com.example.shortlink.link.service.ShortLinkService;
import com.example.shortlink.link.vo.ShortLinkVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Value("${rpc.token}")
    private String rpcToken;

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
        Map<String, Object> result = shortLinkService.pageByGroupId(request);
        return JsonData.buildSuccess(result);
    }

    /**
     * 删除短链
     *
     * @param delRequest
     * @return
     */
    @PostMapping("/del")
    public JsonData del(@RequestBody ShortLinkDelRequest delRequest) {
        JsonData jsonData = shortLinkService.del(delRequest);
        return jsonData;
    }

    /**
     * 更新短链
     *
     * @param request
     * @return
     */
    @PostMapping("/update")
    public JsonData update(@RequestBody ShortLinkUpdateRequest request) {
        JsonData jsonData = shortLinkService.update(request);
        return jsonData;
    }

    @GetMapping("/check")
    public JsonData check(@RequestParam("shortLinkCode") String shortLinkCode,
                          HttpServletRequest request) {

        String token = request.getHeader("rpc-token");
        if (rpcToken.equalsIgnoreCase(token)) {
            ShortLinkVo shortLinkVo = shortLinkService.parseShortLinkCode(shortLinkCode);

            return shortLinkVo == null ? JsonData.buildError("短链不存在") : JsonData.buildSuccess();
        } else {
            return JsonData.buildError("非法访问");
        }
    }

}

