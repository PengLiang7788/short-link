package com.example.shortlink.link.service.impl;

import com.example.shortlink.common.enums.DomainTypeEnum;
import com.example.shortlink.common.enums.EventMessageType;
import com.example.shortlink.common.enums.ShortLinkStateEnum;
import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.common.model.EventMessage;
import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.common.util.IDUtil;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.common.util.JsonUtil;
import com.example.shortlink.link.component.ShortLinkComponent;
import com.example.shortlink.link.config.RabbitMQConfig;
import com.example.shortlink.link.controller.request.ShortLinkAddRequest;
import com.example.shortlink.link.controller.request.ShortLinkDelRequest;
import com.example.shortlink.link.controller.request.ShortLinkPageRequest;
import com.example.shortlink.link.controller.request.ShortLinkUpdateRequest;
import com.example.shortlink.link.manager.DomainManager;
import com.example.shortlink.link.manager.GroupCodeMappingManager;
import com.example.shortlink.link.manager.LinkGroupManager;
import com.example.shortlink.link.manager.ShortLinkManager;
import com.example.shortlink.link.model.DomainDo;
import com.example.shortlink.link.model.GroupCodeMappingDo;
import com.example.shortlink.link.model.LinkGroupDO;
import com.example.shortlink.link.model.ShortLinkDO;
import com.example.shortlink.link.service.ShortLinkService;
import com.example.shortlink.link.vo.ShortLinkVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.security.auth.Login;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author 彭亮
 * @create 2023-01-06 9:37
 */
@Service
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private ShortLinkManager shortLinkManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private DomainManager domainManager;

    @Autowired
    private LinkGroupManager linkGroupManager;

    @Autowired
    private ShortLinkComponent shortLinkComponent;

    @Autowired
    private GroupCodeMappingManager groupCodeMappingManager;

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

        String newOriginalUrl = CommonUtil.addUrlPrefix(shortLinkAddRequest.getOriginalUrl());
        shortLinkAddRequest.setOriginalUrl(newOriginalUrl);

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

    /**
     * 用于处理短链新增逻辑
     * 1、判断短链域名是否合法
     * 2、判断组名是否合法
     * 3、生成长链摘要
     * 4、生成短链码
     * 5、加锁
     * 6、查询短链码是否存在
     * 7、构建短链对象
     * 8、保存数据库
     *
     * @param eventMessage
     * @return
     */
    @Override
    public boolean handlerAddShortLink(EventMessage eventMessage) {

        Long accountNo = eventMessage.getAccountNo();
        String eventMessageType = eventMessage.getEventMessageType();

        ShortLinkAddRequest shortLinkAddRequest = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkAddRequest.class);
        // 短链域名校验
        DomainDo domainDo = checkDomain(shortLinkAddRequest.getDomainType(), shortLinkAddRequest.getDomainId(), accountNo);
        // 校验组是否合法
        LinkGroupDO linkGroupDO = checkLinkGroup(shortLinkAddRequest.getGroupId(), accountNo);

        // 长链摘要
        String originalUrlDigest = CommonUtil.MD5(shortLinkAddRequest.getOriginalUrl());

        // 短链码重复标记
        boolean duplicationCodeFlag = false;

        // 生成短链码
        String shortLinkCode = shortLinkComponent.createShortLinkCode(shortLinkAddRequest.getOriginalUrl());

        // 加锁
        String script = "if redis.call('EXISTS',KEYS[1])==0 then redis.call('set',KEYS[1],ARGV[1]); redis.call('expire',KEYS[1],ARGV[2]); return 1;" +
                " elseif redis.call('get',KEYS[1]) == ARGV[1] then return 2;" +
                " else return 0; end;";

        Long result = redisTemplate.execute(new
                DefaultRedisScript<>(script, Long.class), Arrays.asList(shortLinkCode), accountNo, 100);

        // 加锁成功
        if (result > 0) {
            // C端处理
            if (EventMessageType.SHORT_LINK_ADD_LINK.name().equalsIgnoreCase(eventMessageType)) {
                //先判断短链码是否被占用
                ShortLinkDO shortLinkCodeDoInDB = shortLinkManager.findByShortLinkCode(shortLinkCode);
                if (shortLinkCodeDoInDB == null) {
                    ShortLinkDO shortLinkDO = ShortLinkDO.builder().accountNo(accountNo)
                            .code(shortLinkCode).title(shortLinkAddRequest.getTitle())
                            .originalUrl(shortLinkAddRequest.getOriginalUrl())
                            .domain(domainDo.getValue()).groupId(linkGroupDO.getId())
                            .expired(shortLinkAddRequest.getExpired()).sign(originalUrlDigest)
                            .state(ShortLinkStateEnum.ACTIVE.name()).del(0).build();

                    shortLinkManager.addShortLink(shortLinkDO);

                    return true;
                } else {
                    log.error("C端短链码重复:{}", eventMessage);
                    duplicationCodeFlag = true;
                }

            } else if (EventMessageType.SHORT_LINK_ADD_MAPPING.name().equalsIgnoreCase(eventMessageType)) {
                GroupCodeMappingDo groupCodeMappingDoInDB = groupCodeMappingManager.findByCodeAndGroupId(shortLinkCode, linkGroupDO.getId(), accountNo);
                if (groupCodeMappingDoInDB == null) {
                    //B端处理
                    GroupCodeMappingDo groupCodeMappingDo = GroupCodeMappingDo.builder().accountNo(accountNo)
                            .code(shortLinkCode).title(shortLinkAddRequest.getTitle())
                            .originalUrl(shortLinkAddRequest.getOriginalUrl())
                            .domain(domainDo.getValue()).groupId(linkGroupDO.getId())
                            .expired(shortLinkAddRequest.getExpired()).sign(originalUrlDigest)
                            .state(ShortLinkStateEnum.ACTIVE.name()).del(0).build();
                    groupCodeMappingManager.add(groupCodeMappingDo);
                    return true;
                } else {
                    log.error("B端短链码重复:{}", eventMessage);
                    duplicationCodeFlag = true;
                }
            }
        } else {
            // 加锁失败 自旋100毫秒，在调用；失败的原因可能是短链码被占用，需要重新生成
            log.error("枷锁失败:{}", eventMessage);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
            }
            duplicationCodeFlag = true;
        }
        if (duplicationCodeFlag) {
            String newOriginalUrl = CommonUtil.addUrlPrefixVersion(shortLinkAddRequest.getOriginalUrl());
            shortLinkAddRequest.setOriginalUrl(newOriginalUrl);

            eventMessage.setContent(JsonUtil.obj2Json(shortLinkAddRequest));
            log.warn("短链码保存失败,重新生成:{}", eventMessage);
            handlerAddShortLink(eventMessage);
        }
        return false;
    }

    /**
     * 从B端查找 group_code_mapping
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> pageByGroupId(ShortLinkPageRequest request) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        Map<String, Object> result = groupCodeMappingManager
                .pageShortLinkByGroupId(request.getPage(), request.getSize(), accountNo, request.getGroupId());

        return result;
    }

    /**
     * 删除短链
     * @param delRequest
     * @return
     */
    @Override
    public JsonData del(ShortLinkDelRequest delRequest) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(delRequest))
                .messageId(IDUtil.generateSnowFlakeID().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_DEL.name())
                .build();
        // 发送消息
        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getShortLinkEventExchange()
                , rabbitMQConfig.getShortLinkDelRoutingKey(), eventMessage);

        return JsonData.buildSuccess();
    }

    /**
     * 更新短链
     * @param request
     * @return
     */
    @Override
    public JsonData update(ShortLinkUpdateRequest request) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.generateSnowFlakeID().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_UPDATE.name())
                .build();
        // 发送消息
        rabbitTemplate.convertAndSend(
                rabbitMQConfig.getShortLinkEventExchange()
                , rabbitMQConfig.getShortLinkUpdateRoutingKey(), eventMessage);

        return JsonData.buildSuccess();
    }

    /**
     * 校验域名是否合法
     *
     * @param domainType
     * @param domainId
     * @param accountNo
     * @return
     */
    private DomainDo checkDomain(String domainType, Long domainId, Long accountNo) {

        DomainDo domainDo = null;

        if (DomainTypeEnum.CUSTOM.name().equalsIgnoreCase(domainType)) {
            domainDo = domainManager.findById(domainId, accountNo);
        } else {
            domainDo = domainManager.findByDomainTypeAndId(domainId, DomainTypeEnum.OFFICIAL);
        }
        Assert.notNull(domainDo, "短链域名不合法");
        return domainDo;
    }

    /**
     * 判断组名是否合法
     *
     * @param groupId
     * @param accountNo
     * @return
     */
    private LinkGroupDO checkLinkGroup(Long groupId, Long accountNo) {
        LinkGroupDO linkGroupDO = linkGroupManager.detail(groupId, accountNo);
        Assert.notNull(linkGroupDO, "短链组名不合法");
        return linkGroupDO;
    }
}
