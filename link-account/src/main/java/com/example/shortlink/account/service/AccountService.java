package com.example.shortlink.account.service;

import com.example.shortlink.account.controller.request.AccountRegisterRequest;
import com.example.shortlink.common.util.JsonData;

/**
 * @author 彭亮
 * @create 2022-12-21 14:37
 */
public interface AccountService {
    /**
     * 用户注册
     * @param registerRequest
     * @return
     */
    JsonData register(AccountRegisterRequest registerRequest);
}
