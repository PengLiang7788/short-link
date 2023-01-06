package com.example.shortlink.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 彭亮
 * @create 2023-01-06 18:51
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventMessage implements Serializable {
    /**
     * 消息队列的消息id
     */
    private String messageId;

    /**
     * 事件类型
     */
    private String eventMessageType;

    /**
     * 业务id
     */
    private String bizId;

    /**
     * 账号
     */
    private Long accountNo;

    /**
     * 消息体
     */
    private String content;

    /**
     * 备注
     */
    private String remark;
}
