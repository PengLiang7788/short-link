package com.example.shortlink.link.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.common.enums.DomainTypeEnum;
import com.example.shortlink.link.manager.DomainManager;
import com.example.shortlink.link.mapper.DomainMapper;
import com.example.shortlink.link.model.DomainDo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 彭亮
 * @create 2023-01-06 14:00
 */
@Component
@Slf4j
public class DomainManagerImpl implements DomainManager {

    @Autowired
    private DomainMapper domainMapper;

    /**
     * 查找详情
     *
     * @param id
     * @param accountNo
     * @return
     */
    @Override
    public DomainDo findById(Long id, Long accountNo) {
        return domainMapper.selectOne(new LambdaQueryWrapper<DomainDo>().eq(DomainDo::getId, id).eq(DomainDo::getAccountNo, accountNo));
    }

    /**
     * 根据类型查找
     *
     * @param id
     * @param domainTypeEnum
     * @return
     */
    @Override
    public DomainDo findByDomainTypeAndId(Long id, DomainTypeEnum domainTypeEnum) {
        return domainMapper.selectOne(new LambdaQueryWrapper<DomainDo>().eq(DomainDo::getId, id).eq(DomainDo::getDomainType, domainTypeEnum.name()));
    }

    /**
     * 新增
     *
     * @param domainDo
     * @return
     */
    @Override
    public int addDomain(DomainDo domainDo) {
        return domainMapper.insert(domainDo);
    }

    /**
     * 列举所有官方域名
     *
     * @return
     */
    @Override
    public List<DomainDo> listOfficialDomain() {
        return domainMapper.selectList(new LambdaQueryWrapper<DomainDo>().eq(DomainDo::getDomainType, DomainTypeEnum.OFFICIAL.name()));
    }

    /**
     * 列举所有自定义域名
     *
     * @param accountNo
     * @return
     */
    @Override
    public List<DomainDo> listCustomDomain(Long accountNo) {
        return domainMapper.selectList(new LambdaQueryWrapper<DomainDo>()
                .eq(DomainDo::getDomainType, DomainTypeEnum.CUSTOM.name())
                .eq(DomainDo::getAccountNo, accountNo));
    }
}
