package com.example.shortlink.account.service.impl;

import com.example.shortlink.account.manager.TrafficManager;
import com.example.shortlink.account.model.TrafficDO;
import com.example.shortlink.account.service.TrafficService;
import com.example.shortlink.account.vo.ProductVo;
import com.example.shortlink.common.enums.EventMessageType;
import com.example.shortlink.common.model.EventMessage;
import com.example.shortlink.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-11 20:06
 */
@Service
@Slf4j
public class TrafficServiceImpl implements TrafficService {

    @Autowired
    private TrafficManager trafficManager;


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void handleTrafficMessage(EventMessage eventMessage) {
        String messageType = eventMessage.getEventMessageType();
        if (EventMessageType.PRODUCT_ORDER_PAY.name().equalsIgnoreCase(messageType)) {
            // 订单已经支付，新增流量
            String content = eventMessage.getContent();
            Map<String, Object> orderInfoMap = JsonUtil.json2Obj(content, Map.class);
            // 还原商品订单商品信息
            Long accountNo = (Long) orderInfoMap.get("accountNo");
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
        }
    }
}
