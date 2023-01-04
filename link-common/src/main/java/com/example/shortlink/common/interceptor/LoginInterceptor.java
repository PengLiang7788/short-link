package com.example.shortlink.common.interceptor;

import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.model.LoginUser;
import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.common.util.JWTUtil;
import com.example.shortlink.common.util.JsonData;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 彭亮
 * @create 2023-01-01 13:19
 */
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            response.setStatus(HttpStatus.NO_CONTENT.value());
            return true;
        }
        String accessToken = request.getHeader("token");
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter("token");
        }
        if (StringUtils.isNotBlank(accessToken)) {
            Claims claims = JWTUtil.checkJWT(accessToken);
            if (claims == null) {
                // 未登录
                CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
                return false;
            }
            long accountNo = Long.parseLong(claims.get("account_no").toString());
            String headImg = (String) claims.get("head_img");
            String username = (String) claims.get("username");
            String phone = (String) claims.get("phone");
            String auth = (String) claims.get("auth");
            String mail = (String) claims.get("mail");
            LoginUser loginUser = LoginUser.builder()
                    .accountNo(accountNo)
                    .username(username)
                    .auth(auth)
                    .phone(phone)
                    .mail(mail)
                    .headImg(headImg)
                    .build();
//            request.setAttribute("loginUser",loginUser);
            // 通过threadlocal
            threadLocal.set(loginUser);
            return true;
        }
        CommonUtil.sendJsonMessage(response,JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }
}
