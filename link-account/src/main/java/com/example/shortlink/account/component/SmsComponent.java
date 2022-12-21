package com.example.shortlink.account.component;

import com.example.shortlink.account.config.SmsConfig;
import com.example.shortlink.common.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 彭亮
 * @create 2022-12-21 15:25
 */
@Component
@Slf4j
public class SmsComponent {

    @Autowired
    private SmsConfig smsConfig;

    /**
     * 发送短信验证码
     * @param mobile
     * @param value
     */
    public void send(String mobile,String value){
        String host = "https://jmsms.market.alicloudapi.com";
        String path = "/sms/send";
        String method = "POST";
        String appcode = smsConfig.getAppCode();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", mobile);
        querys.put("templateId", smsConfig.getTemplateId());
        querys.put("value", value);
        Map<String, String> bodys = new HashMap<String, String>();

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()){
                log.info("发送短信成功,响应消息为:{}",EntityUtils.toString(response.getEntity()));
            }else {
                log.error("发送短信失败,相应消息为:{}",EntityUtils.toString(response.getEntity()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
















