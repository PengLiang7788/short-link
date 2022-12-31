package com.example.shortlink.account.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author 彭亮
 * @create 2022-12-31 20:20
 */
public interface FileService {
    /**
     * 上传用户头像
     * @param file
     * @return
     */
    String uploadUserImg(MultipartFile file);
}
