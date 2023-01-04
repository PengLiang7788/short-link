package com.example.shortlink.link.service.impl;

import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.link.controller.request.LinkGroupAddRequest;
import com.example.shortlink.link.manager.LinkGroupManager;
import com.example.shortlink.link.model.LinkGroupDO;
import com.example.shortlink.link.service.LinkGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 彭亮
 * @create 2023-01-03 20:48
 */
@Service
public class LinkGroupServiceImpl implements LinkGroupService {

    @Autowired
    private LinkGroupManager linkGroupManager;

    /**
     * 添加分组
     *
     * @param addRequest
     * @return
     */
    @Override
    public int add(LinkGroupAddRequest addRequest) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        LinkGroupDO linkGroupDO = new LinkGroupDO();
        linkGroupDO.setAccountNo(accountNo);
        linkGroupDO.setTitle(addRequest.getTitle());

        int rows = linkGroupManager.add(linkGroupDO);

        return rows;
    }

    /**
     * 根据分组id删除分组
     *
     * @param groupId
     * @return
     */
    @Override
    public int del(Long groupId) {
        // 先获取账户号，防止越权删除
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        int rows = linkGroupManager.del(groupId, accountNo);
        return rows;
    }
}
