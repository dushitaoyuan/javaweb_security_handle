package com.taoyuanx.securitydemo.security.ratelimit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * @author dushitaoyuan
 * @desc redis 限流实现
 * 复制 spring-cloud-gateway,可与spring-boot,spring-cloud等分离使用
 * @date 2019/9/5
 */
public class RedisRateLimiter extends AbstractRateLimiter {
    private RedisTemplate stringRedisTemplate;
    private RedisScript<List<Long>> script;

    public RedisRateLimiter(RedisTemplate redisTemplate) {
        DefaultRedisScript script = new DefaultRedisScript();
        script.setScriptSource(new ResourceScriptSource(
                new ClassPathResource("META-INF/demo.lua")));
        script.setResultType(List.class);
        this.script = script;
        this.stringRedisTemplate = redisTemplate;
    }

    /* @Override
     public boolean tryAcquire(String key, Double limit) {
         return doTryAcquire(1, key, limit);
     }

     @Override
     public boolean tryAcquire(int permits, String key, Double limit) {
         return doTryAcquire(permits, key, limit);
     }*/
    @Override
    protected boolean doTryAcquire(int permits, String key, Double limit) {
        List<Object> scriptArgs = Arrays.asList(limit.longValue(), limit.longValue(), Instant.now().getEpochSecond(), permits);
        Object execute = stringRedisTemplate.execute(this.script, getKeys(key),
                scriptArgs);


        return execute == null;

    }

    private List<String> getKeys(String key) {

        int keyId = key.hashCode();
        String prefix = "request_rate_limiter.{" + keyId;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }


}
