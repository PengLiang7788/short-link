package com.example.shortlink.common.exception;

import com.example.shortlink.common.enums.BizCodeEnum;
import lombok.Data;

/**
 * @author 彭亮
 * @create 2022-12-21 13:19
 */
@Data
public class BizException extends RuntimeException {
    private int code;

    private String msg;

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }
    public BizException(BizCodeEnum bizCodeEnum){
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.msg = bizCodeEnum.getMessage();
    }
}
