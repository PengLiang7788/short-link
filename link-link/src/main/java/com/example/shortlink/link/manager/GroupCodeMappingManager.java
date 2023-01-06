package com.example.shortlink.link.manager;

import com.example.shortlink.common.enums.ShortLinkStateEnum;
import com.example.shortlink.link.model.GroupCodeMappingDo;

import java.util.Map;
import java.util.Objects;

/**
 * @author 彭亮
 * @create 2023-01-06 12:02
 */
public interface GroupCodeMappingManager {

    /**
     * 查找详情
     *
     * @param mappingId
     * @param accountNo
     * @param groupId
     * @return
     */
    GroupCodeMappingDo findByGroupIdAndMappingId(Long mappingId, Long accountNo, Long groupId);

    /**
     * 新增
     *
     * @param groupCodeMappingDo
     * @return
     */
    int add(GroupCodeMappingDo groupCodeMappingDo);

    /**
     * 根据短链码删除
     *
     * @param shortLinkCode
     * @param accountNo
     * @param groupId
     * @return
     */
    int del(String shortLinkCode, Long accountNo, Long groupId);

    /**
     * 分页查找
     *
     * @param page
     * @param size
     * @param accountNo
     * @param groupId
     * @return
     */
    Map<String, Object> pageShortLinkByGroupId(Integer page, Integer size, Long accountNo, Long groupId);

    /**
     * 更新短链码状态
     * @param accountNo
     * @param groupId
     * @param shortLinkCode
     * @param shortLinkStateEnum
     * @return
     */
    int updateGroupCodeMappingState(Long accountNo, Long groupId, String shortLinkCode, ShortLinkStateEnum shortLinkStateEnum);
}