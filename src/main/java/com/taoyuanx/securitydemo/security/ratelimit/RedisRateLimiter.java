package com.taoyuanx.securitydemo.security.ratelimit;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private StringRedisTemplate redisTemplate;
    private RedisScript<List<Long>> tokenScript;
    private RedisScript<Long> countScript;

    public RedisRateLimiter(StringRedisTemplate redisTemplate) {
        DefaultRedisScript script = new DefaultRedisScript();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/rate_limiter_token.lua")));
        script.setResultType(List.class);
        this.tokenScript = script;

        script = new DefaultRedisScript();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/rate_limiter_count.lua")));
        script.setResultType(Long.class);
        this.countScript = script;

        this.redisTemplate = redisTemplate;
    }

    @Override
    protected boolean doTryAcquire(int permits, String key, Double limit) {
        String[] scriptArgs = {limit.longValue() + "", limit.longValue() + "", Instant.now().getEpochSecond() + "", permits + ""};
        List<Long> results = redisTemplate.execute(this.tokenScript, getKeys(key), scriptArgs);
        return results.get(0) == 1L;

    }

    @Override
    public boolean tryCount(int count, String key, Long totalCount) {
        String[] scriptArgs = {count + "", totalCount + ""};
        Long result = redisTemplate.execute(this.countScript, Arrays.asList(key), scriptArgs);
        return result == 1L;
    }

    private List<String> getKeys(String key) {
        int keyId = key.hashCode();
        String prefix = "request_rate_limiter.{" + keyId;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }


}
