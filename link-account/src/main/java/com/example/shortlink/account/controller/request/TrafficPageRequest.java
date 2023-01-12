package com.example.shortlink.account.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 彭亮
 * @create 2023-01-12 9:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficPageRequest {

    private Integer page;

    private Integer size;

}
