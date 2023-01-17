package com.example.shortlink.app.util;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author 彭亮
 * @create 2023-01-17 20:11
 */
@Slf4j
public class UtilTest {

    @Test
    public void testUserAgent(){
        String userAgentStr = "";

        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();

        String browserName = browser.getGroup().getName();
        String os = operatingSystem.getGroup().getName();
        String manufacture = operatingSystem.getManufacturer().getName();
        String deviceType = operatingSystem.getDeviceType().getName();


    }

}
