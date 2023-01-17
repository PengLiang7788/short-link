package com.example.shortlink.app.util;


import com.example.shortlink.app.model.DeviceInfoDo;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-17 14:40
 */
public class DeviceUtil {

    /**
     * 生成web设备唯一id
     *
     * @param map
     * @return
     */
    public static String geneWebUniqueDeviceId(Map<String, String> map) {
        String deviceId = MD5(map.toString());
        return deviceId;
    }

    /**
     * MD5加密
     *
     * @param data
     * @return
     */
    public static String MD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取浏览器对象
     *
     * @param agent
     * @return
     */
    public static Browser getBrowser(String agent) {
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        return userAgent.getBrowser();
    }

    /**
     * 获取操作系统对象
     *
     * @param agent
     * @return
     */
    public static OperatingSystem getOperatingSystem(String agent) {
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        return userAgent.getOperatingSystem();
    }

    /**
     * 获取浏览器名称
     *
     * @param agent
     * @return Firefox、Chrome
     */
    public static String getBrowserName(String agent) {
        return getBrowser(agent).getGroup().getName();
    }

    /**
     * 获取设备类型
     *
     * @param agent
     * @return MOBILE、COMPUTER
     */
    public static String getDeviceType(String agent) {
        return getOperatingSystem(agent).getDeviceType().toString();
    }

    /**
     * 获取操作系统
     *
     * @param agent
     * @return Windows，IOS，Android
     */
    public static String getOS(String agent) {
        return getOperatingSystem(agent).getName();
    }

    /**
     * 获取设备厂家
     *
     * @param agent
     * @return
     */
    public static String getDeviceManufacturer(String agent) {
        return getOperatingSystem(agent).getManufacturer().toString();
    }

    /**
     * 获取操作系统版本
     *
     * @param agent
     * @return
     */
    public static String getOSVersion(String agent) {
        String osVersion = "";
        if (StringUtils.isBlank(agent)) {
            return osVersion;
        }
        String[] strArr = agent.substring(agent.indexOf("(") + 1, agent.indexOf(")")).split(";");
        if (null == strArr || strArr.length == 0) {
            return osVersion;
        }
        osVersion = strArr[1];
        return osVersion;
    }

    /**
     * 解析对象
     *
     * @param agent
     * @return
     */
    public static DeviceInfoDo getDeviceInfo(String agent) {

        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();

        String browserName = browser.getGroup().getName();
        String os = operatingSystem.getGroup().getName();
        String manufacture = operatingSystem.getManufacturer().toString();
        String deviceType = operatingSystem.getDeviceType().toString();

        DeviceInfoDo deviceInfoDo = DeviceInfoDo.builder()
                .browserName(browserName)
                .deviceManufacturer(manufacture)
                .deviceType(deviceType)
                .os(os)
                .osVersion(getOSVersion(agent))
                .build();

        return deviceInfoDo;
    }
}
