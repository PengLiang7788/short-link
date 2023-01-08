package com.example.shortlink.link.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.shortlink.link.manager.ShortLinkManager;
import com.example.shortlink.link.mapper.ShortLinkMapper;
import com.example.shortlink.link.model.ShortLinkDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 彭亮
 * @create 2023-01-04 19:25
 */
@Component
public class ShortLinkManagerImpl implements ShortLinkManager {

    @Autowired
    private ShortLinkMapper shortLinkMapper;

    /**
     * 新增短链
     *
     * @param shortLinkDO
     * @return
     */
    @Override
    public int addShortLink(ShortLinkDO shortLinkDO) {
        int rows = shortLinkMapper.insert(shortLinkDO);
        return rows;
    }

    /**
     * 查找短链
     *
     * @param shortLinkCode
     * @return
     */
    @Override
    public ShortLinkDO findByShortLinkCode(String shortLinkCode) {
        ShortLinkDO shortLinkDO = shortLinkMapper.selectOne(new LambdaQueryWrapper<ShortLinkDO>()
                .eq(ShortLinkDO::getCode, shortLinkCode)
                .eq(ShortLinkDO::getDel, 0));
        return shortLinkDO;
    }

    /**
     * 删除短链
     *
     * @return
     */
    @Override
    public int del(ShortLinkDO shortLinkDO) {
        int rows = shortLinkMapper.update(null, new LambdaUpdateWrapper<ShortLinkDO>()
                .eq(ShortLinkDO::getCode, shortLinkDO.getCode())
                .eq(ShortLinkDO::getAccountNo, shortLinkDO.getAccountNo())
                .set(ShortLinkDO::getDel, 1));

        return rows;
    }

    /**
     * C端更新短链
     *
     * @param shortLinkDO
     * @return
     */
    @Override
    public int update(ShortLinkDO shortLinkDO) {
        int rows = shortLinkMapper.update(null, new LambdaUpdateWrapper<ShortLinkDO>()
                .eq(ShortLinkDO::getCode, shortLinkDO.getCode())
                .eq(ShortLinkDO::getDel, 0)
                .eq(ShortLinkDO::getAccountNo, shortLinkDO.getAccountNo())
                .set(ShortLinkDO::getTitle, shortLinkDO.getTitle())
                .set(ShortLinkDO::getDomain, shortLinkDO.getDomain()));
        return rows;
    }
}
