package com.example.shortlink.link.service;

import com.example.shortlink.link.vo.DomainVo;

import java.util.List;

/**
 * @author 彭亮
 * @create 2023-01-06 14:09
 */
public interface DomainService {
    /**
     * 列举全部域名
     */
    List<DomainVo> listAll();

}
