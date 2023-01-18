package com.example.shortlink.app.func;

import com.alibaba.fastjson.JSONObject;
import com.example.shortlink.app.model.DeviceInfoDo;
import com.example.shortlink.app.model.ShortLinkWideDo;
import com.example.shortlink.app.util.DeviceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @author 彭亮
 * @create 2023-01-18 11:11
 */
@Slf4j
public class DeviceMapFunction implements MapFunction<String, ShortLinkWideDo> {

    @Override
    public ShortLinkWideDo map(String value) throws Exception {
        // 还原json对象
        JSONObject jsonObject = JSONObject.parseObject(value);
        // 获取ua
        String userAgent = jsonObject.getJSONObject("data").getString("user-agent");
        // 解析设备对象
        DeviceInfoDo deviceInfo = DeviceUtil.getDeviceInfo(userAgent);

        String udid = jsonObject.getString("udid");
        deviceInfo.setUdid(udid);
        // 配置短链基本信息宽表
        ShortLinkWideDo shortLinkWideDo = ShortLinkWideDo.builder()
                // 短链基本信息补齐
                .accountNo(jsonObject.getJSONObject("data").getLong("accountNo"))
                .visitTime(jsonObject.getLong("ts"))
                .code(jsonObject.getString("bizId"))
                .referer(jsonObject.getString("referer"))
                .isNew(jsonObject.getInteger("is_new"))
                .ip(jsonObject.getString("ip"))
                // 设备信息补齐
                .browserName(deviceInfo.getBrowserName())
                .os(deviceInfo.getOs())
                .osVersion(deviceInfo.getOsVersion())
                .deviceType(deviceInfo.getDeviceType())
                .deviceManufacturer(deviceInfo.getDeviceManufacturer())
                .udid(deviceInfo.getUdid())

                .build();

        return shortLinkWideDo;
    }


}
