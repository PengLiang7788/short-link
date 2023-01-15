package com.example.shortlink.account.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shortlink.account.manager.TrafficManager;
import com.example.shortlink.account.mapper.TrafficMapper;
import com.example.shortlink.account.model.TrafficDO;
import com.example.shortlink.common.enums.PluginTypeEnum;
import com.example.shortlink.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

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
     * 删除过期流量包(付费)
     *
     * @return
     */
    @Override
    public boolean deleteExpireTraffic() {
        int rows = trafficMapper.delete(new LambdaQueryWrapper<TrafficDO>()
                .le(TrafficDO::getExpiredDate, new Date())
                .ne(TrafficDO::getOutTradeNo,"free_init"));
        log.info("删除过期流量包影响行数:rows={}", rows);
        return true;
    }

    /**
     * 查找可用的短链流量包(未过期)，包括免费流量包
     * select * from traffic where account_no = 1111 and (expire_date >= ? or out_trade_no = free_init)
     *
     * @param accountNo
     * @return
     */
    @Override
    public List<TrafficDO> selectAvailableTraffics(Long accountNo) {

        String today = TimeUtil.format(new Date(), "yyyy-MM-dd");

        LambdaQueryWrapper<TrafficDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TrafficDO::getAccountNo,accountNo);
        queryWrapper.and(wrapper->wrapper.ge(TrafficDO::getExpiredDate,today).or().eq(TrafficDO::getOutTradeNo,"free_init"));

        return trafficMapper.selectList(queryWrapper);
    }

    /**
     * 增加流量包使用次数
     *
     * @param accountNo
     * @param trafficId
     * @param usedTimes
     * @return
     */
    @Override
    public int addDayUsedTimes(Long accountNo, Long trafficId, Integer usedTimes) {
        return trafficMapper.addDayUsedTimes(accountNo, trafficId, usedTimes);
    }

    /**
     * 恢复流量包使用当天当天次数
     * @param accountNo
     * @param trafficId
     * @param usedTimes
     * @param useDateStr
     * @return
     */
    @Override
    public int releaseUsedTimes(Long accountNo, Long trafficId, Integer usedTimes,String useDateStr) {
        return trafficMapper.releaseUsedTimes(accountNo, trafficId, usedTimes,useDateStr);
    }

    /**
     * 批量更新流量包使用次数为0
     *
     * @param accountNo
     * @param unUpdatedTrafficIds
     * @return
     */
    @Override
    public int batchUpdateUsedTimes(Long accountNo, List<Long> unUpdatedTrafficIds) {

        int rows = trafficMapper.update(null, new LambdaUpdateWrapper<TrafficDO>()
                .eq(TrafficDO::getAccountNo, accountNo)
                .in(TrafficDO::getId, unUpdatedTrafficIds)
                .set(TrafficDO::getDayUsed, 0));

        return rows;
    }
}
