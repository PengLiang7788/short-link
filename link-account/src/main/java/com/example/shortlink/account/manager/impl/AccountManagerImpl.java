package com.example.shortlink.account.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.shortlink.account.manager.AccountManager;
import com.example.shortlink.account.mapper.AccountMapper;
import com.example.shortlink.account.model.AccountDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 彭亮
 * @create 2022-12-21 14:38
 */
@Component
@Slf4j
public class AccountManagerImpl implements AccountManager {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public int insert(AccountDO accountDO) {
        return accountMapper.insert(accountDO);
    }

    /**
     * 根据手机号查找账户
     * @param phone
     * @return
     */
    @Override
    public List<AccountDO> findByPhone(String phone) {
        List<AccountDO> accountDOList = accountMapper
                .selectList(new LambdaQueryWrapper<AccountDO>().eq(AccountDO::getPhone, phone));
        return accountDOList;
    }
}
