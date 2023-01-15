package com.example.shortlink.account.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.shortlink.account.model.TrafficDO;

import java.util.List;

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
     * 删除过期流量包
     *
     * @return
     */
    boolean deleteExpireTraffic();

    /**
     * 查找可用的短链流量包(未过期)，包括免费流量包
     *
     * @param accountNo
     * @return
     */
    List<TrafficDO> selectAvailableTraffics(Long accountNo);

    /**
     * 给某个流量包增加使用次数
     *
     * @param accountNo
     * @param trafficId
     * @param usedTimes
     * @return
     */
    int addDayUsedTimes(Long accountNo, Long trafficId, Integer usedTimes);

    /**
     * 恢复流量包使用当天当天次数
     * @param accountNo
     * @param trafficId
     * @param usedTimes
     * @param useDateStr
     * @return
     */
    int releaseUsedTimes(Long accountNo, Long trafficId, Integer usedTimes,String useDateStr);

    /**
     * 批量更新流量包使用次数为0
     *
     * @param accountNo
     * @param unUpdatedTrafficIds
     * @return
     */
    int batchUpdateUsedTimes(Long accountNo, List<Long> unUpdatedTrafficIds);

}
