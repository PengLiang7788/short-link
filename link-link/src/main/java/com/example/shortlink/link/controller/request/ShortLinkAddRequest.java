package com.example.shortlink.link.controller.request;

import lombok.Data;

import java.util.Date;

/**
 * @author 彭亮
 * @create 2023-01-06 19:23
 */
@Data
public class ShortLinkAddRequest {
    /**
     * 组id
     */
    private Long groupId;

    /**
     * 短链标题
     */
    private String title;

    /**
     * 原始url
     */
    private String originalUrl;

    /**
     * 域名id
     */
    private Long domainId;

    /**
     * 域名类型
     */
    private String domainType;

    /**
     * 过期时间
     */
    private Date expired;

}
