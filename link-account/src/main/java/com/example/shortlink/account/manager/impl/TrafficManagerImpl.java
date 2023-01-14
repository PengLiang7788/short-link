package com.example.shortlink.account.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shortlink.account.manager.TrafficManager;
import com.example.shortlink.account.mapper.TrafficMapper;
import com.example.shortlink.account.model.TrafficDO;
import com.example.shortlink.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 彭亮
 * @create 2023-01-11 19:48
 */
@Component
@Slf4j
public class TrafficManagerImpl implements TrafficManager {

    @Autowired
    private TrafficMapper trafficMapper;

    /**
     * 新增
     *
     * @param trafficDO
     * @return
     */
    @Override
    public int add(TrafficDO trafficDO) {
        return trafficMapper.insert(trafficDO);
    }

    /**
     * 分页查找
     *
     * @param page
     * @param size
     * @param accountNo
     * @return
     */
    @Override
    public IPage<TrafficDO> pageAvailable(int page, int size, long accountNo) {
        Page<TrafficDO> pageInfo = new Page<>(page, size);
        String today = TimeUtil.format(new Date(), "yyyy-MM-dd");

        Page<TrafficDO> trafficDOPage = trafficMapper.selectPage(pageInfo, new LambdaQueryWrapper<TrafficDO>()
                .eq(TrafficDO::getAccountNo, accountNo)
                .ge(TrafficDO::getExpiredDate, today)
                .orderByDesc(TrafficDO::getGmtCreate));

        return trafficDOPage;
    }

    /**
     * 根据账户和流量包id查询流量包详情
     *
     * @param trafficId
     * @param accountNo
     * @return
     */
    @Override
    public TrafficDO findByIdAndAccountNo(Long trafficId, Long accountNo) {
        String today = TimeUtil.format(new Date(), "yyyy-MM-dd");

        TrafficDO trafficDO = trafficMapper.selectOne(new LambdaQueryWrapper<TrafficDO>()
                .eq(TrafficDO::getAccountNo, accountNo)
                .eq(TrafficDO::getId, trafficId)
                // 并且未过期
                .ge(TrafficDO::getExpiredDate, today));

        return trafficDO;
    }

    /**
     * 给某个流量包增加天使用次数
     *
     * @param currentTrafficId
     * @param accountNo
     * @param dayUsedTimes
     * @return
     */
    @Override
    public int addDayUsedTimes(long currentTrafficId, Long accountNo, int dayUsedTimes) {
        int rows = trafficMapper.update(null, new LambdaUpdateWrapper<TrafficDO>()
                .eq(TrafficDO::getAccountNo, accountNo)
                .eq(TrafficDO::getId, currentTrafficId)
                .set(TrafficDO::getDayUsed, dayUsedTimes));
        return rows;
    }

    /**
     * 删除过期流量包
     *
     * @return
     */
    @Override
    public boolean deleteExpireTraffic() {
        int rows = trafficMapper.delete(new LambdaQueryWrapper<TrafficDO>()
                .le(TrafficDO::getExpiredDate, new Date()));
        log.info("删除过期流量包影响行数:rows={}", rows);
        return true;
    }
}
