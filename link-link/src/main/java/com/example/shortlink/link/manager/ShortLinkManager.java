package com.example.shortlink.link.manager;

import com.example.shortlink.link.model.ShortLinkDO;

/**
 * @author 彭亮
 * @create 2023-01-04 19:25
 */
public interface ShortLinkManager {

    /**
     * 新增
     * @param shortLinkDO
     * @return
     */
    int addShortLink(ShortLinkDO shortLinkDO);

    /**
     * 根据短链码查找
     * @param shortLinkCode
     * @return
     */
    ShortLinkDO findByShortLinkCode(String shortLinkCode);

    /**
     * 删除短链
     * @param shortLinkCode
     * @param accountNo
     * @return
     */
    int del(String shortLinkCode,Long accountNo);

}
