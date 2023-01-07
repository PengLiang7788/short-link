package com.example.shortlink.link.controller.request;

import lombok.Data;

import java.util.Date;

/**
 * @author 彭亮
 * @create 2023-01-06 19:23
 */
@Data
public class ShortLinkPageRequest {
    /**
     * 组id
     */
    private Long groupId;

    /**
     * 第几页
     */
    private int page;

    /**
     * 每页多少条
     */
    private int size;

}
