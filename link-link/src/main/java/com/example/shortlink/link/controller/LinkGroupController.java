package com.example.shortlink.link.controller;


import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.link.controller.request.LinkGroupAddRequest;
import com.example.shortlink.link.controller.request.LinkGroupUpdateRequest;
import com.example.shortlink.link.service.LinkGroupService;
import com.example.shortlink.link.vo.LinkGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 彭亮
 * @since 2023-01-03
 */
@RestController
@RequestMapping("/api/group/v1")
public class LinkGroupController {

    @Autowired
    private LinkGroupService linkGroupService;

    /**
     * 创建分组
     *
     * @param addRequest
     * @return
     */
    @PostMapping("/add")
    public JsonData add(@RequestBody LinkGroupAddRequest addRequest) {
        int rows = linkGroupService.add(addRequest);
        return rows == 1 ? JsonData.buildSuccess() : JsonData.buildResult(BizCodeEnum.GROUP_ADD_FAIL);
    }

    /**
     * 根据分组id删除分组
     *
     * @param groupId
     * @return
     */
    @DeleteMapping("/del/{group_id}")
    public JsonData del(@PathVariable("group_id") Long groupId) {
        int rows = linkGroupService.del(groupId);
        return rows == 1 ? JsonData.buildSuccess() : JsonData.buildResult(BizCodeEnum.GROUP_NOT_EXIST);
    }

    /**
     * 根据分组id查询分组详情
     *
     * @param groupId
     * @return
     */
    @GetMapping("/detail/{group_id}")
    public JsonData detail(@PathVariable("group_id") Long groupId) {
        LinkGroupVo linkGroupVo = linkGroupService.detail(groupId);
        return JsonData.buildSuccess(linkGroupVo);
    }

    /**
     * 列出用户的全部分组
     *
     * @return
     */
    @GetMapping("/list")
    public JsonData findUserAllLinkGroup() {
        List<LinkGroupVo> linkGroupVoList = linkGroupService.listAllGroup();
        return JsonData.buildSuccess(linkGroupVoList);
    }


    @PutMapping("/update")
    public JsonData update(@RequestBody LinkGroupUpdateRequest updateRequest) {
        int rows = linkGroupService.updateById(updateRequest);
        return rows == 1 ? JsonData.buildSuccess() : JsonData.buildResult(BizCodeEnum.GROUP_OPER_FAIL);
    }

}

