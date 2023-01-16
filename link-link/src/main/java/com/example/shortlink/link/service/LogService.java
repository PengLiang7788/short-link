package com.example.shortlink.link.service;

import com.example.shortlink.common.util.JsonData;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 彭亮
 * @create 2023-01-16 13:56
 */
public interface LogService {

    /**
     * 记录日志
     *
     * @param request
     * @param shortLinkCode
     * @param accountNo
     */
    void recordShortLinkLog(HttpServletRequest request, String shortLinkCode, Long accountNo);

}
