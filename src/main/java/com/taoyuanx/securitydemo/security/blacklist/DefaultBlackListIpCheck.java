package com.taoyuanx.securitydemo.security.blacklist;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author dushitaoyuan
 * @desc 默认黑名单检测实现类
 * @date 2019/8/30
 */
public class DefaultBlackListIpCheck implements BlackListIpCheck {
   private BloomFilter<CharSequence> ipBlackList;
   public DefaultBlackListIpCheck(List<String> ipBlackList){
       if(ipBlackList==null||ipBlackList.isEmpty()){
           throw new RuntimeException("黑名单ip不可为空");
       }
       this.ipBlackList = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), ipBlackList.size());
       for(String blackListIp:ipBlackList){
           this.ipBlackList.put(blackListIp);
       }
   }


    @Override
    public boolean ipInBlackList(String ip) {
        return ipBlackList.mightContain(ip);
    }
}
