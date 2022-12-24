package com.example.shortlink.account.controller;

import com.example.shortlink.account.service.NotifyService;
import com.example.shortlink.common.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 彭亮
 * @create 2022-12-23 13:46
 */
@RestController
@RequestMapping("/api/account/v1")
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    /**
     * 测试发送验证码接口
     * @return
     */
    @RequestMapping("send_code")
    public JsonData sendCode(){
        notifyService.testSend();
        return JsonData.buildSuccess();
    }

}
