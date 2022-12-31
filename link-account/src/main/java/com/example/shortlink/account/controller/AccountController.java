package com.example.shortlink.account.controller;

import com.example.shortlink.account.service.FileService;
import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 彭亮
 * @create 2022-12-21 14:37
 */
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    private FileService fileService;

    /**
     * 文件上传  默认最大1M
     *
     * @param file
     * @return
     */
    @PostMapping("upload")
    public JsonData uploadUserImg(@RequestPart("file") MultipartFile file) {

        String result = fileService.uploadUserImg(file);

        return result != null ? JsonData.buildSuccess(result) : JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
    }
}
