package com.example.shortlink.account.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.account.manager.TrafficTaskManager;
import com.example.shortlink.account.mapper.TrafficTaskMapper;
import com.example.shortlink.account.model.TrafficTaskDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 彭亮
 * @create 2023-01-15 15:04
 */
@Component
@Slf4j
public class TrafficTaskManagerImpl implements TrafficTaskManager {

    @Autowired
    private TrafficTaskMapper trafficTaskMapper;

    /**
     * 新增短链任务表
     *
     * @param trafficTaskDO
     * @return
     */
    @Override
    public int add(TrafficTaskDO trafficTaskDO) {
        return trafficTaskMapper.insert(trafficTaskDO);
    }

    /**
     * 根据任务id和账号查找
     *
     * @param id
     * @param accountNo
     * @return
     */
    @Override
    public TrafficTaskDO findByIdAndAccountNo(Long id, Long accountNo) {
        TrafficTaskDO trafficTaskDO = trafficTaskMapper.selectOne(new LambdaQueryWrapper<TrafficTaskDO>()
                .eq(TrafficTaskDO::getId, id)
                .eq(TrafficTaskDO::getAccountNo, accountNo));
        return trafficTaskDO;
    }

    /**
     * 根据任务id和账号删除
     *
     * @param id
     * @param accountNo
     * @return
     */
    @Override
    public int deleteByIdAndAccountNo(Long id, Long accountNo) {
        int rows = trafficTaskMapper.delete(new LambdaQueryWrapper<TrafficTaskDO>()
                .eq(TrafficTaskDO::getId, id)
                .eq(TrafficTaskDO::getAccountNo, accountNo));
        return rows;
    }
}
