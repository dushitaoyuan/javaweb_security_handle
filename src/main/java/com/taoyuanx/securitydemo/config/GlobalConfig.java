package com.taoyuanx.securitydemo.config;

import com.taoyuanx.securitydemo.security.ratelimit.AbstractRateLimiter;
import com.taoyuanx.securitydemo.security.ratelimit.RedisRateLimiter;
import com.taoyuanx.securitydemo.utils.RSAUtil;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.List;

/**
 * @author dushitaoyuan
 * @desc 系统全局配置
 * @date 2019/8/29
 */
@Data
@ConfigurationProperties("application.config")
@Configuration
public class GlobalConfig implements InitializingBean {
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

    private String rsaP12Path;
    private String rsaP12Password;

    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Certificate certificate;

    private String publickKeyBase64;

    public String getConfig(String configKey) {
        return environment.getProperty(configKey);
    }

    /**
     * 限流实现类
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    @Autowired
    public AbstractRateLimiter rateLimiter(StringRedisTemplate redisTemplate) {
        RedisRateLimiter redisRateLimiter = new RedisRateLimiter(redisTemplate);
        return redisRateLimiter;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        //证书初始化
        KeyStore keyStore = RSAUtil.getKeyStore(rsaP12Path, rsaP12Password);
        publicKey = RSAUtil.getPublicKey(keyStore);
        privateKey = RSAUtil.getPrivateKey(keyStore, rsaP12Password);
        certificate = RSAUtil.getCertificate(keyStore);
        publickKeyBase64 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCejQE9rKg8v5TuaS9tODR9QNPg\n" +
                "P9MDhtyIFmKLmQTu2an/pEW18XWXEKi04ORYfwr7BjAJ9HMWspGRg8rbSjbethud\n" +
                "QnJUDtrqHNDYzJ1HhQT7sngGvpuH9ME7ZW4yZcMDS7/i5tQSps31JmGI+ULZg1cv\n" +
                "G5A/SbSqiE5PXSfIhQIDAQAB";
                Base64.encodeBase64String(certificate.getEncoded());
    }
}
