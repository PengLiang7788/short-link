package com.example.shortlink.link.service;

import com.example.shortlink.common.util.JsonData;

/**
 * @author 彭亮
 * @create 2023-01-16 13:56
 */
public interface LogService {

    JsonData recordShortLinkLog(String msg);

}
