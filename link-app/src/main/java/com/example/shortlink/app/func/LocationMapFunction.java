package com.example.shortlink.app.func;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.shortlink.app.model.ShortLinkWideDo;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

/**
 * @author 彭亮
 * @create 2023-01-18 10:28
 */
@Slf4j
public class LocationMapFunction extends RichMapFunction<ShortLinkWideDo, String> {

    private static final String IP_PARSE_URL = "https://restapi.amap.com/v3/ip?ip=%s&output=json&key=ddde35996428d58da1c748f841ef433f";

    private CloseableHttpClient httpClient;

    @Override
    public void open(Configuration parameters) throws Exception {
        this.httpClient = createHttpClient();
    }

    @Override
    public void close() throws Exception {
        if (httpClient != null) {
            httpClient.close();
        }
    }

    @Override
    public String map(ShortLinkWideDo value) throws Exception {

        String ip = value.getIp();
        String url = String.format(IP_PARSE_URL, ip);

        HttpGet httpGet = new HttpGet(url);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");

                JSONObject locationObj = JSON.parseObject(result);
                String province = locationObj.getString("province");
                String city = locationObj.getString("city");

                value.setProvince(province);
                value.setCity(city);
            }
        } catch (Exception e) {
            log.error("ip解析错误,value={},msg={}", value, e.getMessage());
        }

        return JSONObject.toJSONString(value);
    }

    private CloseableHttpClient createHttpClient() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

        // 设置整个连接池的最大连接数
        connectionManager.setMaxTotal(500);
        // MaxPerRoute路由是对maxTotal的细分，每个主机的并发，这里的route指的时域名
        connectionManager.setDefaultMaxPerRoute(300);

        RequestConfig requestConfig = RequestConfig.custom()
                // 设置从连接池中获取连接的超时时间
                .setConnectionRequestTimeout(1000)
                // 连上服务器的超时时间
                .setConnectTimeout(1000)
                // 返回数据的超时时间
                .setSocketTimeout(2000)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();

        return httpClient;

    }
}
