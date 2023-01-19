package com.example.shortlink.data.vo;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2023-01-19 16:10
 */
@Data
public class VisitTrendVo {

    private Long ipCount = 0L;

    private Long pvCount=0L;

    private Long uvCount=0L;

    private Long newUVCount = 0L;

    /**
     * 时间的字符串映射，天，小时
     */
    private String dateTimeStr;

}
