package com.taoyuanx.securitydemo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * @author dushitaoyuan
 * @desc 系统全局配置
 * @date 2019/8/29
 */
@Data
@ConfigurationProperties("application.config")
@Configuration
public class GlobalConfig {
    @Autowired
    Environment environment;

    private String systemDomain;
    private List<String> refererCheckUrl;
    private List<String> refererCheckAllowDomains;

    private List<String> blackListIp;
    private String allowUploadExt;
    private String tokenKey;
    private Long expireSeconds;
    private String fileStorageDir;

    private String systemFileFormat;
    public String getConfig(String configKey) {
        return environment.getProperty(configKey);
    }


}
