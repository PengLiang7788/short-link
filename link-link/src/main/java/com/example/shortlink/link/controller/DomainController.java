package com.example.shortlink.link.controller;

import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.link.model.DomainDo;
import com.example.shortlink.link.service.DomainService;
import com.example.shortlink.link.vo.DomainVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 彭亮
 * @create 2023-01-06 11:59
 */
@RestController
@RequestMapping("/api/domain/v1")
public class DomainController {

    @Autowired
    private DomainService domainService;

    /**
     * 列举全部可用域名列表
     * @return
     */
    @GetMapping("/list")
    public JsonData listAll(){
        List<DomainVo> list = domainService.listAll();
        return JsonData.buildSuccess(list);
    }

}
