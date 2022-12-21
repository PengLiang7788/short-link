package com.example.shortlink.common.exception;

import com.example.shortlink.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 彭亮
 * @create 2022-12-21 13:22
 */
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public JsonData handler(Exception e){
        if (e instanceof BizException){
            BizException bizException = (BizException) e;
            log.error("[业务异常]:{}",e);
            return JsonData.buildCodeAndMsg(bizException.getCode(), bizException.getMsg());
        }else {
            log.error("[系统异常]:{}",e);
            return JsonData.buildError("系统异常");
        }
    }

}
