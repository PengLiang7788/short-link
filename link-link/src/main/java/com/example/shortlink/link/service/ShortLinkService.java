package com.example.shortlink.link.service;

import com.example.shortlink.link.vo.ShortLinkVo;

/**
 * @author 彭亮
 * @create 2023-01-06 9:37
 */
public interface ShortLinkService {
    /**
     * 解析短链
     * @param shortLinkCode
     * @return
     */
    ShortLinkVo parseShortLinkCode(String shortLinkCode);
}
