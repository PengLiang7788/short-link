package com.example.shortlink.link.service.impl;

import com.example.shortlink.common.interceptor.LoginInterceptor;
import com.example.shortlink.link.manager.DomainManager;
import com.example.shortlink.link.model.DomainDo;
import com.example.shortlink.link.service.DomainService;
import com.example.shortlink.link.vo.DomainVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 彭亮
 * @create 2023-01-06 14:09
 */
@Slf4j
@Service
public class DomainServiceImpl implements DomainService {

    @Autowired
    private DomainManager domainManager;

    /**
     * 列举全部可用域名
     *
     * @return
     */
    @Override
    public List<DomainVo> listAll() {

        long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        List<DomainDo> officialDomainList = domainManager.listOfficialDomain();
        List<DomainDo> customDomainList = domainManager.listCustomDomain(accountNo);

        customDomainList.addAll(officialDomainList);

        List<DomainVo> listAll = customDomainList.stream().map(item -> {
            DomainVo domainVo = new DomainVo();
            BeanUtils.copyProperties(item, domainVo);
            return domainVo;
        }).collect(Collectors.toList());

        return listAll;
    }
}
