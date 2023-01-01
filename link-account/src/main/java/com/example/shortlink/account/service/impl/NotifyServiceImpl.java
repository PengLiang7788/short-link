package com.example.shortlink.account.service.impl;

import com.example.shortlink.account.component.MailComponent;
import com.example.shortlink.account.component.SmsComponent;
import com.example.shortlink.account.config.SmsConfig;
import com.example.shortlink.account.service.NotifyService;
import com.example.shortlink.common.constant.RedisKey;
import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.enums.SendCodeEnum;
import com.example.shortlink.common.util.CheckUtil;
import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author 彭亮
 * @create 2022-12-23 13:47
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    /**
     * 验证码过期时间，单位：分钟
     */
    private static final int CODE_EXPIRED = 10;

    private static final String SUBJECT = "短链平台验证码";

    private static final String CONTENT = "您的验证码为%s,有效期为10分钟,打死也不要告诉别人";

    @Autowired
    private SmsComponent smsComponent;

    @Autowired
    private SmsConfig smsConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MailComponent mailComponent;

    /**
     * 发送验证码
     *
     * @param sendCodeEnum
     * @param to
     */
    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {
        // 校验是否重复发送验证码
        String cacheKey = String.format(RedisKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        // 如果不为空，再判断是否是60秒内重复发送 0123_23232321213
        if (StringUtils.isNotBlank(cacheValue)) {
            long ttl = Long.parseLong(cacheValue.split("_")[1]);
            // 当前时间戳-验证码发送的时间戳，如果小于60秒，则不给重复发送
            long leftTime = CommonUtil.getCurrentTimestamp() - ttl;
            if (leftTime < (1000 * 60)) {
                log.info("重复发送短信验证码，时间间隔:{}秒", leftTime);
                return JsonData.buildResult(BizCodeEnum.CODE_LIMITED);
            }
        }

        // 生成验证码
        String code = CommonUtil.getRandomCode(6);
        // 拼接生成key
        String value = code + "_" + CommonUtil.getCurrentTimestamp();
        // 存储到redis中
        redisTemplate.opsForValue().set(cacheKey,value,CODE_EXPIRED, TimeUnit.MINUTES);
        if (CheckUtil.isEmail(to)) {
            // 发送邮箱验证码
            mailComponent.sendMail(to,SUBJECT,String.format(CONTENT,code));

        } else if (CheckUtil.isPhone(to)) {
            // 发送手机验证码
            smsComponent.send(to, smsConfig.getTemplateId(), code);
        }
        return JsonData.buildSuccess();
    }

    /**
     * 校验验证码
     * @param sendCodeEnum
     * @param to
     * @param code
     * @return
     */
    @Override
    public boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code) {
        String cacheKey = String.format(RedisKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(cacheValue)){
            String cacheCode = cacheValue.split("_")[0];
            if (cacheCode.equalsIgnoreCase(code)){
                // 删除验证码
                redisTemplate.delete(cacheKey);
                return true;
            }
        }

        return false;
    }
}

























