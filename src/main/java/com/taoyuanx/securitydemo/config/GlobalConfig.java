package com.taoyuanx.securitydemo.config;

import com.taoyuanx.securitydemo.security.ratelimit.AbstractRateLimiter;
import com.taoyuanx.securitydemo.security.ratelimit.RedisRateLimiter;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.UnknownHostException;
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

    /**
     * 限流实现类
     * @param redisTemplate
     * @return
     */
    @Bean
    public AbstractRateLimiter rateLimiter(RedisTemplate redisTemplate){
        RedisRateLimiter redisRateLimiter=new RedisRateLimiter(redisTemplate);
        return  redisRateLimiter;
    }


}
