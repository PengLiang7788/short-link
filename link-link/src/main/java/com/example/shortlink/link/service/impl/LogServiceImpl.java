package com.example.shortlink.link.service.impl;

import com.example.shortlink.common.enums.LogTypeEnum;
import com.example.shortlink.common.model.LogRecord;
import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.common.util.JsonUtil;
import com.example.shortlink.link.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-16 13:56
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService {

    private static final String TOPIC_NAME = "ods_link_visit_topic";

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public void recordShortLinkLog(HttpServletRequest request, String shortLinkCode, Long accountNo) {

        // ip信息
        String ip = CommonUtil.getIpAddr(request);
        // 全部请求头
        Map<String, String> headerMap = CommonUtil.getAllRequestHeader(request);

        Map<String, String> availableMap = new HashMap<String, String>() {{
            put("user-agent", headerMap.get("user-agent"));
            put("referer", headerMap.get("referer"));
            put("accountNo", accountNo.toString());
        }};

        LogRecord logRecord = LogRecord.builder()
                // 日志类型
                .event(LogTypeEnum.SHORT_LINK_TYPE.name())
                // 日志内容
                .data(availableMap)
                // 客户端ip
                .ip(ip)
                // 产生时间
                .ts(CommonUtil.getCurrentTimestamp())
                // 业务唯一标识
                .bizId(shortLinkCode).build();

        String jsonLog = JsonUtil.obj2Json(logRecord);
        // 打印控制台 方便排查
        log.info(jsonLog);

        // 发送kafka
        kafkaTemplate.send(TOPIC_NAME,jsonLog);

    }
}
