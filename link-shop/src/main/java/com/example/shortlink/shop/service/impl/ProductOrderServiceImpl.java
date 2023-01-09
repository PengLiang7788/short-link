package com.example.shortlink.shop.service.impl;

import com.example.shortlink.common.constant.TImeConstant;
import com.example.shortlink.common.enums.BillTypeEnum;
import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.enums.ProductOrderPayTypeEnum;
import com.example.shortlink.common.enums.ProductOrderStateEnum;
import com.example.shortlink.common.exception.BizException;
import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.common.model.LoginUser;
import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.common.util.JsonUtil;
import com.example.shortlink.shop.controller.request.ConfirmOrderRequest;
import com.example.shortlink.shop.controller.request.ProductOrderPageRequest;
import com.example.shortlink.shop.manager.ProductManager;
import com.example.shortlink.shop.manager.ProductOrderManager;
import com.example.shortlink.shop.model.ProductDo;
import com.example.shortlink.shop.model.ProductOrderDo;
import com.example.shortlink.shop.service.ProductOrderService;
import com.example.shortlink.shop.vo.PayInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author 彭亮
 * @create 2023-01-09 13:26
 */
@Service
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {

    @Autowired
    private ProductOrderManager productOrderManager;

    @Autowired
    private ProductManager productManager;

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
                .orderPayTimeOut(TImeConstant.ORDER_PAY_TIMEOUT_MILLS).build();

        // 发送延迟消息 TODO

        // 对接支付信息 TODO

        return null;
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
