package com.example.shortlink.common.config;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author 彭亮
 * @create 2022-12-21 15:30
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory requestFactory){
        return new RestTemplate(requestFactory);
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory(){
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public HttpClient httpClient(){
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
