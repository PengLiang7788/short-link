package com.example.shortlink.data.controller.request;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2023-01-19 15:51
 */
@Data
public class VisitTrendRequest {

    private String code;

    private String type;

    private String startTime;

    private String endTime;
}

