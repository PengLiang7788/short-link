package com.example.shortlink.link.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.link.manager.LinkGroupManager;
import com.example.shortlink.link.mapper.LinkGroupMapper;
import com.example.shortlink.link.model.LinkGroupDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
     *
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
     *
     * @param groupId
     * @param accountNo
     * @return
     */
    @Override
    public int del(Long groupId, long accountNo) {
        int result = linkGroupMapper.delete(new LambdaQueryWrapper<LinkGroupDO>().eq(LinkGroupDO::getId, groupId).eq(LinkGroupDO::getAccountNo, accountNo));
        return result;
    }

    /**
     * 根据分组id和账号查询分组详情信息
     */
    @Override
    public LinkGroupDO detail(Long groupId, long accountNo) {
        LinkGroupDO linkGroupDO = linkGroupMapper.selectOne(new LambdaQueryWrapper<LinkGroupDO>().eq(LinkGroupDO::getId, groupId).eq(LinkGroupDO::getAccountNo, accountNo));
        return linkGroupDO;
    }

    /**
     * 根据账户id查询用户的全部分组
     *
     * @param accountNo
     * @return
     */
    @Override
    public List<LinkGroupDO> listAllGroup(long accountNo) {
        List<LinkGroupDO> linkGroupDOList = linkGroupMapper.selectList(new LambdaQueryWrapper<LinkGroupDO>().eq(LinkGroupDO::getAccountNo, accountNo));
        return linkGroupDOList;
    }

    /**
     * 更改分组名
     *
     * @param linkGroupDO
     * @return
     */
    @Override
    public int updateById(LinkGroupDO linkGroupDO) {
        int rows = linkGroupMapper.update(linkGroupDO, new LambdaQueryWrapper<LinkGroupDO>()
                .eq(LinkGroupDO::getId, linkGroupDO.getId())
                .eq(LinkGroupDO::getAccountNo, linkGroupDO.getAccountNo()));
        return rows;
    }
}
