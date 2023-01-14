package com.example.shortlink.link.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 彭亮
 * @create 2023-01-12 10:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
