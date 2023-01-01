package com.example.shortlink.account.controller;

import com.example.shortlink.account.controller.request.AccountLoginRequest;
import com.example.shortlink.account.controller.request.AccountRegisterRequest;
import com.example.shortlink.account.service.AccountService;
import com.example.shortlink.account.service.FileService;
import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 彭亮
 * @create 2022-12-21 14:37
 */
@RestController
@RequestMapping("/api/account/v1")
public class AccountController {

    @Autowired
    private FileService fileService;

    @Autowired
    private AccountService accountService;

    /**
     * 文件上传  SpringBoot默认文件最大1M
     * 通过在yml中配置 spring.servlet.multipart.max-file-size 来设置默认上传文件的大小
     *
     * @param file 上传的文件
     * @return
     */
    @PostMapping("upload")
    public JsonData uploadUserImg(@RequestPart("file") MultipartFile file) {

        String result = fileService.uploadUserImg(file);

        return result != null ? JsonData.buildSuccess(result) : JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
    }

    /**
     * 用户注册
     *
     * @param registerRequest
     * @return
     */
    @PostMapping("register")
    public JsonData register(@RequestBody AccountRegisterRequest registerRequest) {
        JsonData jsonData = accountService.register(registerRequest);
        return jsonData;
    }

    @PostMapping("login")
    public JsonData login(@RequestBody AccountLoginRequest loginRequest) {
        JsonData jsonData = accountService.login(loginRequest);
        return jsonData;
    }

}
