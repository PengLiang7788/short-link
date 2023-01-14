package com.example.shortlink.account.mapper;

import com.example.shortlink.account.model.TrafficDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author 二当家小D
 * @since 2022-12-21
 */
public interface TrafficMapper extends BaseMapper<TrafficDO> {

    /**
     * 给某个流量包增加天使用次数
     *
     * @param accountNo
     * @param trafficId
     * @param usedTimes
     * @return
     */
    int addDayUsedTimes(@Param("accountNo") Long accountNo, @Param("trafficId") Long trafficId,
                        @Param("usedTimes") Integer usedTimes);

    /**
     * 恢复流量包当天使用次数
     *
     * @param accountNo
     * @param trafficId
     * @param usedTimes
     * @return
     */
    int releaseUsedTimes(@Param("accountNo") Long accountNo, @Param("trafficId") Long trafficId, @Param("usedTimes") Integer usedTimes);

    /**
     * 查找可用的短链流量包(未过期)，包括免费流量包
     * @param accountNo
     * @param today
     */
//    List<TrafficDO> selectAvailableTraffics(@Param("accountNo") Long accountNo, @Param("today") String today);
}
