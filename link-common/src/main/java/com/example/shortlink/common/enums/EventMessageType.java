package com.example.shortlink.common.enums;

/**
 * @author 彭亮
 * @create 2023-01-06 19:32
 */
public enum EventMessageType {

    /**
     * 短链创建
     */
    SHORT_LINK_ADD,

    /**
     * 创建短链 C端
     */
    SHORT_LINK_ADD_LINK,

    /**
     * 创建短链 B端
     */
    SHORT_LINK_ADD_MAPPING,

    /**
     * 短链创建
     */
    SHORT_LINK_DEL,

    /**
     * 删除短链 C端
     */
    SHORT_LINK_DEL_LINK,

    /**
     * 删除短链 B端
     */
    SHORT_LINK_DEL_MAPPING,

    /**
     * 更新创建
     */
    SHORT_LINK_UPDATE,

    /**
     * 更新短链 C端
     */
    SHORT_LINK_UPDATE_LINK,

    /**
     * 更新短链 B端
     */
    SHORT_LINK_UPDATE_MAPPING,

    /**
     * 新建商品订单
     */
    PRODUCT_ORDER_NEW,

    /**
     * 订单支付
     */
    PRODUCT_ORDER_PAY,

    /**
     * 免费流量包发放消息
     */
    TRAFFIC_FREE_INIT
}
