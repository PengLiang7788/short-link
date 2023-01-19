package com.example.shortlink.app.util;

import com.example.shortlink.app.model.ShortLinkVisitStatsDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author 彭亮
 * @create 2023-01-19 12:12
 */
@Slf4j
public class MyClickHouseSink {

    /**
     * kafka的broker地址
     */
    private static String CLICK_HOUSE_SERVER = null;

    static {
        Properties properties = new Properties();

        InputStream is = MyClickHouseSink.class.getClassLoader().getResourceAsStream("application.properties");

        try {
            properties.load(is);

        } catch (IOException e) {
            log.error("加载ClickHouse配置文件失败:{}", e);
        }

        //获取key配置对应的value
        CLICK_HOUSE_SERVER = properties.getProperty("clickhouse.servers");
    }

    /**
     * 获取ClickHouseServer写入数据的sinkFunction
     *
     * @param sql
     * @return
     */
    public static SinkFunction getJdbcSink(String sql) {
        SinkFunction<ShortLinkVisitStatsDo> sinkFunction = JdbcSink.sink(sql, new JdbcStatementBuilder<ShortLinkVisitStatsDo>() {
                    @Override
                    public void accept(PreparedStatement ps, ShortLinkVisitStatsDo obj) throws SQLException {
                        ps.setObject(1, obj.getCode());
                        ps.setObject(2, obj.getReferer());
                        ps.setObject(3, obj.getIsNew());
                        ps.setObject(4, obj.getAccountNo());
                        ps.setObject(5, obj.getProvince());
                        ps.setObject(6, obj.getCity());
                        ps.setObject(7, obj.getIp());

                        ps.setObject(8, obj.getBrowserName());
                        ps.setObject(9, obj.getOs());
                        ps.setObject(10, obj.getDeviceType());

                        ps.setObject(11, obj.getPv());
                        ps.setObject(12, obj.getUv());
                        ps.setObject(13, obj.getStartTime());
                        ps.setObject(14, obj.getEndTime());
                        ps.setObject(15, obj.getVisitTime());

                    }
                },
                // 控制批量写入大小
                new JdbcExecutionOptions.Builder().withBatchSize(10).build()
                // 连接配置
                , new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withUrl(CLICK_HOUSE_SERVER)
                        .withDriverName("ru.yandex.clickhouse.ClickHouseDriver")
                        .withUsername("default")
                        .withPassword("pl1008666.").build());

        return sinkFunction;
    }

}
