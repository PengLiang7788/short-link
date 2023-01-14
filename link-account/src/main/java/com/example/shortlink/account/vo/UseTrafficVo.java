package com.example.shortlink.account.vo;

import com.example.shortlink.account.model.TrafficDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 彭亮
 * @create 2023-01-14 16:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UseTrafficVo {

    /**
     * 天剩余可用总次数 = 总次数 - 已用
     */
    private Integer dayTotalLeftTimes;

    /**
     * 当前使用的流量包
     */
    private TrafficDO currentTrafficDo;

    /**
     * 记录没过期，但是今天没更新的流量包id
     */
    private List<Long> unUpdateTrafficIds;
}
