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
                .eq(GroupCodeMappingDo::getGroupId, groupId)
                .eq(GroupCodeMappingDo::getDel, 0));
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
     * @return
     */
    @Override
    public int del(GroupCodeMappingDo groupCodeMappingDo) {
        int rows = groupCodeMappingMapper.update(null, new LambdaUpdateWrapper<GroupCodeMappingDo>()
                .eq(GroupCodeMappingDo::getAccountNo, groupCodeMappingDo.getAccountNo())
                .eq(GroupCodeMappingDo::getGroupId, groupCodeMappingDo.getGroupId())
                .eq(GroupCodeMappingDo::getId, groupCodeMappingDo.getId())
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
                        .eq(GroupCodeMappingDo::getGroupId, groupId)
                        .eq(GroupCodeMappingDo::getDel, 0)
                        .orderByDesc(GroupCodeMappingDo::getGmtCreate));

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
                .eq(GroupCodeMappingDo::getDel, 0)
                .set(GroupCodeMappingDo::getState, shortLinkStateEnum.name()));
        return rows;
    }

    /**
     * 查找是否存在
     *
     * @param shortLinkCode
     * @param groupId
     * @param accountNo
     * @return
     */
    @Override
    public GroupCodeMappingDo findByCodeAndGroupId(String shortLinkCode, Long groupId, Long accountNo) {
        GroupCodeMappingDo groupCodeMappingDo = groupCodeMappingMapper.selectOne(new LambdaQueryWrapper<GroupCodeMappingDo>().eq(GroupCodeMappingDo::getCode, shortLinkCode)
                .eq(GroupCodeMappingDo::getGroupId, groupId)
                .eq(GroupCodeMappingDo::getAccountNo, accountNo)
                .eq(GroupCodeMappingDo::getDel, 0));
        return groupCodeMappingDo;
    }

    /**
     * B端更新短链
     *
     * @param groupCodeMappingDo
     * @return
     */
    @Override
    public int update(GroupCodeMappingDo groupCodeMappingDo) {
        int rows = groupCodeMappingMapper.update(null, new LambdaUpdateWrapper<GroupCodeMappingDo>()
                .eq(GroupCodeMappingDo::getId, groupCodeMappingDo.getId())
                .eq(GroupCodeMappingDo::getGroupId, groupCodeMappingDo.getGroupId())
                .eq(GroupCodeMappingDo::getAccountNo, groupCodeMappingDo.getAccountNo())
                .eq(GroupCodeMappingDo::getDel, 0)
                .set(GroupCodeMappingDo::getTitle, groupCodeMappingDo.getTitle())
                .set(GroupCodeMappingDo::getDomain, groupCodeMappingDo.getDomain()));
        return rows;
    }
}
