package com.example.shortlink.account.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.shortlink.account.config.RabbitMQConfig;
import com.example.shortlink.account.controller.request.TrafficPageRequest;
import com.example.shortlink.account.controller.request.UseTrafficRequest;
import com.example.shortlink.account.feign.ProductFeignService;
import com.example.shortlink.account.feign.ShortLinkFeignService;
import com.example.shortlink.account.manager.TrafficManager;
import com.example.shortlink.account.manager.TrafficTaskManager;
import com.example.shortlink.account.model.TrafficDO;
import com.example.shortlink.account.model.TrafficTaskDO;
import com.example.shortlink.account.service.TrafficService;
import com.example.shortlink.account.vo.ProductVo;
import com.example.shortlink.account.vo.TrafficVo;
import com.example.shortlink.account.vo.UseTrafficVo;
import com.example.shortlink.common.constant.RedisKey;
import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.enums.EventMessageType;
import com.example.shortlink.common.enums.TaskStateEnum;
import com.example.shortlink.common.exception.BizException;
import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.common.model.EventMessage;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.common.util.JsonUtil;
import com.example.shortlink.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 彭亮
 * @create 2023-01-11 20:06
 */
@Service
@Slf4j
public class TrafficServiceImpl implements TrafficService {

    @Autowired
    private TrafficManager trafficManager;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private TrafficTaskManager trafficTaskManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private ShortLinkFeignService shortLinkFeignService;


    /**
     * 处理流量包消费业务
     *
     * @param eventMessage
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void handleTrafficMessage(EventMessage eventMessage) {
        Long accountNo = eventMessage.getAccountNo();

        String messageType = eventMessage.getEventMessageType();
        if (EventMessageType.PRODUCT_ORDER_PAY.name().equalsIgnoreCase(messageType)) {
            // 订单已经支付，新增流量
            String content = eventMessage.getContent();
            Map<String, Object> orderInfoMap = JsonUtil.json2Obj(content, Map.class);
            // 还原商品订单商品信息
            String outTradeNo = (String) orderInfoMap.get("outTradeNo");
            Integer buyNum = (Integer) orderInfoMap.get("buyNum");
            String productStr = (String) orderInfoMap.get("product");
            ProductVo productVo = JsonUtil.json2Obj(productStr, ProductVo.class);
            log.info("商品信息:{}", productVo);

            // 流量包过期时间
            LocalDateTime expiredDateTime = LocalDateTime.now().plusDays(productVo.getValidDay());
            Date date = Date.from(expiredDateTime.atZone(ZoneId.systemDefault()).toInstant());

            // 构建流量包对象
            TrafficDO trafficDO = TrafficDO.builder()
                    .accountNo(accountNo)
                    .dayLimit(productVo.getDayTimes() * buyNum)
                    .dayUsed(0)
                    .totalLimit(productVo.getTotalTimes())
                    .pluginType(productVo.getPluginType())
                    .level(productVo.getLevel())
                    .productId(productVo.getId())
                    .outTradeNo(outTradeNo)
                    .expiredDate(date).build();

            int rows = trafficManager.add(trafficDO);
            log.info("消费消息新增流量包 rows={},{}", rows, trafficDO);

            // 新增流量包应该删除这个key
            String totalTrafficTimeKey = String.format(RedisKey.DAY_TOTAL_TRAFFIC, accountNo);
            redisTemplate.delete(totalTrafficTimeKey);

        } else if (EventMessageType.TRAFFIC_FREE_INIT.name().equalsIgnoreCase(messageType)) {
            // 账户注册，发放免费流量包
            Long productId = Long.valueOf(eventMessage.getBizId());

            JsonData jsonData = productFeignService.detail(productId);
            ProductVo productVo = jsonData.getData(new TypeReference<ProductVo>() {
            });

            // 构建流量包对象
            TrafficDO trafficDO = TrafficDO.builder()
                    .accountNo(accountNo)
                    .dayLimit(productVo.getDayTimes())
                    .dayUsed(0)
                    .totalLimit(productVo.getTotalTimes())
                    .pluginType(productVo.getPluginType())
                    .level(productVo.getLevel())
                    .productId(productVo.getId())
                    .outTradeNo("free_init")
                    .expiredDate(new Date()).build();

            trafficManager.add(trafficDO);

        } else if (EventMessageType.TRAFFIC_USED.name().equalsIgnoreCase(messageType)) {
            // 流量包使用，检查是否使用
            // 检查task是否存在
            // 检查短链是否成功
            // 如果不成功则恢复流量包
            // 删除task(也可以更新task状态，定时删除就行)
            Long trafficTaskId = Long.valueOf(eventMessage.getBizId());
            TrafficTaskDO trafficTaskDO = trafficTaskManager.findByIdAndAccountNo(trafficTaskId, accountNo);

            // 非空且锁定
            if (trafficTaskDO != null && trafficTaskDO.getLockState().equalsIgnoreCase(TaskStateEnum.LOCK.name())) {

                JsonData jsonData = shortLinkFeignService.check(trafficTaskDO.getBizId());

                if (jsonData.getCode() != 0) {
                    log.error("创建短链失败，流量包回滚");

                    String useDateStr = TimeUtil.format(trafficTaskDO.getGmtCreate(), "yyyy-MM-dd");

                    trafficManager.releaseUsedTimes(accountNo, trafficTaskDO.getTrafficId(), 1, useDateStr);

                    // 恢复流量包应该删除这个key
                    String totalTrafficTimeKey = String.format(RedisKey.DAY_TOTAL_TRAFFIC, accountNo);
                    redisTemplate.delete(totalTrafficTimeKey);
                }
                // 可以更新状态，定时删除
                trafficTaskManager.deleteByIdAndAccountNo(trafficTaskId, accountNo);
            }


        }
    }

    /**
     * 分页查询流量包列表，查询可用的流量包
     *
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> pageAvailable(TrafficPageRequest request) {
        int size = request.getSize();
        int page = request.getPage();
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        IPage<TrafficDO> trafficDOIPage = trafficManager.pageAvailable(page, size, accountNo);
        // 获取流量包列表
        List<TrafficDO> records = trafficDOIPage.getRecords();

        List<TrafficVo> trafficVoList = records.stream().map(item -> beanProcess(item))
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("total_record", trafficDOIPage.getTotal());
        map.put("total_page", trafficDOIPage.getPages());
        map.put("current_data", trafficVoList);

        return map;
    }

    /**
     * 查找流量包详情
     *
     * @param trafficId
     * @return
     */
    @Override
    public TrafficVo detail(long trafficId) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        TrafficDO trafficDO = trafficManager.findByIdAndAccountNo(trafficId, accountNo);
        TrafficVo trafficVo = beanProcess(trafficDO);

        return trafficVo;
    }

    /**
     * 删除过期流量包
     *
     * @return
     */
    @Override
    public boolean deleteExpireTraffic() {
        return trafficManager.deleteExpireTraffic();
    }

    /**
     * 扣减流量包
     * <p>
     * 查询用户全部可用流量包
     * 遍历用户可用流量包
     * 判断是否更新 --> 用日期进行判断
     * 没更新的流量包加入【待更新集合】中
     * 增加【今日剩余可用总次数】
     * 已经更新的判断是否超过当天使用次数
     * 如果没有超过则增加 【今日剩余可用总次数】
     * 超过则忽略
     * 更新用户今日流量包相关数据
     * 扣减使用的某个流量包使用次数
     *
     * @param trafficRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public JsonData reduce(UseTrafficRequest trafficRequest) {
        Long accountNo = trafficRequest.getAccountNo();

        // 处理流量包，筛选出未更新流量包，当前使用流量包
        UseTrafficVo useTrafficVo = processTrafficList(accountNo);

        log.info("今天可用总次数:{},当前使用流量包:{}", useTrafficVo.getDayTotalLeftTimes(), useTrafficVo.getCurrentTrafficDo());

        if (useTrafficVo.getCurrentTrafficDo() == null) {
            return JsonData.buildResult(BizCodeEnum.TRAFFIC_REDUCE_FAIL);
        }

        log.info("待更新流量包列表:{}", useTrafficVo.getUnUpdateTrafficIds());

        if (useTrafficVo.getUnUpdateTrafficIds().size() > 0) {
            // 更新今日流量包
            trafficManager.batchUpdateUsedTimes(accountNo, useTrafficVo.getUnUpdateTrafficIds());
        }

        // 先更新再扣减当前使用的流量包
        int rows = trafficManager.addDayUsedTimes(accountNo, useTrafficVo.getCurrentTrafficDo().getId(), 1);

        TrafficTaskDO trafficTaskDO = TrafficTaskDO.builder().accountNo(accountNo).bizId(trafficRequest.getBizId())
                .useTimes(1).trafficId(useTrafficVo.getCurrentTrafficDo().getId())
                .lockState(TaskStateEnum.LOCK.name()).build();

        trafficTaskManager.add(trafficTaskDO);

        if (rows != 1) {
            throw new BizException(BizCodeEnum.TRAFFIC_REDUCE_FAIL);
        }

        // 往redis设置下总流量包次数，短链服务递减即可 如果有新增流量包，则删除这个key
        long leftSeconds = TimeUtil.getRemainSecondsOneDay(new Date());

        String totalTrafficTimeKey = String.format(RedisKey.DAY_TOTAL_TRAFFIC, accountNo);

        redisTemplate.opsForValue().set(totalTrafficTimeKey, useTrafficVo.getDayTotalLeftTimes() - 1, leftSeconds, TimeUnit.SECONDS);

        EventMessage trafficUSeEventMessage = EventMessage.builder().accountNo(accountNo).bizId(trafficTaskDO.getId() + "")
                .eventMessageType(EventMessageType.TRAFFIC_USED.name()).build();

        // 发送延迟消息，用于异常回滚
        rabbitTemplate.convertAndSend(rabbitMQConfig.getTrafficEventExchange(),
                rabbitMQConfig.getTrafficReleaseDelayRoutingKey(), trafficUSeEventMessage);

        return JsonData.buildSuccess();
    }

    /**
     * 处理流量包，筛选出未更新流量包，当前使用流量包
     *
     * @param accountNo
     * @return
     */
    private UseTrafficVo processTrafficList(Long accountNo) {
        // 获取全部流量包
        List<TrafficDO> trafficDOList = trafficManager.selectAvailableTraffics(accountNo);
        if (trafficDOList == null || trafficDOList.size() == 0) {
            throw new BizException(BizCodeEnum.TRAFFIC_EXCEPTION);
        }
        // 天剩余可用总次数
        Integer dayTotalLeftTimes = 0;

        // 当前使用
        TrafficDO currentTrafficDo = null;

        // 没过期,但是今天没更新的流量包列表
        List<Long> unUpdateTrafficIds = new ArrayList<>();

        // 今天日期
        String todayStr = TimeUtil.format(new Date(), "yyyy-MM-dd");

        for (TrafficDO trafficDO : trafficDOList) {
            String trafficUpdateDate = TimeUtil.format(trafficDO.getGmtModified(), "yyyy-MM-dd");
            if (todayStr.equalsIgnoreCase(trafficUpdateDate)) {
                // 已经更新 获取当前流量包还剩余的使用次数
                int dayLeftTimes = trafficDO.getDayLimit() - trafficDO.getDayUsed();

                //天剩余可用总次数 = 总次数 - 已用
                dayTotalLeftTimes = dayLeftTimes + dayTotalLeftTimes;

                // 选取当次使用的流量包
                if (dayLeftTimes > 0 && currentTrafficDo == null) {
                    currentTrafficDo = trafficDO;
                }

            } else {
                // 未更新
                dayTotalLeftTimes = dayTotalLeftTimes + trafficDO.getDayLimit();

                // 记录未更新的流量包
                unUpdateTrafficIds.add(trafficDO.getId());

                // 选取当此使用流量包
                if (currentTrafficDo == null) {
                    currentTrafficDo = trafficDO;
                }
            }
        }
        UseTrafficVo useTrafficVo = new UseTrafficVo();
        useTrafficVo.setCurrentTrafficDo(currentTrafficDo);
        useTrafficVo.setUnUpdateTrafficIds(unUpdateTrafficIds);
        useTrafficVo.setDayTotalLeftTimes(dayTotalLeftTimes);
        return useTrafficVo;
    }

    private TrafficVo beanProcess(TrafficDO trafficDO) {
        TrafficVo trafficVo = new TrafficVo();
        BeanUtils.copyProperties(trafficDO, trafficVo);

        //惰性更新，前端显示问题，根据更新时间进行判断是否需要显示最新的流量包
        String todayStr = TimeUtil.format(new Date(), "yyyy-MM-dd");
        String trafficUpdateStr = TimeUtil.format(trafficDO.getGmtModified(), "yyyy-MM-dd");

        if (!todayStr.equalsIgnoreCase(trafficUpdateStr)) {
            trafficVo.setDayUsed(0);
        }

        return trafficVo;

    }
}
