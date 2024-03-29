package com.example.shortlink.link.service;

import com.example.shortlink.common.model.EventMessage;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.link.controller.request.ShortLinkAddRequest;
import com.example.shortlink.link.controller.request.ShortLinkDelRequest;
import com.example.shortlink.link.controller.request.ShortLinkPageRequest;
import com.example.shortlink.link.controller.request.ShortLinkUpdateRequest;
import com.example.shortlink.link.vo.ShortLinkVo;

import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-06 9:37
 */
public interface ShortLinkService {
    /**
     * 解析短链
     *
     * @param shortLinkCode
     * @return
     */
    ShortLinkVo parseShortLinkCode(String shortLinkCode);

    /**
     * 创建短链
     *
     * @param shortLinkAddRequest
     * @return
     */
    JsonData createShortLink(ShortLinkAddRequest shortLinkAddRequest);

    /**
     * 处理新增短链消息
     *
     * @param eventMessage
     * @return
     */
    boolean handleAddShortLink(EventMessage eventMessage);

    /**
     * 处理更新短链消息
     *
     * @param eventMessage
     * @return
     */
    boolean handleUpdateShortLink(EventMessage eventMessage);

    /**
     * 处理删除短链消息
     *
     * @param eventMessage
     * @return
     */
    boolean handleDelShortLink(EventMessage eventMessage);


    /**
     * 分页查找短链
     *
     * @param request
     * @return
     */
    Map<String, Object> pageByGroupId(ShortLinkPageRequest request);

    /**
     * 删除短链
     *
     * @param delRequest
     * @return
     */
    JsonData del(ShortLinkDelRequest delRequest);

    /**
     * 更新短链
     * @param request
     * @return
     */
    JsonData update(ShortLinkUpdateRequest request);
}
