package com.example.shortlink.account.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author 彭亮
 * @create 2022-12-31 17:46
 */
@Component
@Slf4j
public class MailComponent {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    /**
     * 发送邮件
     * @param to  接收方
     * @param subject 主题
     * @param content 内容
     */
    public void sendMail(String to, String subject, String content){
        // 1、创建一个邮件消息对象
        SimpleMailMessage message = new SimpleMailMessage();

        // 2、设置相关属性
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom(from);

        // 3、发送邮件
        mailSender.send(message);

        log.info("邮件发送成功：{}",message.toString());
    }

}
