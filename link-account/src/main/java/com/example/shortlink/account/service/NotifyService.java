package com.example.shortlink.account.service;

import com.example.shortlink.common.enums.SendCodeEnum;
import com.example.shortlink.common.util.JsonData;

/**
 * @author 彭亮
 * @create 2022-12-23 13:47
 */
public interface NotifyService {

    /**
     * 发送验证码
     * @param userRegister
     * @param to
     */
    JsonData sendCode(SendCodeEnum userRegister, String to);
}
