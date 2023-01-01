package com.example.shortlink.account.controller.request;

import lombok.Data;

/**
 * @author 彭亮
 * @create 2023-01-01 11:21
 */
@Data
public class AccountLoginRequest {

    private String phone;

    private String pwd;

}
