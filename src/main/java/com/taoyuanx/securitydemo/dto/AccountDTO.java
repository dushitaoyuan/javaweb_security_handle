package com.taoyuanx.securitydemo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dushitaoyuan
 * @desc 账户实体
 * @date 2019/8/29
 */
@Data
public class AccountDTO implements Serializable {
    private Long accountId;
    private Integer accountStatus;

}
