package com.example.shortlink.link.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author 彭亮
 * @create 2023-01-04 19:13
 */
public class ShardingTableConfig {
    /**
     * 存储数据表位置编号
     */
    private static final List<String> tableSuffixList = new ArrayList<>();

    // 配置启用哪些表的后缀
    static {
        tableSuffixList.add("0");
        tableSuffixList.add("a");
    }

    /**
     * 获取随机后缀
     *
     * @return
     */
    public static String getRandomTableSuffix(String code) {
        int hashCode = code.hashCode();
        int index = Math.abs(hashCode) % tableSuffixList.size();
        return tableSuffixList.get(index);
    }
}
