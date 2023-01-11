package com.example.shortlink.shop.service.impl;

import com.example.shortlink.common.constant.TImeConstant;
import com.example.shortlink.common.enums.*;
import com.example.shortlink.common.exception.BizException;
import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.common.model.EventMessage;
import com.example.shortlink.common.model.LoginUser;
import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.common.util.JsonUtil;
import com.example.shortlink.shop.component.PayFactory;
import com.example.shortlink.shop.config.RabbitMQConfig;
import com.example.shortlink.shop.controller.request.ConfirmOrderRequest;
import com.example.shortlink.shop.controller.request.ProductOrderPageRequest;
import com.example.shortlink.shop.manager.ProductManager;
import com.example.shortlink.shop.manager.ProductOrderManager;
import com.example.shortlink.shop.model.ProductDo;
import com.example.shortlink.shop.model.ProductOrderDo;
import com.example.shortlink.shop.service.ProductOrderService;
import com.example.shortlink.shop.vo.PayInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-09 13:26
 */
@Service
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private PayFactory payFactory;

    @Autowired
    private ProductOrderManager productOrderManager;

    @Autowired
    private ProductManager productManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    /**
     * 分页接口
     *
     * @return
     */
    @Override
    public Map<String, Object> page(ProductOrderPageRequest request) {

        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        Map<String, Object> pageResult = productOrderManager.page(request.getPage(), request.getSize(), accountNo, request.getState());
        return pageResult;
    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo
     * @return
     */
    @Override
    public String queryProductOrderState(String outTradeNo) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        ProductOrderDo productOrderDo = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);
        if (productOrderDo == null) {
            return "";
        } else {
            return productOrderDo.getState();
        }
    }

    /**
     * 确认订单
     * <p>
     * 防重提交
     * 获取最新的流量包价格
     * 订单验价
     * 如果有优惠券或者其他折扣
     * 验证前端显示和后台计算的价格
     * 创建订单对象保存数据库
     * 发送延迟消息-用于自动关单
     * 创建支付信息-对接三方支付
     * 回调更新订单状态
     * 支付成功创建流量包
     *
     * @param orderRequest
     * @return
     */
    @Override
    @Transactional
    public JsonData confirmOrder(ConfirmOrderRequest orderRequest) {

        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        // 生成订单号
        String orderOutTradeNo = CommonUtil.getStringNumRandom(32);

        ProductDo productDo = productManager.findDetailById(orderRequest.getProductId());

        // 验证价格
        this.checkPrice(productDo, orderRequest);

        // 创建订单
        ProductOrderDo productOrderDo = this.saveProductOrder(orderRequest, loginUser, orderOutTradeNo, productDo);

        // 创建支付信息
        PayInfoVo payInfoVo = PayInfoVo.builder().accountNo(loginUser.getAccountNo()).outTradeNo(orderOutTradeNo)
                .clientType(orderRequest.getClientType()).payType(orderRequest.getPayType())
                .title(productDo.getTitle()).description("")
                .payFee(orderRequest.getPayAmount())
                .orderPayTimeoutMills(TImeConstant.ORDER_PAY_TIMEOUT_MILLS).build();

        // 发送延迟消息
        EventMessage eventMessage = EventMessage.builder().eventMessageType(EventMessageType.PRODUCT_ORDER_NEW.name())
                .accountNo(loginUser.getAccountNo())
                .bizId(orderOutTradeNo).build();
        rabbitTemplate.convertAndSend(rabbitMQConfig.getOrderEventExchange(), rabbitMQConfig.getOrderCloseDelayRoutingKey(), eventMessage);

        // 对接支付信息
        String codeUrl = payFactory.pay(payInfoVo);
        if (StringUtils.isNotBlank(codeUrl)) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("code_url", codeUrl);
            resultMap.put("out_trade_no", payInfoVo.getOutTradeNo());
            return JsonData.buildSuccess(resultMap);
        }

        return JsonData.buildResult(BizCodeEnum.PAY_ORDER_FAIL);
    }

    /**
     * 关闭订单
     * 延迟消息的时间需要比订单过期的时间长一点，这样就不存在查询的时候用户还能支付成功
     * 查询订单是否存在，如果已经支付则正常结束
     * 如果订单未支付，主动调用第三方支付平台查询订单状态
     * 确认未支付，本地取消订单
     * 如果第三方平台已经支付，主动的把订单状态改成已支付，造成该原因的情况可能是支付通道回调有问题，然后触发支付后的动作。
     * 触发方式：RPC or MQ？
     *
     * @param eventMessage
     * @return
     */
    @Override
    public boolean closeProductOrder(EventMessage eventMessage) {
        String outTradeNo = eventMessage.getBizId();
        Long accountNo = eventMessage.getAccountNo();

        ProductOrderDo productOrderDo = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);
        if (productOrderDo == null) {
            // 订单不存在
            log.warn("订单不存在");
            return true;
        }

        if (productOrderDo.getState().equalsIgnoreCase(ProductOrderStateEnum.PAY.name())) {
            // 已经支付
            log.info("直接确认消息，订单已经支付:{}", eventMessage);
            return true;
        }

        if (productOrderDo.getState().equalsIgnoreCase(ProductOrderStateEnum.NEW.name())) {
            // 未支付，需要向第三方支付平台进行查询状态
            PayInfoVo payInfoVo = new PayInfoVo();
            payInfoVo.setPayType(productOrderDo.getPayType());
            payInfoVo.setOutTradeNo(outTradeNo);
            payInfoVo.setAccountNo(accountNo);

            //TODO 向第三方支付平台查询订单状态
            String payResult = "";
            if (StringUtils.isBlank(payResult)) {
                // 如果为空 则未支付成功 本地取消订单
                productOrderManager.updateOrderPayState(outTradeNo, accountNo,
                        ProductOrderStateEnum.CANCEL.name(), ProductOrderStateEnum.NEW.name());
                log.info("未支付成功，本地取消订单:{}", eventMessage);
            } else {
                // 支付成功 主动把订单状态更新成支付成功
                log.warn("支付成功，但是微信回调通知失败，需要排查问题:{}", eventMessage);
                productOrderManager.updateOrderPayState(outTradeNo, accountNo,
                        ProductOrderStateEnum.PAY.name(), ProductOrderStateEnum.NEW.name());
                //TODO 触发支付成功后的逻辑

            }
        }

        return true;
    }

    /**
     * 处理微信回调通知
     *
     * @param payType
     * @param paramsMap
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public JsonData processOrderCallbackMsg(ProductOrderPayTypeEnum payType, Map<String, String> paramsMap) {
        // 商户订单号
        String outTradeNo = paramsMap.get("out_trade_no");
        // 交易状态
        String tradeState = paramsMap.get("trade_state");
        // 账户号
        Long accountNo = Long.valueOf(paramsMap.get("account_no"));

        ProductOrderDo productOrderDo = productOrderManager.findByOutTradeNoAndAccountNo(outTradeNo, accountNo);

        Map<String, Object> content = new HashMap<>(4);
        content.put("outTradeNo", outTradeNo);
        content.put("buyNum", productOrderDo.getBuyNum());
        content.put("accountNo", accountNo);
        content.put("product", productOrderDo.getProductSnapshot());

        // 构建消息
        EventMessage eventMessage = EventMessage.builder().bizId(outTradeNo)
                .accountNo(accountNo).messageId(outTradeNo).content(JsonUtil.obj2Json(content))
                .eventMessageType(EventMessageType.ORDER_PAY.name()).build();

        if (payType.name().equalsIgnoreCase(ProductOrderPayTypeEnum.ALI_PAY.name())) {
            //TODO 支付宝支付
        } else if (payType.name().equalsIgnoreCase(ProductOrderPayTypeEnum.WECHAT_PAY.name())) {
            // 微信支付
            if ("SUCCESS".equalsIgnoreCase(tradeState)) {
                rabbitTemplate.convertAndSend(rabbitMQConfig.getOrderEventExchange()
                        , rabbitMQConfig.getOrderUpdateTrafficRoutingKey(), eventMessage);

                return JsonData.buildSuccess();
            }
        }
        return JsonData.buildResult(BizCodeEnum.PAY_ORDER_CALLBACK_NOT_SUCCESS);
    }


    private ProductOrderDo saveProductOrder(ConfirmOrderRequest orderRequest, LoginUser loginUser, String orderOutTradeNo, ProductDo productDo) {
        ProductOrderDo productOrderDo = new ProductOrderDo();
        // 设置用户信息
        productOrderDo.setAccountNo(loginUser.getAccountNo());
        productOrderDo.setNickname(loginUser.getUsername());

        // 设置商品信息
        productOrderDo.setProductId(productDo.getId());
        productOrderDo.setProductTitle(productDo.getTitle());
        productOrderDo.setProductSnapshot(JsonUtil.obj2Json(productDo));
        productOrderDo.setProductAmount(productDo.getAmount());

        // 设置订单信息
        productOrderDo.setBuyNum(orderRequest.getBuyNum());
        productOrderDo.setOutTradeNo(orderOutTradeNo);
        productOrderDo.setCreateTime(new Date());
        productOrderDo.setDel(0);

        // 设置发票信息
        productOrderDo.setBillType(BillTypeEnum.valueOf(orderRequest.getBillType()).name());
        productOrderDo.setBillHeader(productOrderDo.getBillHeader());
        productOrderDo.setBillReceiverPhone(orderRequest.getBillReceiverPhone());
        productOrderDo.setBillReceiverEmail(orderRequest.getBillReceiverEmail());
        productOrderDo.setBillContent(orderRequest.getBillContent());

        // 实际支付价格
        productOrderDo.setPayAmount(orderRequest.getPayAmount());
        // 总价，没使用优惠券和折扣
        productOrderDo.setTotalAmount(orderRequest.getTotalAmount());
        // 订单状态
        productOrderDo.setState(ProductOrderStateEnum.NEW.name());
        // 支付类型
        productOrderDo.setPayType(ProductOrderPayTypeEnum.valueOf(orderRequest.getPayType()).name());

        // 插入数据库
        productOrderManager.add(productOrderDo);

        return productOrderDo;
    }

    /**
     * 验证价格
     *
     * @param productDo
     * @param orderRequest
     */
    private void checkPrice(ProductDo productDo, ConfirmOrderRequest orderRequest) {
        // 后台计算价格
        BigDecimal bizTotal = BigDecimal.valueOf(orderRequest.getBuyNum()).multiply(productDo.getAmount());

        // 前端传递总价和后端计算总价格是否一致
        //TODO 优惠券价格进行抵扣
        if (bizTotal.compareTo(orderRequest.getPayAmount()) != 0) {
            log.error("验证价格失败:{}", orderRequest);
            throw new BizException(BizCodeEnum.ORDER_CONFIRM_PRICE_FAIL);
        }
    }

}
