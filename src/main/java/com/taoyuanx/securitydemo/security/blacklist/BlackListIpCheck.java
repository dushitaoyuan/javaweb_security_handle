package com.taoyuanx.securitydemo.security.blacklist;

/**
 * @author dushitaoyuan
 * @desc 黑名单判断
 * @date 2019/8/30
 */
public interface  BlackListIpCheck {
    boolean ipInBlackList(String ip);


}
