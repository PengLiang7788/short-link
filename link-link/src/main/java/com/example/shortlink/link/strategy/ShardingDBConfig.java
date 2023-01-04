package com.example.shortlink.link.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author 彭亮
 * @create 2023-01-04 19:13
 */
public class ShardingDBConfig {
    /**
     * 存储数据库位置编号
     */
    private static final List<String> dbPrefix = new ArrayList<>();

    private static Random random = new Random();

    // 配置启用那些库的前缀
    static {
        dbPrefix.add("0");
        dbPrefix.add("1");
        dbPrefix.add("a");
    }

    /**
     * 获取随机前缀
     *
     * @return
     */
    public static String getRandomDBPrefix() {
        int index = random.nextInt(dbPrefix.size());
        return dbPrefix.get(index);
    }


}
