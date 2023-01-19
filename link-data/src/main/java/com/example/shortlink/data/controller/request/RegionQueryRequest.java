package com.example.shortlink.data.controller.request;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2023-01-19 15:11
 */
@Data
public class RegionQueryRequest {

    private String code;

    private String startTime;

    private String endTime;
}
