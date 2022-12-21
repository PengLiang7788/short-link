package com.example.link.account.sms;

import com.example.shortlink.account.AccountApplication;
import com.example.shortlink.account.component.SmsComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 彭亮
 * @create 2022-12-21 15:56
 */
@SpringBootTest(classes = AccountApplication.class)
@RunWith(SpringRunner.class)
public class SmsComponentTest {

    @Autowired
    private SmsComponent smsComponent;

    @Test
    public void testSms(){
        smsComponent.send("18107024620","1234");
    }

}
