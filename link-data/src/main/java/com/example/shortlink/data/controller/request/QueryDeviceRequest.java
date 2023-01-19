package com.example.shortlink.data.controller.request;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2023-01-19 19:49
 */
@Data
public class QueryDeviceRequest {

    private String code;

    private String startTime;

    private String endTime;

}
