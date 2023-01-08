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
     * @return
     */
    int del(GroupCodeMappingDo groupCodeMappingDo);

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
     *
     * @param accountNo
     * @param groupId
     * @param shortLinkCode
     * @param shortLinkStateEnum
     * @return
     */
    int updateGroupCodeMappingState(Long accountNo, Long groupId, String shortLinkCode, ShortLinkStateEnum shortLinkStateEnum);

    /**
     * 查找B端短链是否存在
     *
     * @param shortLinkCode
     * @param groupId
     * @param accountNo
     * @return
     */
    GroupCodeMappingDo findByCodeAndGroupId(String shortLinkCode, Long groupId, Long accountNo);

    /**
     * 更新短链
     *
     * @param groupCodeMappingDo
     * @return
     */
    int update(GroupCodeMappingDo groupCodeMappingDo);
}
