package com.example.shortlink.link.controller.request;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2023-01-03 21:02
 */
@Data
public class LinkGroupUpdateRequest {
    /**
     * 分组id
     */
    private Long id;
    /**
     * 组名
     */
    private String title;

}
