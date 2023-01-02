package com.example.shortlink.account.config;

import com.example.shortlink.common.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 彭亮
 * @create 2023-01-02 15:30
 */
@Configuration
@Slf4j
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                // 添加拦截器
                .addPathPatterns("/api/account/*/**","/api/traffic/*/**")
                // 排除不拦截
                .excludePathPatterns(
                        "/api/account/*/register","/api/account/*/login","/api/account/*/upload",
                        "/api/notify/*/captcha","/api/notify/*/send_code");
    }
}
