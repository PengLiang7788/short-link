package com.example.shortlink.link.controller.request;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2023-01-06 19:23
 */
@Data
public class ShortLinkUpdateRequest {
    /**
     * 组id
     */
    private Long groupId;

    /**
     * 映射id
     */
    private Long mappingId;

    /**
     * 短链码
     */
    private String code;

    /**
     * 短链标题
     */
    private String title;

    /**
     * 域名id
     */
    private Long domainId;

    /**
     * 域名类型
     */
    private String domainType;
}
