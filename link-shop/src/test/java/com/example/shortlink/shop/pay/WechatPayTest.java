package com.example.shortlink.shop.pay;

import com.alibaba.fastjson.JSONObject;
import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.shop.config.PayBeanConfig;
import com.example.shortlink.shop.config.WechatPayApi;
import com.example.shortlink.shop.config.WechatPayConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author 彭亮
 * @create 2023-01-10 16:13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class WechatPayTest {

    @Autowired
    private PayBeanConfig payBeanConfig;

    @Autowired
    private WechatPayConfig payConfig;

    @Autowired
    private CloseableHttpClient wechatPayClient;

    @Test
    public void testLoadPrivateKey() throws IOException {
        log.info(payBeanConfig.getPrivateKey().getAlgorithm());
    }

    @Test
    public void testNativeOrder() {

        String outTradeNo = CommonUtil.getStringNumRandom(32);

        JSONObject payObj = new JSONObject();

        payObj.put("mchid", payConfig.getMchId());
        payObj.put("out_trade_no", outTradeNo);
        payObj.put("appid", payConfig.getWxPayAppid());
        payObj.put("description", "退款测试");
        payObj.put("notify_url", payConfig.getCallbackUrl());

        //订单总金额，单位为分。
        JSONObject amountObj = new JSONObject();
        amountObj.put("total", 100);
        amountObj.put("currency", "CNY");

        payObj.put("amount", amountObj);
        //附属参数，可以用在回调
        payObj.put("attach", "{\"accountNo\":" + 888 + "}");

        String body = payObj.toJSONString();

        log.info("请求参数:{}", body);

        StringEntity entity = new StringEntity(body, "utf-8");
        entity.setContentType("application/json");

        HttpPost httpPost = new HttpPost(WechatPayApi.NATIVE_ORDER);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = wechatPayClient.execute(httpPost)) {

            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());

            log.info("下单响应码:{},响应体:{}", statusCode, responseStr);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Test
    public void testNativeQuery() {
        String outTradeNo = "jVDdIa5yPV6lmwe1VJjMaiOmoFRRbwm1";

        String url = String.format(WechatPayApi.NATIVE_QUERY, outTradeNo, payConfig.getMchId());

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");

        try (CloseableHttpResponse response = wechatPayClient.execute(httpGet)) {

            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());

            log.info("查询响应码:{},响应体:{}", statusCode, responseStr);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Test
    public void testNativeCloseOrder() {
        String outTradeNo = "jVDdIa5yPV6lmwe1VJjMaiOmoFRRbwm1";

        JSONObject payObj = new JSONObject();
        payObj.put("mchid", payConfig.getMchId());
        String body = payObj.toJSONString();

        log.info("请求参数:{}", body);

        StringEntity entity = new StringEntity(body, "utf-8");
        entity.setContentType("application/json");

        String url = String.format(WechatPayApi.NATIVE_CLOSE_ORDER, outTradeNo);

        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = wechatPayClient.execute(httpPost)) {

            //响应码
            int statusCode = response.getStatusLine().getStatusCode();

            log.info("关闭订单响应码:{}", statusCode);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testNativeRefundOrder() {

        String outTradeNo = "PLF9dMYnh8QBbEHTiLo2CKGojwGYXIyi";
        String refundNo = CommonUtil.getStringNumRandom(32);

        // 请求body参数
        JSONObject refundObj = new JSONObject();
        //订单号
        refundObj.put("out_trade_no", outTradeNo);
        //退款单编号，商户系统内部的退款单号，商户系统内部唯一，
        // 只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔
        refundObj.put("out_refund_no", refundNo);
        refundObj.put("reason","商品已售完");
        refundObj.put("notify_url", payConfig.getCallbackUrl());

        JSONObject amountObj = new JSONObject();
        //退款金额
        amountObj.put("refund", 90);
        //实际支付的总金额
        amountObj.put("total", 100);
        amountObj.put("currency", "CNY");

        refundObj.put("amount", amountObj);

        String body = refundObj.toJSONString();

        log.info("请求参数:{}", body);

        StringEntity entity = new StringEntity(body, "utf-8");
        entity.setContentType("application/json");

        HttpPost httpPost = new HttpPost(WechatPayApi.NATIVE_REFUND_ORDER);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = wechatPayClient.execute(httpPost)) {

            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());

            log.info("申请订单退款响应码:{},响应体:{}", statusCode, responseStr);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void testNativeRefundQuery() {
        String refundNo = "58XeMJTKQr1kji2VHK9vFrqf6cd1sdWN";

        String url = String.format(WechatPayApi.NATIVE_REFUND_QUERY, refundNo);

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");

        try (CloseableHttpResponse response = wechatPayClient.execute(httpGet)) {

            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());

            log.info("查询退款响应码:{},响应体:{}", statusCode, responseStr);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
