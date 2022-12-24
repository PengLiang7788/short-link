package com.example.shortlink.account.service.impl;

import com.example.shortlink.account.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author 彭亮
 * @create 2022-12-23 13:47
 */
@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {



    @Override
    public void testSend() {
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
