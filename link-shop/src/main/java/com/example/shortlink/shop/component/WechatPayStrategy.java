package com.example.shortlink.shop.component;

import com.alibaba.fastjson.JSONObject;
import com.example.shortlink.shop.config.WechatPayApi;
import com.example.shortlink.shop.config.WechatPayConfig;
import com.example.shortlink.shop.vo.PayInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 微信支付
 *
 * @author 彭亮
 * @create 2023-01-11 10:54
 */
@Service
@Slf4j
public class WechatPayStrategy implements PayStrategy {

    @Autowired
    private WechatPayConfig payConfig;

    @Autowired
    private CloseableHttpClient wechatPayClient;

    @Override
    public String unifiedOrder(PayInfoVo payInfoVo) {
        //过期时间  RFC 3339格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        //支付订单过期时间
        String timeExpire = sdf.format(new Date(System.currentTimeMillis() + payInfoVo.getOrderPayTimeoutMills()));

        JSONObject amountObj = new JSONObject();
        //数据库存储是double比如，100.99元，微信支付需要以分为单位

        int amount = payInfoVo.getPayFee().multiply(BigDecimal.valueOf(100)).intValue();
        amountObj.put("total", amount);
        amountObj.put("currency", "CNY");

        JSONObject payObj = new JSONObject();
        payObj.put("mchid", payConfig.getMchId());
        payObj.put("out_trade_no", payInfoVo.getOutTradeNo());
        payObj.put("appid", payConfig.getWxPayAppid());
        payObj.put("description", payInfoVo.getTitle());
        payObj.put("notify_url", payConfig.getCallbackUrl());

        payObj.put("time_expire", timeExpire);
        payObj.put("amount", amountObj);
        //回调携带
        payObj.put("attach", "{\"accountNo\":" + payInfoVo.getAccountNo() + "}");

        // 处理请求body参数
        String body = payObj.toJSONString();

        log.info("请求参数:{}", body);

        StringEntity entity = new StringEntity(body, "utf-8");
        entity.setContentType("application/json");

        HttpPost httpPost = new HttpPost(WechatPayApi.NATIVE_ORDER);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);

        String result = "";

        try (CloseableHttpResponse response = wechatPayClient.execute(httpPost)) {

            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());

            log.debug("下单响应码:{},响应体:{}", statusCode, responseStr);

            if (statusCode == HttpStatus.OK.value()) {
                JSONObject jsonObject = JSONObject.parseObject(responseStr);
                if (jsonObject.containsKey("code_url")) {
                    result = jsonObject.getString("code_url");
                }
            } else {
                log.error("下单响应失败:{},响应体:{}", statusCode, responseStr);
            }
        } catch (Exception e) {
            log.error("微信支付下单响应异常:{}", e);
        }
        return result;
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
