package com.example.shortlink.app.dwm;

import com.alibaba.fastjson.JSONObject;
import com.example.shortlink.app.model.DeviceInfoDo;
import com.example.shortlink.app.model.ShortLinkWideDo;
import com.example.shortlink.app.util.DeviceUtil;
import com.example.shortlink.app.util.KafkaUtil;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

/**
 * @author 彭亮
 * @create 2023-01-17 20:37
 */
public class DwmShortLinkWideApp {

    /**
     * 定义source topic
     */
    public static final String SOURCE_TOPIC = "dwd_link_visit_topic";

    /**
     * 定义消费者组
     */
    private static final String GROUP_ID = "dwm_short_link_group";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

//        DataStream<String> ds = env.socketTextStream("192.168.200.140", 8888);

        FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC, GROUP_ID);

        DataStreamSource<String> ds = env.addSource(kafkaConsumer);

        // 格式转换，补齐设备信息
        SingleOutputStreamOperator<ShortLinkWideDo> deviceWideDS = ds.flatMap(new FlatMapFunction<String, ShortLinkWideDo>() {
            @Override
            public void flatMap(String value, Collector<ShortLinkWideDo> out) throws Exception {
                // 还原json对象
                JSONObject jsonObject = JSONObject.parseObject(value);
                // 获取ua
                String userAgent = jsonObject.getJSONObject("data").getString("user-agent");
                // 解析设备对象
                DeviceInfoDo deviceInfo = DeviceUtil.getDeviceInfo(userAgent);

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

                out.collect(shortLinkWideDo);

            }
        });

        deviceWideDS.print("设备信息宽表补齐");

        env.execute();

    }

}
