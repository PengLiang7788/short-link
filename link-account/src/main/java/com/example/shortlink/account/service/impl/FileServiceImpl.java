package com.example.shortlink.account.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.example.shortlink.account.config.OSSConfig;
import com.example.shortlink.account.service.FileService;
import com.example.shortlink.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 彭亮
 * @create 2022-12-31 20:21
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private OSSConfig ossConfig;

    /**
     * 上传用户头像
     *
     * @param file
     * @return
     */
    @Override
    public String uploadUserImg(MultipartFile file) {
        String bucketname = ossConfig.getBucketname();
        String accessKeyId = ossConfig.getAccessKeyId();
        String endpoint = ossConfig.getEndpoint();
        String accessKeySecret = ossConfig.getAccessKeySecret();

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 获取文件原始名称
        String originalFilename = file.getOriginalFilename();

        //按照日期进行归档 2022/12/31/xxx.jpg
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        String folder = pattern.format(ldt);
        String fileName = CommonUtil.generateUUID();
        // 获取文件扩展名
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 在oss上的bucket创建文件夹
        String newFileName = "user/" + folder + "/" + fileName + extension;

        try {
            PutObjectResult putObjectResult = ossClient.putObject(bucketname, newFileName, file.getInputStream());
            if (putObjectResult != null) {
                String imgUrl = "https://" + bucketname + "." + endpoint + "/" + newFileName;
                return imgUrl;
            }
        } catch (IOException e) {
            log.error("文件上传oss失败:{}",e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return null;

    }
}
