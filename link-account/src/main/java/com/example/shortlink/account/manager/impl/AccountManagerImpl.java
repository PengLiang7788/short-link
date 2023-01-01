package com.example.shortlink.account.manager.impl;

import com.example.shortlink.account.manager.AccountManager;
import com.example.shortlink.account.model.AccountDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 彭亮
 * @create 2022-12-21 14:38
 */
@Component
@Slf4j
public class AccountManagerImpl implements AccountManager {

    @Autowired
    private AccountManager accountManager;

    @Override
    public int insert(AccountDO accountDO) {
        return accountManager.insert(accountDO);
    }
}
