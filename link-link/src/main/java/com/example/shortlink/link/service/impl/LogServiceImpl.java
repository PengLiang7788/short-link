package com.example.shortlink.link.service.impl;

import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.link.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 彭亮
 * @create 2023-01-16 13:56
 */
@Service
@Slf4j
public class LogServiceImpl implements LogService {

    @Override
    public JsonData recordShortLinkLog(String msg) {

        log.info("这个是记录日志:{}",msg);

        return JsonData.buildSuccess();
    }
}
