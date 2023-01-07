package com.example.shortlink.link.service.impl;

import com.example.shortlink.common.enums.EventMessageType;
import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.common.model.EventMessage;
import com.example.shortlink.common.util.IDUtil;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.common.util.JsonUtil;
import com.example.shortlink.link.config.RabbitMQConfig;
import com.example.shortlink.link.controller.request.ShortLinkAddRequest;
import com.example.shortlink.link.manager.ShortLinkManager;
import com.example.shortlink.link.model.ShortLinkDO;
import com.example.shortlink.link.service.ShortLinkService;
import com.example.shortlink.link.vo.ShortLinkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 彭亮
 * @create 2023-01-06 9:37
 */
@Service
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

    @Autowired
    private ShortLinkManager shortLinkManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    /**
     * 解析短链
     *
     * @param shortLinkCode
     * @return
     */
    @Override
    public ShortLinkVo parseShortLinkCode(String shortLinkCode) {
        ShortLinkDO shortLinkDO = shortLinkManager.findByShortLinkCode(shortLinkCode);
        if (shortLinkDO == null) {
            return null;
        }
        ShortLinkVo shortLinkVo = new ShortLinkVo();
        BeanUtils.copyProperties(shortLinkDO, shortLinkVo);

        return shortLinkVo;
    }

    /**
     * 创建短链
     *
     * @param shortLinkAddRequest
     * @return
     */
    @Override
    public JsonData createShortLink(ShortLinkAddRequest shortLinkAddRequest) {

        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(shortLinkAddRequest))
                .messageId(IDUtil.generateSnowFlakeID().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_ADD.name())
                .build();

        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getShortLinkEventExchange()
                , rabbitMQConfig.getShortLinkAddRoutingKey(), eventMessage);
        return JsonData.buildSuccess();
    }
}
