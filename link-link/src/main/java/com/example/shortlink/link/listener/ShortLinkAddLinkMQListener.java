package com.example.shortlink.link.listener;

import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.enums.EventMessageType;
import com.example.shortlink.common.exception.BizException;
import com.example.shortlink.common.model.EventMessage;
import com.example.shortlink.link.service.ShortLinkService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author 彭亮
 * @create 2023-01-06 18:55
 */
@Component
@Slf4j
@RabbitListener(queues = "short_link.add.link.queue")
public class ShortLinkAddLinkMQListener {

    @Autowired
    private ShortLinkService shortLinkService;

    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.info("监听到ShortLinkAddLinkMQListener message消息内容:{}",message);
        try{
            eventMessage.setEventMessageType(EventMessageType.SHORT_LINK_ADD_LINK.name());
            shortLinkService.handleAddShortLink(eventMessage);

        }catch (Exception e){
            //处理业务异常，还有进行其他操作，比如记录失败原因
            log.error("消费失败:{}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
        log.info("消费成功:{}",message);
    }

}
