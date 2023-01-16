package com.example.shortlink.app.dwd;

import com.example.shortlink.app.util.KafkaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

/**
 * @author 彭亮
 * @create 2023-01-16 15:19
 */
@Slf4j
public class DwdShortLinkLogApp {

    /**
     * 定义topic
     */
    public static final String SOURCE_TOPIC = "ods_link_visit_topic";

    /**
     * 定义消费者组
     */
    private static final String GROUP_ID = "dwd_short_link_group";

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        FlinkKafkaConsumer<String> kafkaConsumer = KafkaUtil.getKafkaConsumer(SOURCE_TOPIC, GROUP_ID);

        DataStreamSource<String> ds = env.addSource(kafkaConsumer);

        ds.print();

        env.execute();

    }

}
