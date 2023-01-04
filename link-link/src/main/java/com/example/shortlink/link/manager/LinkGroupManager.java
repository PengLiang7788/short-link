package com.example.shortlink.link.manager;

import com.example.shortlink.link.model.LinkGroupDO;

/**
 * @author 彭亮
 * @create 2023-01-03 21:08
 */
public interface LinkGroupManager {
    /**
     * 添加分组
     * @param linkGroupDO
     * @return
     */
    int add(LinkGroupDO linkGroupDO);

    int del(Long groupId, long accountNo);
}
