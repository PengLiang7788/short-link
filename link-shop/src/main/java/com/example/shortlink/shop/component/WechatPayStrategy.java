package com.example.shortlink.shop.component;

import com.example.shortlink.shop.vo.PayInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信支付
 * @author 彭亮
 * @create 2023-01-11 10:54
 */
@Service
@Slf4j
public class WechatPayStrategy implements PayStrategy{

    @Override
    public String unifiedOrder(PayInfoVo payInfoVo) {
        return null;
    }

    @Override
    public String refund(PayInfoVo payInfoVo) {
        return PayStrategy.super.refund(payInfoVo);
    }

    @Override
    public String queryPayStatus(PayInfoVo payInfoVo) {
        return PayStrategy.super.queryPayStatus(payInfoVo);
    }

    @Override
    public String closeOrder(PayInfoVo payInfoVo) {
        return PayStrategy.super.closeOrder(payInfoVo);
    }
}
