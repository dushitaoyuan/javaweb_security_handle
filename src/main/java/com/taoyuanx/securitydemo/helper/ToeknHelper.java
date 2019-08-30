package com.taoyuanx.securitydemo.helper;

import com.taoyuanx.securitydemo.config.GlobalConfig;
import com.taoyuanx.securitydemo.constant.SystemConstants;
import com.taoyuanx.securitydemo.dto.AccountDTO;
import com.taoyuanx.securitydemo.utils.SimpleTokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author dushitaoyuan
 * @desc token签发帮助类
 * @date 2019/8/30
 */
@Component
public class ToeknHelper {
    @Autowired
    GlobalConfig globalConfig;

    public String create(AccountDTO accountDTO) {
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put(SystemConstants.TOKEN_ACCOUNTID_KEY, String.valueOf(accountDTO.getAccountId()));
        tokenData.put(SystemConstants.TOKEN_ACCOUNT_STATUS_KEY, String.valueOf(accountDTO.getAccountStatus()));
        return SimpleTokenManager.createToken(globalConfig.getTokenKey(), tokenData, globalConfig.getExpireSeconds(), TimeUnit.SECONDS);
    }
    public Map<String, Object> vafy(String token) {
        return  SimpleTokenManager.vafy(globalConfig.getTokenKey(),token);
    }

    public Long getAccountId(Map<String, Object> tokenData) {
        return Long.parseLong(tokenData.get(SystemConstants.TOKEN_ACCOUNTID_KEY).toString());
    }

    public Integer getAccountStatus(Map<String, Object> tokenData) {
        return (Integer) tokenData.get(SystemConstants.TOKEN_ACCOUNT_STATUS_KEY);
    }
}
