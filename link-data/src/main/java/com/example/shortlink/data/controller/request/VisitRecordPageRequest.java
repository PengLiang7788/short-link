package com.example.shortlink.data.controller.request;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2023-01-19 14:16
 */
@Data
public class VisitRecordPageRequest {

    /**
     * 短链码
     */
    private String code;

    /**
     * 每页大小
     */
    private int size;

    /**
     * 当前页
     */
    private int page;

}
