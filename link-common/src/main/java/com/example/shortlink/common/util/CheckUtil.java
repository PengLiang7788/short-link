package com.example.shortlink.common.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 彭亮
 * @create 2022-12-17 20:54
 */
public class CheckUtil {

    /**
     * 邮箱正则
     */
    private static final Pattern MAIL_PATTERN = Pattern.compile("/^[A-Za-z0-9]+([_\\.][A-Za-z0-9]+)*@([A-Za-z0-9\\-]+\\.)+[A-Za-z]{2,6}$/");

    /**
     * 手机号正则
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");

    /**
     * 是否是邮箱
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        if (StringUtils.isEmpty(email)){
            return false;
        }
        Matcher matcher = MAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    public static boolean isPhone(String phone){
        if (StringUtils.isEmpty(phone)){
            return false;
        }
        Matcher matcher = PHONE_PATTERN.matcher(phone);
        return matcher.matches();
    }

}
