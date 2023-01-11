package com.example.shortlink.shop.component;

import com.example.shortlink.common.enums.ProductOrderPayTypeEnum;
import com.example.shortlink.shop.vo.PayInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 彭亮
 * @create 2023-01-11 11:02
 */
@Component
@Slf4j
public class PayFactory {
    //TODO 抽离公共代码

    @Autowired
    private AliPayStrategy aliPayStrategy;

    @Autowired
    private WechatPayStrategy wechatPayStrategy;

    /**
     * 创建支付，简单工厂模式
     *
     * @param payInfoVo
     * @return
     */
    public String pay(PayInfoVo payInfoVo) {
        String payType = payInfoVo.getPayType();
        if (ProductOrderPayTypeEnum.ALI_PAY.name().equalsIgnoreCase(payType)) {
            // 支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(aliPayStrategy);

            return payStrategyContext.executeUnifiedOrder(payInfoVo);
        } else if (ProductOrderPayTypeEnum.WECHAT_PAY.name().equalsIgnoreCase(payType)) {
            // 微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);

            return payStrategyContext.executeUnifiedOrder(payInfoVo);
        }
        return "";
    }

    /**
     * 关闭订单
     *
     * @param payInfoVo
     * @return
     */
    public String closeOrder(PayInfoVo payInfoVo) {
        String payType = payInfoVo.getPayType();
        if (ProductOrderPayTypeEnum.ALI_PAY.name().equalsIgnoreCase(payType)) {
            // 支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(aliPayStrategy);

            return payStrategyContext.executeCloseOrder(payInfoVo);
        } else if (ProductOrderPayTypeEnum.WECHAT_PAY.name().equalsIgnoreCase(payType)) {
            // 微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);

            return payStrategyContext.executeCloseOrder(payInfoVo);
        }
        return "";
    }

    /**
     * 查询支付状态
     *
     * @param payInfoVo
     * @return
     */
    public String queryPayStatus(PayInfoVo payInfoVo) {
        String payType = payInfoVo.getPayType();
        if (ProductOrderPayTypeEnum.ALI_PAY.name().equalsIgnoreCase(payType)) {
            // 支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(aliPayStrategy);

            return payStrategyContext.executeQueryPayStatus(payInfoVo);
        } else if (ProductOrderPayTypeEnum.WECHAT_PAY.name().equalsIgnoreCase(payType)) {
            // 微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);

            return payStrategyContext.executeQueryPayStatus(payInfoVo);
        }
        return "";
    }

    /**
     * 退款
     * @param payInfoVo
     * @return
     */
    public String refund(PayInfoVo payInfoVo) {
        String payType = payInfoVo.getPayType();
        if (ProductOrderPayTypeEnum.ALI_PAY.name().equalsIgnoreCase(payType)) {
            // 支付宝支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(aliPayStrategy);

            return payStrategyContext.executeRefund(payInfoVo);
        } else if (ProductOrderPayTypeEnum.WECHAT_PAY.name().equalsIgnoreCase(payType)) {
            // 微信支付
            PayStrategyContext payStrategyContext = new PayStrategyContext(wechatPayStrategy);

            return payStrategyContext.executeRefund(payInfoVo);
        }
        return "";
    }

}
