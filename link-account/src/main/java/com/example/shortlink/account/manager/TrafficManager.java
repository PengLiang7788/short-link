package com.example.shortlink.account.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.shortlink.account.model.TrafficDO;

/**
 * @author 彭亮
 * @create 2023-01-11 19:45
 */
public interface TrafficManager {
    /**
     * 新增流量包
     *
     * @param trafficDO
     * @return
     */
    int add(TrafficDO trafficDO);

    /**
     * 分页查询可用的流量包
     *
     * @param page
     * @param size
     * @param accountNo
     * @return
     */
    IPage<TrafficDO> pageAvailable(int page, int size, long accountNo);

    /**
     * 查找流量包详情
     *
     * @param trafficId
     * @param accountNo
     * @return
     */
    TrafficDO findByIdAndAccountNo(Long trafficId, Long accountNo);

    /**
     * 增加某个流量包天使用次数
     *
     * @param currentTrafficId
     * @param accountNo
     * @param dayUsedTimes
     * @return
     */
    int addDayUsedTimes(long currentTrafficId, Long accountNo, int dayUsedTimes);

    /**
     * 删除过期流量包
     *
     * @return
     */
    boolean deleteExpireTraffic();


}
