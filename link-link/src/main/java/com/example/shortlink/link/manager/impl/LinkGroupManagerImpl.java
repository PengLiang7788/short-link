package com.example.shortlink.link.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.link.manager.LinkGroupManager;
import com.example.shortlink.link.mapper.LinkGroupMapper;
import com.example.shortlink.link.model.LinkGroupDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 彭亮
 * @create 2023-01-03 21:08
 */
@Component
public class LinkGroupManagerImpl implements LinkGroupManager {

    @Autowired
    private LinkGroupMapper linkGroupMapper;

    /**
     * 添加分组
     * @param linkGroupDO
     * @return
     */
    @Override
    public int add(LinkGroupDO linkGroupDO) {
        int rows = linkGroupMapper.insert(linkGroupDO);
        return rows;
    }

    /**
     * 根据账户id和分组id删除分组
     * @param groupId
     * @param accountNo
     * @return
     */
    @Override
    public int del(Long groupId, long accountNo) {
        int result = linkGroupMapper.delete(new LambdaQueryWrapper<LinkGroupDO>().eq(LinkGroupDO::getId, groupId).eq(LinkGroupDO::getAccountNo, accountNo));
        return result;
    }
}
