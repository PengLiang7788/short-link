package com.example.shortlink.account.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.shortlink.account.controller.request.TrafficPageRequest;
import com.example.shortlink.account.feign.ProductFeignService;
import com.example.shortlink.account.manager.TrafficManager;
import com.example.shortlink.account.model.TrafficDO;
import com.example.shortlink.account.service.TrafficService;
import com.example.shortlink.account.vo.ProductVo;
import com.example.shortlink.account.vo.TrafficVo;
import com.example.shortlink.common.enums.EventMessageType;
import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.common.model.EventMessage;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        List<TrafficVo> trafficVoList = records.stream().map(item -> {
            TrafficVo trafficVo = new TrafficVo();
            BeanUtils.copyProperties(item, trafficVo);
            return trafficVo;
        }).collect(Collectors.toList());

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
        TrafficVo trafficVo = new TrafficVo();
        BeanUtils.copyProperties(trafficDO, trafficVo);

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
}
