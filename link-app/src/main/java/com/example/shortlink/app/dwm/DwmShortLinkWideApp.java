package com.example.shortlink.app.dwm;

import com.alibaba.fastjson.JSONObject;
import com.example.shortlink.app.func.AsyncLocationRequestFunction;
import com.example.shortlink.app.func.DeviceMapFunction;
import com.example.shortlink.app.func.LocationMapFunction;
import com.example.shortlink.app.model.DeviceInfoDo;
import com.example.shortlink.app.model.ShortLinkWideDo;
import com.example.shortlink.app.util.DeviceUtil;
import com.example.shortlink.app.util.KafkaUtil;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

import java.util.concurrent.TimeUnit;

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

    /**
     * 定义输出
     */
    public static final String SINK_TOPIC = "dwm_link_visit_topic";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

//        DataStream<String> ds = env.socketTextStream("192.168.200.140", 8888);

        // 获取流
        FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC, GROUP_ID);

        DataStreamSource<String> ds = env.addSource(kafkaConsumer);

        // 格式转换，补齐设备信息
        SingleOutputStreamOperator<ShortLinkWideDo> deviceWideDS = ds.map(new DeviceMapFunction());

        deviceWideDS.print("设备信息宽表补齐");

        // 补齐地理位置信息
//        SingleOutputStreamOperator<String> shortLinkWideDs = deviceWideDS.map(new LocationMapFunction());

        SingleOutputStreamOperator<String> shortLinkWideDs =
                AsyncDataStream.unorderedWait(deviceWideDS, new AsyncLocationRequestFunction(), 1000, TimeUnit.MILLISECONDS, 200);

        shortLinkWideDs.print("地理位置信息宽表补齐");

        FlinkKafkaProducer<String> kafkaProducer = KafkaUtil.getKafkaProducer(SINK_TOPIC);

        // 将sink写到dwm层，kafka存储
        shortLinkWideDs.addSink(kafkaProducer);

        env.execute();

    }

}
