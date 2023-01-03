package com.example.shortlink.link;

import com.example.shortlink.common.util.CommonUtil;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author 彭亮
 * @create 2023-01-03 18:26
 */
@Slf4j
public class ShortLinkTest {

    @Test
    public void testMurmurHash() {
        for (int i = 0; i < 5; i++) {

            String originalUrl = "https://baidu.com?id=" + CommonUtil.generateUUID() + "pwd=" + CommonUtil.getStringNumRandom(7);
            long murmur3_32 = Hashing.murmur3_32().hashUnencodedChars(originalUrl).padToLong();
            log.info("murmur3_32={}", murmur3_32);
        }
    }

}
