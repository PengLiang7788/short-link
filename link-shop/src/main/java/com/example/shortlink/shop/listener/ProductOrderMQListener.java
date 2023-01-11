package com.example.shortlink.shop.listener;

import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.exception.BizException;
import com.example.shortlink.common.model.EventMessage;
import com.example.shortlink.shop.service.ProductOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author 彭亮
 * @create 2023-01-10 11:10
 */
@Configuration
@Slf4j
@RabbitListener(queuesToDeclare = {
        @Queue("order.close.queue"),
        @Queue("order.update.queue")})
public class ProductOrderMQListener {

    @Autowired
    private ProductOrderService productOrderService;

    @RabbitHandler
    public void productOrderHandler(EventMessage eventMessage, Message message, Channel channel) {
        log.info("监听到消息 ProductOrderMQListener message的消息内容:{}", message);
        try {

            productOrderService.handleProductOrderMessage(eventMessage);

        } catch (Exception e) {
            log.error("消费失败:{}", eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
        log.info("消费成功:{}", eventMessage);
    }
}
