package com.example.shortlink.account.listener;

import com.example.shortlink.account.service.TrafficService;
import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.exception.BizException;
import com.example.shortlink.common.model.EventMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 彭亮
 * @create 2023-01-11 20:04
 */
@Component
@RabbitListener(queuesToDeclare = {
        @Queue("order.traffic.queue")
})
@Slf4j
public class TrafficMQListener {

    @Autowired
    private TrafficService trafficService;

    @RabbitHandler
    public void trafficHandler(EventMessage eventMessage, Message message, Channel channel) {
        log.info("监听到消息 trafficHandler:{}", eventMessage);
        try {
            trafficService.handleTrafficMessage(eventMessage);
        } catch (Exception e) {
            log.error("消费失败:{}", eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
        log.info("消费成功:{}", eventMessage);
    }

}
