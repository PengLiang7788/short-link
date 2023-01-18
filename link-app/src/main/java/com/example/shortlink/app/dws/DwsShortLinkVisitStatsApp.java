package com.example.shortlink.app.dws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.shortlink.app.model.ShortLinkVisitStatsDo;
import com.example.shortlink.app.util.KafkaUtil;
import com.example.shortlink.app.util.TimeUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple9;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * @author 彭亮
 * @create 2023-01-18 19:12
 */
public class DwsShortLinkVisitStatsApp {

    /**
     * 宽表
     */
    public static final String SHORT_LINK_SOURCE_TOPIC = "dwm_link_visit_topic";

    public static final String SHORT_LINK_SOURCE_GROUP = "dws_link_visit_topic";

    /**
     * uv的数据流
     */
    public static final String UNIQUE_VISITOR_SOURCE_TOPIC = "dwm_unique_visitor_topic";

    public static final String UNIQUE_VISITOR_SOURCE_GROUP = "dwm_unique_visitor_topic";


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        //1、获取多个数据
        FlinkKafkaConsumer<String> shortLinkSource =
                KafkaUtil.getKafkaConsumer(SHORT_LINK_SOURCE_TOPIC, SHORT_LINK_SOURCE_GROUP);
        // 第一个流
        DataStreamSource<String> shortLinkDS = env.addSource(shortLinkSource);

        FlinkKafkaConsumer<String> uniqueVisitorSource =
                KafkaUtil.getKafkaConsumer(UNIQUE_VISITOR_SOURCE_TOPIC, UNIQUE_VISITOR_SOURCE_GROUP);
        // 第二个流
        DataStreamSource<String> uniqueVisitorDS = env.addSource(uniqueVisitorSource);

        //2、结构转换 uniqueVisitorDS、shortLinkDS
        SingleOutputStreamOperator<ShortLinkVisitStatsDo> shortLinkMapDS = shortLinkDS.map(new MapFunction<String, ShortLinkVisitStatsDo>() {
            @Override
            public ShortLinkVisitStatsDo map(String value) throws Exception {
                ShortLinkVisitStatsDo visitStatsDo = parseVisitStats(value);
                visitStatsDo.setPv(1L);
                visitStatsDo.setUv(0L);
                return visitStatsDo;
            }
        });

        SingleOutputStreamOperator<ShortLinkVisitStatsDo> uniqueVisitorMapDS = uniqueVisitorDS.map(new MapFunction<String, ShortLinkVisitStatsDo>() {
            @Override
            public ShortLinkVisitStatsDo map(String value) throws Exception {
                ShortLinkVisitStatsDo visitStatsDo = parseVisitStats(value);
                visitStatsDo.setPv(0L);
                visitStatsDo.setUv(1L);
                return visitStatsDo;
            }
        });

        //3、多流合并 (合并相同结构的流)
        DataStream<ShortLinkVisitStatsDo> unionDS = shortLinkMapDS.union(uniqueVisitorMapDS);

        //4、设置WaterMark
        SingleOutputStreamOperator<ShortLinkVisitStatsDo> watermarksDS = unionDS.assignTimestampsAndWatermarks(WatermarkStrategy
                // 指定允许最大乱序延迟3秒
                .<ShortLinkVisitStatsDo>forBoundedOutOfOrderness(Duration.ofSeconds(3))
                // 指定时间时间列，毫秒
                .withTimestampAssigner((event, timestamp) -> event.getVisitTime()));

        //5、多维度、多字段分组
        KeyedStream<ShortLinkVisitStatsDo, Tuple9<String, String, Integer, String, String, String, String, String, String>> keyedStream =
                watermarksDS.keyBy(new KeySelector<ShortLinkVisitStatsDo, Tuple9<String, String, Integer, String, String, String, String, String, String>>() {
                    @Override
                    public Tuple9<String, String, Integer, String, String, String, String, String, String> getKey(ShortLinkVisitStatsDo obj) throws Exception {
                        return Tuple9.of(obj.getCode(), obj.getReferer(), obj.getIsNew(),
                                obj.getProvince(), obj.getCity(), obj.getIp(),
                                obj.getBrowserName(), obj.getOs(), obj.getDeviceType());
                    }
                });

        //6、开窗 10秒一次数据插入到 ck
        WindowedStream<ShortLinkVisitStatsDo, Tuple9<String, String, Integer, String, String, String, String, String, String>, TimeWindow> windowedStream =
                keyedStream.window(TumblingEventTimeWindows.of(Time.seconds(10)));

        //7、聚合统计(补充统计起止时间)
        SingleOutputStreamOperator<Object> reduceDS = windowedStream.reduce(new ReduceFunction<ShortLinkVisitStatsDo>() {
            @Override
            public ShortLinkVisitStatsDo reduce(ShortLinkVisitStatsDo value1, ShortLinkVisitStatsDo value2) throws Exception {
                value1.setPv(value1.getPv() + value2.getPv());
                value1.setUv(value1.getUv() + value2.getUv());

                return value1;
            }
        }, new ProcessWindowFunction<ShortLinkVisitStatsDo, Object, Tuple9<String, String, Integer, String, String, String, String, String, String>, TimeWindow>() {
            @Override
            public void process(Tuple9<String, String, Integer, String, String, String, String, String, String> tuple,
                                Context context, Iterable<ShortLinkVisitStatsDo> elements, Collector<Object> out) throws Exception {

                for (ShortLinkVisitStatsDo visitStatsDo : elements) {
                    // 窗口开始和结束时间
                    String startTime = TimeUtil.formatWithTime(context.window().getStart());
                    String endTime = TimeUtil.formatWithTime(context.window().getEnd());
                    visitStatsDo.setStartTime(startTime);
                    visitStatsDo.setStartTime(endTime);

                    out.collect(visitStatsDo);
                }
            }
        });

        reduceDS.print();

        //8、输出到ClickHouse

        env.execute();
    }

    private static ShortLinkVisitStatsDo parseVisitStats(String value) {

        JSONObject jsonObj = JSON.parseObject(value);

        ShortLinkVisitStatsDo visitStatsDO = ShortLinkVisitStatsDo.builder()
                .code(jsonObj.getString("code"))
                .accountNo(jsonObj.getLong("accountNo"))
                .visitTime(jsonObj.getLong("visitTime"))
                .referer(jsonObj.getString("referer"))
                .isNew(jsonObj.getInteger("isNew"))
                .udid(jsonObj.getString("udid"))

                //地理位置信息
                .province(jsonObj.getString("province"))
                .city(jsonObj.getString("city"))
                .isp(jsonObj.getString("isp"))
                .ip(jsonObj.getString("ip"))

                //设备信息
                .browserName(jsonObj.getString("browserName"))
                .os(jsonObj.getString("os"))
                .osVersion(jsonObj.getString("osVersion"))
                .deviceType(jsonObj.getString("deviceType"))

                .build();

        return visitStatsDO;
    }


}
