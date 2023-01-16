package com.example.shortlink.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 彭亮
 * @create 2023-01-16 14:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogRecord {
    /**
     * 客户端ip
     */
    private String ip;

    /**
     * 产生时间戳
     */
    private Long ts;

    /**
     * 日志事件类型
     */
    private String event;

    /**
     * udid是设备的唯一标识，全程uniqueDeviceIdentifier
     */
    private String udid;

    /**
     * 业务id
     */
    private String bizId;

    /**
     * 日志详情
     */
    private Object data;

}
