package com.example.shortlink.app.func;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.shortlink.app.model.ShortLinkWideDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * @author 彭亮
 * @create 2023-01-18 15:50
 */
@Slf4j
public class AsyncLocationRequestFunction extends RichAsyncFunction<ShortLinkWideDo, String> {

    private static final String IP_PARSE_URL = "https://restapi.amap.com/v3/ip?ip=%s&output=json&key=ddde35996428d58da1c748f841ef433f";

    private CloseableHttpAsyncClient httpAsyncClient;

    @Override
    public void open(Configuration parameters) throws Exception {
        this.httpAsyncClient = createAsyncHttpClient();
    }

    @Override
    public void close() throws Exception {
        if (httpAsyncClient != null) {
            httpAsyncClient.close();
        }
    }

    @Override
    public void timeout(ShortLinkWideDo input, ResultFuture<String> resultFuture) throws Exception {
        resultFuture.complete(Collections.singleton(null));
    }

    @Override
    public void asyncInvoke(ShortLinkWideDo shortLinkWideDo, ResultFuture<String> resultFuture) throws Exception {

        String ip = shortLinkWideDo.getIp();
        String url = String.format(IP_PARSE_URL, ip);
        HttpGet httpGet = new HttpGet(url);

        Future<HttpResponse> future = httpAsyncClient.execute(httpGet, null);

        CompletableFuture.supplyAsync(new Supplier<ShortLinkWideDo>() {
            @Override
            public ShortLinkWideDo get() {

                try {
                    HttpResponse response = future.get();
                    int statusCode = response.getStatusLine().getStatusCode();

                    if (statusCode == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        String result = EntityUtils.toString(entity, "UTF-8");

                        JSONObject locationObj = JSON.parseObject(result);
                        String city = locationObj.getString("city");
                        String province = locationObj.getString("province");

                        shortLinkWideDo.setProvince(province);
                        shortLinkWideDo.setCity(city);

                        return shortLinkWideDo;
                    }
                } catch (InterruptedException | ExecutionException | IOException e) {
                    log.error("ip解析错误,value={},msg={}", shortLinkWideDo, e.getMessage());
                }
                shortLinkWideDo.setProvince("-");
                shortLinkWideDo.setCity("-");
                return shortLinkWideDo;
            }
        }).thenAccept((result) -> {
            resultFuture.complete(Collections.singleton(JSON.toJSONString(shortLinkWideDo)));
        });

    }

    /**
     * 创建异步HttpClient
     *
     * @return
     */
    private CloseableHttpAsyncClient createAsyncHttpClient() {
        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    // 返回数据的超时时间
                    .setSocketTimeout(20000)
                    // 连接上服务器的超时时间
                    .setConnectTimeout(10000)
                    // 从连接池中获取连接的超时时间
                    .setConnectionRequestTimeout(1000)
                    .build();

            DefaultConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();

            PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
            // 设置连接池最大连接数
            connManager.setMaxTotal(500);
            // MaxPerRoute是对maxtotal的细分，每个主机的并发最大是300，route是指域名
            connManager.setDefaultMaxPerRoute(300);

            CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom().setConnectionManager(connManager)
                    .setDefaultRequestConfig(requestConfig)
                    .build();
            httpClient.start();
            return httpClient;
        } catch (Exception e) {
            log.error("初始化 CloseableHttpAsyncClient异常:{}", e.getMessage());
            return null;
        }
    }
}
