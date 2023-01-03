package com.example.shortlink.link;

import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.link.component.ShortLinkComponent;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * @author 彭亮
 * @create 2023-01-03 18:26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkServiceApplication.class)
@Slf4j
public class ShortLinkTest {

    @Autowired
    private ShortLinkComponent shortLinkComponent;

    /**
     * 测试短链平台
     */
    @Test
    public void testCreateShortLink(){
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int num1 = random.nextInt(10);
            int num2 = random.nextInt(1000000);
            int num3 = random.nextInt(1000000);
            String originalUrl = num1 + "baidu" + num2 + ".com" + num3;
            String shortLinkCode = shortLinkComponent.createShortLinkCode(originalUrl);
            log.info("originalUrl:{},shortLinkCode:{}",originalUrl,shortLinkCode);
        }
    }

    @Test
    public void testMurmurHash() {
        for (int i = 0; i < 5; i++) {

            String originalUrl = "https://baidu.com?id=" + CommonUtil.generateUUID() + "pwd=" + CommonUtil.getStringNumRandom(7);
            long murmur3_32 = Hashing.murmur3_32().hashUnencodedChars(originalUrl).padToLong();
            log.info("murmur3_32={}", murmur3_32);
        }
    }

}
