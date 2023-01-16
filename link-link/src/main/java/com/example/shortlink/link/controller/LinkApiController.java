package com.example.shortlink.link.controller;

import com.example.shortlink.common.enums.ShortLinkStateEnum;
import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.link.service.LogService;
import com.example.shortlink.link.service.ShortLinkService;
import com.example.shortlink.link.vo.ShortLinkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 彭亮
 * @create 2023-01-06 9:32
 */
@Slf4j
@Controller
public class LinkApiController {

    @Autowired
    private ShortLinkService shortLinkService;

    @Autowired
    private LogService logService;

    /**
     * 根据短链码解析出原始网址
     * <p>
     * 解析301还是302，这边是返回http code是302
     * 为什么用301跳转而不是302跳转
     * 301是永久重定向，302是临时重定向
     * 短地址已经生成就不会发生变化，所以用301是同时对服务器压力也会有一定减少
     * 但是如果使用301就无法统计到短地址呗点击的次数
     * 所以选择302虽然回增加服务器压力，但是有很多数据可以获取进行解析
     *
     * @param shortLinkCode
     * @param request
     * @param response
     */
    @GetMapping(path = "/{shortLinkCode}")
    public void dispatch(@PathVariable("shortLinkCode") String shortLinkCode,
                         HttpServletRequest request, HttpServletResponse response) {

        try {
            log.info("短链码:{}", shortLinkCode);
            // 判断短链码是否合规
            if (isLetterDigit(shortLinkCode)) {
                // 查找短链
                ShortLinkVo shortLinkVo = shortLinkService.parseShortLinkCode(shortLinkCode);

                if (shortLinkVo != null) {
                    logService.recordShortLinkLog(request, shortLinkCode, shortLinkVo.getAccountNo());
                }

                // 判断是否过期和可用
                if (isVisitable(shortLinkVo)) {
                    String originalUrl = CommonUtil.removeUrlPrefix(shortLinkVo.getOriginalUrl());

                    response.setHeader("Location", originalUrl);
                    // 302跳转
                    response.setStatus(HttpStatus.FOUND.value());
                } else {
                    response.setStatus(HttpStatus.NOT_FOUND.value());
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }


    }

    /**
     * 判断短链是否可用
     *
     * @param shortLinkVo
     * @return
     */
    private static boolean isVisitable(ShortLinkVo shortLinkVo) {
        if (shortLinkVo != null && shortLinkVo.getExpired().getTime() > CommonUtil.getCurrentTimestamp()) {
            if (ShortLinkStateEnum.ACTIVE.name().equalsIgnoreCase(shortLinkVo.getState())) {
                return true;
            }
        } else if (shortLinkVo != null && shortLinkVo.getExpired().getTime() == -1) {
            if (ShortLinkStateEnum.ACTIVE.name().equalsIgnoreCase(shortLinkVo.getState())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 仅包括数字和字母
     *
     * @param str
     * @return
     */
    private static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
    }

}
