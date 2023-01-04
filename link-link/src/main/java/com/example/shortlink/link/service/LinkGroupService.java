package com.example.shortlink.link.service;

import com.example.shortlink.link.controller.request.LinkGroupAddRequest;

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
}
