package com.example.shortlink.link.strategy;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @author 彭亮
 * @create 2023-01-04 17:21
 */
public class CustomTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    /**
     * @param availableTargetNames 数据源集合
     *                             在分库时值为所有分片库的集合 databasesNames
     *                             在分表时为对应分片库中所有分片表的集合 tablesNames
     * @param shardingValue        分片属性，包括logicTableName 逻辑表
     *                             columnName 分片键
     *                             value 从SQL中解析出的分片键的值
     * @return
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {

        // 获取逻辑表
        String targetName = availableTargetNames.iterator().next();

        // 获取短链码
        String value = shardingValue.getValue();

        // 获取短链码最后一位
        String codeSuffix = value.substring(value.length() - 1);

        // 拼接Actual table
        return targetName + "_" + codeSuffix;
    }
}
