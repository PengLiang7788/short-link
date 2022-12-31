package com.example.shortlink.common.constant;

/**
 * @author 彭亮
 * @create 2022-12-31 16:44
 */
public class RedisKey {
    /**
     * 验证码缓存key: 第一个是类型，第二个是唯一标识(手机号或邮箱)
     */
    public static final String CHECK_CODE_KEY = "code:%s:%s";

}
