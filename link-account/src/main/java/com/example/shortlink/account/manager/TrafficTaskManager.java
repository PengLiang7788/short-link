package com.example.shortlink.account.manager;

import com.example.shortlink.account.model.TrafficTaskDO;

/**
 * @author 彭亮
 * @create 2023-01-15 15:02
 */
public interface TrafficTaskManager {

    /**
     * 新增短链任务表
     *
     * @param trafficTaskDO
     * @return
     */
    int add(TrafficTaskDO trafficTaskDO);

    /**
     * 根据任务id和账号查找
     *
     * @param id
     * @param accountNo
     * @return
     */
    TrafficTaskDO findByIdAndAccountNo(Long id, Long accountNo);

    /**
     * 根据任务id和账号删除
     *
     * @param id
     * @param accountNo
     * @return
     */
    int deleteByIdAndAccountNo(Long id, Long accountNo);
}
