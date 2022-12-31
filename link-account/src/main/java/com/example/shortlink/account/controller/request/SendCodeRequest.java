package com.example.shortlink.account.controller.request;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2022-12-31 16:15
 */
@Data
public class SendCodeRequest {

    /**
     * 接收前端的图形验证码
     */
    private String captcha;

    /**
     * 接收方
     */
    private String to;


}
