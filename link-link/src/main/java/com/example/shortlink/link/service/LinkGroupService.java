package com.example.shortlink.link.service;

import com.example.shortlink.link.controller.request.LinkGroupAddRequest;
import com.example.shortlink.link.controller.request.LinkGroupUpdateRequest;
import com.example.shortlink.link.model.LinkGroupDO;
import com.example.shortlink.link.vo.LinkGroupVo;

import java.util.List;

/**
 * @author 彭亮
 * @create 2023-01-03 20:48
 */
public interface LinkGroupService {
    /**
     * 添加分组
     * @param addRequest
     * @return
     */
    int add(LinkGroupAddRequest addRequest);

    /**
     * 根据分组id删除分组
     * @param groupId
     * @return
     */
    int del(Long groupId);

    /**
     * 根据分组id查询分组详情信息
     * @param groupId
     * @return
     */
    LinkGroupVo detail(Long groupId);

    /**
     * 列出用户全部分组
     * @return
     */
    List<LinkGroupVo> listAllGroup();

    /**
     * 更改分组名
     * @param updateRequest
     * @return
     */
    int updateById(LinkGroupUpdateRequest updateRequest);
}
