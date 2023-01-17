package com.example.shortlink.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 彭亮
 * @create 2023-01-17 20:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceInfoDo {

    /**
     * 浏览器名称
     */
    private String browserName;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 操作系统版本
     */
    private String osVersion;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备厂商
     */
    private String deviceManufacturer;

    /**
     * 终端唯一标识
     */
    private String udid;

}
