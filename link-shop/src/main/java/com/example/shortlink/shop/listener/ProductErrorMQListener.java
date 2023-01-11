package com.example.shortlink.shop.listener;

import com.example.shortlink.common.model.EventMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

/**
 * @author 彭亮
 * @create 2023-01-10 11:10
 */
@Configuration
@Slf4j
@RabbitListener(queuesToDeclare = {@Queue("order.error.queue")})
public class ProductErrorMQListener {

    @RabbitHandler
    public void productOrderHandler(EventMessage eventMessage, Message message, Channel channel){
        log.info("告警: 监听到ShortLinkErrorMQListener eventMessage 消息内容:{}", eventMessage);
        log.info("告警: Message 消息内容:{}", message);
        log.error("告警成功，发送通知短信");
    }
}
