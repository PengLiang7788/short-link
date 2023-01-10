package com.example.shortlink.shop.pay;

import com.example.shortlink.shop.config.PayBeanConfig;
import lombok.extern.slf4j.Slf4j;
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

    @Test
    public void testLoadPrivateKey() throws IOException {
        log.info(payBeanConfig.getPrivateKey().getAlgorithm());
    }
}
