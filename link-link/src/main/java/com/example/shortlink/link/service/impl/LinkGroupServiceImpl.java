package com.example.shortlink.link.service.impl;

import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.link.controller.request.LinkGroupAddRequest;
import com.example.shortlink.link.controller.request.LinkGroupUpdateRequest;
import com.example.shortlink.link.manager.LinkGroupManager;
import com.example.shortlink.link.model.LinkGroupDO;
import com.example.shortlink.link.service.LinkGroupService;
import com.example.shortlink.link.vo.LinkGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 根据分组id查询分组详情
     *
     * @param groupId
     * @return
     */
    @Override
    public LinkGroupVo detail(Long groupId) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        LinkGroupDO linkGroupDO = linkGroupManager.detail(groupId, accountNo);

        LinkGroupVo linkGroupVo = new LinkGroupVo();
        BeanUtils.copyProperties(linkGroupDO, linkGroupVo);
        return linkGroupVo;
    }

    /**
     * 列出用户的全部分组
     *
     * @return
     */
    @Override
    public List<LinkGroupVo> listAllGroup() {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        List<LinkGroupDO> linkGroupDoList = linkGroupManager.listAllGroup(accountNo);

        List<LinkGroupVo> linkGroupVoList = linkGroupDoList.stream().map(item -> {
            LinkGroupVo linkGroupVo = new LinkGroupVo();
            BeanUtils.copyProperties(item, linkGroupVo);
            return linkGroupVo;
        }).collect(Collectors.toList());

        return linkGroupVoList;
    }

    /**
     * 更改分组名
     * @param updateRequest
     * @return
     */
    @Override
    public int updateById(LinkGroupUpdateRequest updateRequest) {
        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        LinkGroupDO linkGroupDO = new LinkGroupDO();
        linkGroupDO.setTitle(updateRequest.getTitle());
        linkGroupDO.setId(updateRequest.getId());
        linkGroupDO.setAccountNo(accountNo);

        int rows = linkGroupManager.updateById(linkGroupDO);

        return rows;
    }
}
