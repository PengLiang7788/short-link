package com.example.shortlink.account.controller;

import com.example.shortlink.account.service.NotifyService;
import com.example.shortlink.common.util.CommonUtil;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author 彭亮
 * @create 2022-12-23 13:46
 */
@RestController
@RequestMapping("/api/account/v1")
@Slf4j
public class NotifyController {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 验证码过期时间
     */
    private static final long CAPTCHA_CODE_EXPIRED = 10;

    @GetMapping("captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        String captchaText = captchaProducer.createText();
        log.info("验证码内容:{}", captchaText);

        redisTemplate.opsForValue().set(getCaptchaKey(request), captchaText, CAPTCHA_CODE_EXPIRED, TimeUnit.MINUTES);

        BufferedImage bufferedImage = captchaProducer.createImage(captchaText);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCaptchaKey(HttpServletRequest request) {
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String key = "account-service:captcha:" + CommonUtil.MD5(ip + userAgent);
        log.info("验证码key:{}", key);
        return key;
    }


}
