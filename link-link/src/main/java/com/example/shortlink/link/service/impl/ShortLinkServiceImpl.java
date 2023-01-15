package com.example.shortlink.link.service.impl;

import com.example.shortlink.common.constant.RedisKey;
import com.example.shortlink.common.enums.BizCodeEnum;
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
import com.example.shortlink.link.controller.request.*;
import com.example.shortlink.link.feign.TrafficFeignService;
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

    @Autowired
    private TrafficFeignService trafficFeignService;

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

        // 需要预先检查下是否有足够多的流量包可以进行创建
        String cacheKey = String.format(RedisKey.DAY_TOTAL_TRAFFIC, accountNo);

        // 检查下key是否存在，然后递减，是否大于等于0，使用lua脚本
        // 如果key不存在，则表示今天未使用过，lua返回值是0，新增流量包的时候，不用重新计算次数，直接删除key，消费的时候会计算更新
        String script = "if redis.call('get',KEYS[1]) then return redis.call('decr',KEYS[1]) else return 0 end";

        // 获取剩余的次数
        Long leftTime = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(cacheKey), "");
        log.info("今日流量包剩余次数:{}", leftTime);

        if (leftTime >= 0) {
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
        } else {
            return JsonData.buildResult(BizCodeEnum.TRAFFIC_REDUCE_FAIL);
        }


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
    public boolean handleAddShortLink(EventMessage eventMessage) {

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
                    boolean reduceFlag = reduceTraffic(eventMessage, shortLinkCode);
                    if (reduceFlag) {
                        ShortLinkDO shortLinkDO = ShortLinkDO.builder().accountNo(accountNo)
                                .code(shortLinkCode).title(shortLinkAddRequest.getTitle())
                                .originalUrl(shortLinkAddRequest.getOriginalUrl())
                                .domain(domainDo.getValue()).groupId(linkGroupDO.getId())
                                .expired(shortLinkAddRequest.getExpired()).sign(originalUrlDigest)
                                .state(ShortLinkStateEnum.ACTIVE.name()).del(0).build();

                        shortLinkManager.addShortLink(shortLinkDO);

                        return true;
                    }

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
            handleAddShortLink(eventMessage);
        }
        return false;
    }

    /**
     * 扣减流量包
     *
     * @param eventMessage
     * @param shortLinkCode
     * @return
     */
    private boolean reduceTraffic(EventMessage eventMessage, String shortLinkCode) {

        UseTrafficRequest useTrafficRequest = UseTrafficRequest.builder().accountNo(eventMessage.getAccountNo())
                .bizId(shortLinkCode).build();

        JsonData jsonData = trafficFeignService.useTraffic(useTrafficRequest);

        if (jsonData.getCode() != 0) {
            log.error("流量包不足，扣减失败:{}", eventMessage);
            return false;
        }

        return true;
    }

    /**
     * 处理更新短链消息
     *
     * @param eventMessage
     * @return
     */
    @Override
    public boolean handleUpdateShortLink(EventMessage eventMessage) {
        Long accountNo = eventMessage.getAccountNo();
        String messageType = eventMessage.getEventMessageType();
        ShortLinkUpdateRequest request = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkUpdateRequest.class);
        // 校验短链域名是否合法
        DomainDo domainDo = checkDomain(request.getDomainType(), request.getDomainId(), accountNo);

        if (EventMessageType.SHORT_LINK_UPDATE_LINK.name().equalsIgnoreCase(messageType)) {
            // C端更新
            ShortLinkDO shortLinkDO = ShortLinkDO.builder().code(request.getCode()).title(request.getTitle())
                    .accountNo(accountNo).domain(domainDo.getValue()).build();
            int rows = shortLinkManager.update(shortLinkDO);
            log.debug("更新C端短链，rows={}", rows);
            return true;
        } else if (EventMessageType.SHORT_LINK_UPDATE_MAPPING.name().equalsIgnoreCase(messageType)) {
            // B端更新
            GroupCodeMappingDo groupCodeMappingDo = GroupCodeMappingDo.builder().id(request.getMappingId())
                    .accountNo(accountNo).groupId(request.getGroupId())
                    .title(request.getTitle()).domain(domainDo.getValue()).build();

            int rows = groupCodeMappingManager.update(groupCodeMappingDo);
            log.debug("更新B端短链，rows={}", rows);
            return true;
        }

        return false;
    }

    /**
     * 处理删除短链消息
     *
     * @param eventMessage
     * @return
     */
    @Override
    public boolean handleDelShortLink(EventMessage eventMessage) {
        Long accountNo = eventMessage.getAccountNo();
        String eventMessageType = eventMessage.getEventMessageType();

        ShortLinkDelRequest request = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkDelRequest.class);

        if (EventMessageType.SHORT_LINK_DEL_LINK.name().equalsIgnoreCase(eventMessageType)) {
            // C端删除
            ShortLinkDO shortLinkDO = ShortLinkDO.builder().code(request.getCode()).accountNo(accountNo).build();
            int rows = shortLinkManager.del(shortLinkDO);
            log.debug("删除C端短链，rows={}", rows);
            return true;
        } else if (EventMessageType.SHORT_LINK_DEL_MAPPING.name().equalsIgnoreCase(eventMessageType)) {
            // B端删除
            GroupCodeMappingDo groupCodeMappingDo = GroupCodeMappingDo.builder().groupId(request.getGroupId())
                    .accountNo(accountNo).id(request.getMappingId()).build();
            int rows = groupCodeMappingManager.del(groupCodeMappingDo);
            log.debug("删除B端短链，rows={}", rows);
            return true;
        }
        return false;
    }

    /**
     * 从B端查找 group_code_mapping
     *
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
     *
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
     *
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
