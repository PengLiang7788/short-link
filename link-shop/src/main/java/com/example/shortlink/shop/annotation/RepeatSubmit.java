package com.example.shortlink.shop.annotation;

import java.lang.annotation.*;

/**
 * 自定义防重提交
 *
 * @author 彭亮
 * @create 2023-01-09 15:48
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {

    /**
     * 防重提交支持两种，一个是方法参数一个是令牌
     */
    enum Type {PARAM, TOKEN}

    /**
     * 默认防重提交是方法参数
     * @return
     */
    Type limitType() default Type.PARAM;

    /**
     * 加锁过期时间，默认5秒
     * @return
     */
    long lockTime() default 5;
}
