package com.example.shortlink.shop.controller.request;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2023-01-06 19:23
 */
@Data
public class ProductOrderPageRequest {
    /**
     * 状态
     */
    private String state;

    /**
     * 第几页
     */
    private int page;

    /**
     * 每页多少条
     */
    private int size;

}
