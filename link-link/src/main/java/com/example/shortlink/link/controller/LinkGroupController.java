package com.example.shortlink.link.controller;


import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.util.JsonData;
import com.example.shortlink.link.controller.request.LinkGroupAddRequest;
import com.example.shortlink.link.controller.request.LinkGroupDelRequest;
import com.example.shortlink.link.service.LinkGroupService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}

