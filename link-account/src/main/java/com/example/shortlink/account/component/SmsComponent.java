package com.example.shortlink.account.component;

import com.example.shortlink.account.config.SmsConfig;
import com.example.shortlink.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;
/**
 * @author 彭亮
 * @create 2022-12-21 15:25
 */
@Component
@Slf4j
public class SmsComponent {

    /**
     * 发送地址
     */
    private static final String URL_TEMPLATE = "https://jmsms.market.alicloudapi.com/sms/send?mobile=%s&templateId=%s&value=%s";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsConfig smsConfig;

    /**
     * 发送短信接口
     * @param to
     * @param templateId
     * @param value
     */
    public void send(String to,String templateId,String value){
        String url = String.format(URL_TEMPLATE,to,templateId,value);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","APPCODE "+smsConfig.getAppCode());
        HttpEntity entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if(response.getStatusCode().is2xxSuccessful()){
            log.info("发送短信验证码成功");
        }else {
            log.error("发送短信验证码失败:{}",response.getBody());
        }

    }

}