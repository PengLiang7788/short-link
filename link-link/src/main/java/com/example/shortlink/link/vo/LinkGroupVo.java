package com.example.shortlink.link.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author 彭亮
 * @create 2023-01-04 12:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LinkGroupVo {
    private Long id;
    /**
     * 组名
     */
    private String title;

    /**
     * 账号唯一编号
     */
    private Long accountNo;

    private Date gmtCreate;

    private Date gmtModified;

}
