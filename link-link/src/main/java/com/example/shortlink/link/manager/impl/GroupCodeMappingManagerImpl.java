package com.example.shortlink.link.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shortlink.common.enums.ShortLinkStateEnum;
import com.example.shortlink.link.manager.GroupCodeMappingManager;
import com.example.shortlink.link.mapper.GroupCodeMappingMapper;
import com.example.shortlink.link.model.GroupCodeMappingDo;
import com.example.shortlink.link.vo.GroupCodeMappingVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 彭亮
 * @create 2023-01-06 12:08
 */
@Component
@Slf4j
public class GroupCodeMappingManagerImpl implements GroupCodeMappingManager {

    @Autowired
    private GroupCodeMappingMapper groupCodeMappingMapper;

    /**
     * 查找详情
     *
     * @param mappingId
     * @param accountNo
     * @param groupId
     * @return
     */
    @Override
    public GroupCodeMappingDo findByGroupIdAndMappingId(Long mappingId, Long accountNo, Long groupId) {
        GroupCodeMappingDo groupCodeMappingDo = groupCodeMappingMapper.selectOne(new LambdaQueryWrapper<GroupCodeMappingDo>()
                .eq(GroupCodeMappingDo::getId, mappingId)
                .eq(GroupCodeMappingDo::getAccountNo, accountNo)
                .eq(GroupCodeMappingDo::getGroupId, groupId));
        return groupCodeMappingDo;
    }

    /**
     * 新增
     *
     * @param groupCodeMappingDo
     * @return
     */
    @Override
    public int add(GroupCodeMappingDo groupCodeMappingDo) {
        return groupCodeMappingMapper.insert(groupCodeMappingDo);
    }

    /**
     * 根据短链码删除
     *
     * @param shortLinkCode
     * @param accountNo
     * @param groupId
     * @return
     */
    @Override
    public int del(String shortLinkCode, Long accountNo, Long groupId) {
        int rows = groupCodeMappingMapper.update(null, new LambdaUpdateWrapper<GroupCodeMappingDo>()
                .eq(GroupCodeMappingDo::getCode, shortLinkCode)
                .eq(GroupCodeMappingDo::getAccountNo, accountNo)
                .eq(GroupCodeMappingDo::getGroupId, groupId)
                .set(GroupCodeMappingDo::getDel, 1));
        return rows;
    }

    /**
     * 分页查找
     *
     * @param page
     * @param size
     * @param accountNo
     * @param groupId
     * @return
     */
    @Override
    public Map<String, Object> pageShortLinkByGroupId(Integer page, Integer size, Long accountNo, Long groupId) {
        Page<GroupCodeMappingDo> pageInfo = new Page<>(page, size);
        Page<GroupCodeMappingDo> groupCodeMappingDoPage = groupCodeMappingMapper
                .selectPage(pageInfo, new LambdaQueryWrapper<GroupCodeMappingDo>()
                        .eq(GroupCodeMappingDo::getAccountNo, accountNo)
                        .eq(GroupCodeMappingDo::getGroupId, groupId));

        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("total_record", groupCodeMappingDoPage.getTotal());
        pageMap.put("total_page", groupCodeMappingDoPage.getPages());
        pageMap.put("current_data", groupCodeMappingDoPage.getRecords().stream().map(item -> {
            GroupCodeMappingVo groupCodeMappingVo = new GroupCodeMappingVo();
            BeanUtils.copyProperties(item, groupCodeMappingVo);
            return groupCodeMappingVo;
        }).collect(Collectors.toList()));

        return pageMap;
    }

    /**
     * 更新短链码状态
     *
     * @param accountNo
     * @param groupId
     * @param shortLinkCode
     * @param shortLinkStateEnum
     * @return
     */
    @Override
    public int updateGroupCodeMappingState(Long accountNo, Long groupId, String shortLinkCode, ShortLinkStateEnum shortLinkStateEnum) {

        int rows = groupCodeMappingMapper.update(null, new LambdaUpdateWrapper<GroupCodeMappingDo>()
                .eq(GroupCodeMappingDo::getCode, shortLinkCode)
                .eq(GroupCodeMappingDo::getAccountNo, accountNo)
                .eq(GroupCodeMappingDo::getGroupId, groupId)
                .set(GroupCodeMappingDo::getState, shortLinkStateEnum.name()));
        return rows;
    }
}
