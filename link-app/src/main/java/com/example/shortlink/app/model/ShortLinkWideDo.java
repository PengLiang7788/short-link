package com.example.shortlink.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 彭亮
 * @create 2023-01-17 20:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLinkWideDo {

    //==============短链业务本身字段=====================

    /**
     * 短链码
     */
    private String code;

    /**
     * 账户号
     */
    private Long accountNo;

    /**
     * 访问时间
     */
    private Long visitTime;

    /**
     * 站点来源，只记录域名
     */
    private String referer;

    /**
     * 是否是新用户
     * 1是新访客，0是旧访客
     */
    private Integer isNew;

    /**
     * 访问来源ip
     */
    private String ip;

    //==============设备相关字段=====================

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
