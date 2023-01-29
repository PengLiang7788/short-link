package com.example.shortlink.account.manager;

import com.example.shortlink.account.model.AccountDO;

import java.util.List;

/**
 * @author 彭亮
 * @create 2022-12-21 14:38
 */
public interface AccountManager {

    int insert(AccountDO accountDO);

    List<AccountDO> findByPhone(String phone);

    /**
     * 查询用户个人信息
     * @param accountNo
     * @return
     */
    AccountDO detail(long accountNo);
}
