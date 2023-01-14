package com.example.shortlink.account.controller.request;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2023-01-12 10:06
 */
@Data
public class UseTrafficRequest {

    /**
     * 账号
     */
    private Long accountNo;

    /**
     * 业务id，短链码
     */
    private String bizId;

}
