package com.example.shortlink.account.service.impl;

import com.alibaba.nacos.common.utils.MD5Utils;
import com.example.shortlink.account.controller.request.AccountLoginRequest;
import com.example.shortlink.account.controller.request.AccountRegisterRequest;
import com.example.shortlink.account.manager.AccountManager;
import com.example.shortlink.account.model.AccountDO;
import com.example.shortlink.account.service.AccountService;
import com.example.shortlink.account.service.NotifyService;
import com.example.shortlink.common.enums.AuthTypeEnum;
import com.example.shortlink.common.enums.BizCodeEnum;
import com.example.shortlink.common.enums.SendCodeEnum;
import com.example.shortlink.common.model.LoginUser;
import com.example.shortlink.common.util.CommonUtil;
import com.example.shortlink.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 彭亮
 * @create 2022-12-21 14:38
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private AccountManager accountManager;

    /**
     * 用户注册
     * 手机验证码验证
     * 密码加密 -> MD5+盐
     * 账号唯一性检查
     * 插入数据库
     * 新注册用户福利发放
     *
     * @param registerRequest
     * @return
     */
    @Override
    public JsonData register(AccountRegisterRequest registerRequest) {
        boolean checkCode = false;
        // 判断验证码
        if (StringUtils.isNotBlank(registerRequest.getCode()) && StringUtils.isNotBlank(registerRequest.getPhone())) {
            checkCode = notifyService.checkCode(SendCodeEnum.USER_REGISTER, registerRequest.getPhone(), registerRequest.getCode());
        }
        if (!checkCode) {
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }

        AccountDO accountDO = new AccountDO();
        BeanUtils.copyProperties(registerRequest, accountDO);

        //TODO 生成唯一账号
        accountDO.setAccountNo(CommonUtil.getCurrentTimestamp());

        // 设置认证级别 默认级别
        accountDO.setAuth(AuthTypeEnum.DEFAULT.name());
        // 密码设置 密钥 盐
        accountDO.setSecret("$1$" + CommonUtil.getStringNumRandom(8));
        // 对密码进行加密
        String cryptPwd = Md5Crypt.md5Crypt(registerRequest.getPwd().getBytes(),accountDO.getSecret());
        accountDO.setPwd(cryptPwd);

        // 插入数据
        int rows = accountManager.insert(accountDO);
        log.info("rows:{},注册成功:{}",rows,accountDO);

        // 用户注册成功，发放福利 TODO
        userRegisterInitTask(accountDO);
        return JsonData.buildSuccess();
    }

    /**
     * 用户登陆
     * @param loginRequest
     * @return
     */
    @Override
    public JsonData login(AccountLoginRequest loginRequest) {
        List<AccountDO> accountDOList =
                accountManager.findByPhone(loginRequest.getPhone());
        if (accountDOList != null && accountDOList.size() == 1){
            AccountDO accountDO = accountDOList.get(0);
            String md5Crypt = Md5Crypt.md5Crypt(loginRequest.getPwd().getBytes(), accountDO.getSecret());
            if (md5Crypt.equals(accountDO.getPwd())){
                LoginUser loginUser = LoginUser.builder().build();
                BeanUtils.copyProperties(loginRequest,loginUser);

                //TODO 生成TOKEN JWT


                return JsonData.buildSuccess();

            } else {
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_PWD_ERROR);
            }


        }else {
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER);
        }
    }

    /**
     * 用户初始化，发放福利:发放流量包 TODO
     * @param accountDO
     */
    private void userRegisterInitTask(AccountDO accountDO) {

    }
}
