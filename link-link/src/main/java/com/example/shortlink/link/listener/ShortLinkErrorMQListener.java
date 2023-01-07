package com.example.shortlink.link.listener;

import com.example.shortlink.common.model.EventMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author 彭亮
 * @create 2023-01-06 18:55
 */
@Component
@Slf4j
@RabbitListener(queues = "short_link.error.queue")
public class ShortLinkErrorMQListener {

    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.info("告警: 监听到ShortLinkErrorMQListener eventMessage 消息内容:{}", eventMessage);
        log.info("告警: Message 消息内容:{}", message);
        log.error("告警成功，发送通知短信");
    }

}
