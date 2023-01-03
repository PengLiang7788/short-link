package com.example.shortlink.common.util;

import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;

/**
 * @author 彭亮
 * @create 2023-01-03 17:13
 */
public class IDUtil {

    private static SnowflakeShardingKeyGenerator shardingKeyGenerator = new SnowflakeShardingKeyGenerator();

    /**
     * 雪花算法生成器
     * @return
     */
    public static Comparable<?> generateSnowFlakeID() {
        return shardingKeyGenerator.generateKey();
    }
}
