package com.example.shortlink.shop.controller;


import com.example.shortlink.common.constant.RedisKey;
import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.enums.CLientTypeEnum;
import com.example.shortlink.common.enums.ProductOrderPayTypeEnum;
import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.shop.annotation.RepeatSubmit;
import com.example.shortlink.shop.controller.request.ConfirmOrderRequest;
import com.example.shortlink.shop.controller.request.ProductOrderPageRequest;
import com.example.shortlink.shop.service.ProductOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 彭亮
 * @since 2023-01-08
 */
@RestController
@RequestMapping("/api/order/v1")
@Slf4j
public class ProductOrderController {

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 下单前获取令牌，用于防重提交
     *
     * @return
     */
    @GetMapping("/token")
    public JsonData getOrderToken() {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        String token = CommonUtil.getStringNumRandom(32);
        String key = String.format(RedisKey.SUBMIT_ORDER_TOKEN_KEY, accountNo, token);

        //令牌有效时间是三十分钟
        redisTemplate.opsForValue().set(key, String.valueOf(Thread.currentThread().getId()), 30, TimeUnit.MINUTES);

        return JsonData.buildSuccess(token);
    }

    /**
     * 分页接口
     *
     * @return
     */
    @PostMapping("/page")
    public JsonData page(@RequestBody ProductOrderPageRequest request) {

        Map<String, Object> pageResult =
                productOrderService.page(request);
        return JsonData.buildSuccess(pageResult);
    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo
     * @return
     */
    @GetMapping("/query_state")
    public JsonData queryState(@RequestParam(value = "out_trade_no") String outTradeNo) {
        String state = productOrderService.queryProductOrderState(outTradeNo);
        return StringUtils.isBlank(state) ? JsonData.buildResult(BizCodeEnum.ORDER_CONFIRM_NOT_EXIST) : JsonData.buildSuccess(state);
    }

    /**
     * 下单接口
     *
     * @param orderRequest
     * @param response
     */
    @PostMapping("/confirm")
    public void confirmOrder(@RequestBody ConfirmOrderRequest orderRequest, HttpServletResponse response) {

        JsonData jsonData = productOrderService.confirmOrder(orderRequest);

        if (jsonData.getCode() == 0) {
            // 支付成功
            // 获取支付终端类型
            String clientType = orderRequest.getClientType();
            // 获取支付类型
            String payType = orderRequest.getPayType();

            if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.ALI_PAY.name())) {
                // 支付宝支付需跳转网页 sdk除外
                if (clientType.equalsIgnoreCase(CLientTypeEnum.PC.name())) {
                    CommonUtil.sendHtmlMessage(response, jsonData);
                } else if (clientType.equalsIgnoreCase(CLientTypeEnum.APP.name())) {

                } else if (clientType.equalsIgnoreCase(CLientTypeEnum.H5.name())) {

                }
            } else if (payType.equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT_PAY.name())) {
                // 微信支付
                CommonUtil.sendJsonMessage(response, jsonData);
            }

        } else {
            // 支付失败
            log.error("创建订单失败{}", jsonData.toString());
            CommonUtil.sendJsonMessage(response, jsonData);
        }
    }

}

