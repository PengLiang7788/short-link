package com.example.shortlink.app.util;


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

}
