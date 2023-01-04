package com.example.shortlink.link.db;

import com.example.shortlink.link.strategy.ShardingDBConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author 彭亮
 * @create 2023-01-04 19:17
 */
@Slf4j
public class CommonTest {

    @Test
    public void testRandomDB(){
        for (int i = 0; i < 20; i++) {
            log.info(ShardingDBConfig.getRandomDBPrefix());
        }
    }

}
