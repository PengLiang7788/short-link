package com.example.shortlink.link.manager;

import com.example.shortlink.common.enums.DomainTypeEnum;
import com.example.shortlink.link.model.DomainDo;

import java.util.List;

/**
 * @author 彭亮
 * @create 2023-01-06 13:52
 */
public interface DomainManager {

    /**
     * 查找详情
     * @param id
     * @param accountNo
     * @return
     */
    DomainDo findById(Long id, Long accountNo);

    /**
     * 根据短链类型查找
     * @param id
     * @param domainTypeEnum
     * @return
     */
    DomainDo findByDomainTypeAndId(Long id, DomainTypeEnum domainTypeEnum);

    /**
     * 新增
     * @param domainDo
     * @return
     */
    int addDomain(DomainDo domainDo);

    /**
     * 列举全部官方域名
     * @return
     */
    List<DomainDo> listOfficialDomain();

    /**
     * 列举全部的自定义域名
     * @param accountNo
     * @return
     */
    List<DomainDo> listCustomDomain(Long accountNo);

}
