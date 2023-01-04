package com.example.shortlink.link.strategy;

import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.exception.BizException;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @author 彭亮
 * @create 2023-01-04 17:01
 */
public class CustomDBPreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    /**
     *
     * @param availableTargetNames 数据源集合
     *                             在分库时值为所有分片库的集合 databaseNames
     *                             在分表时对应分片库中所有分片表的集合 tableNames
     * @param shardingValue 分片属性，包括logicTableName 逻辑表
     *                      columnName 分片键
     *                      value 为从SQL中解析出的分片键的值
     * @return
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        // 获取短链码第一位，即库位
        String codePrefix = shardingValue.getValue().substring(0,1);

        for (String targetName : availableTargetNames) {
            // 获取库名的最后一位，真实配置的ds
            String targetNameSuffix = targetName.substring(targetName.length() - 1);
            // 如果一至则返回
            if (codePrefix.equals(targetNameSuffix)){
                return targetName;
            }
        }

        throw new BizException(BizCodeEnum.DB_ROUTE_NOT_FOUND);
    }
}
