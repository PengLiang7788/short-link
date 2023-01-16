package com.example.shortlink.app.dwd;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author 彭亮
 * @create 2023-01-16 15:19
 */
@Slf4j
public class DwdShortLinkLogApp {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        DataStreamSource<String> ds = env.socketTextStream("192.168.200.140", 8888);

        ds.print();

        env.execute();

    }

}
