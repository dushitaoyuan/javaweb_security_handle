package com.taoyuanx.securitydemo.security.lock;

import com.taoyuanx.securitydemo.security.ratelimit.AbstractRateLimiter;
import com.taoyuanx.securitydemo.utils.SpringContextUtil;
import com.taoyuanx.securitydemo.utils.StringIntUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author dushitaoyuan
 * @desc redis 限流实现
 * 复制 spring-cloud-gateway,可与spring-boot,spring-cloud等分离使用
 * @date 2019/9/5
 */
@Slf4j
public class RedisLock {

    @Getter
    private String lockKey;
    @Getter
    private String lockValue;


    public RedisLock(String lockKey) {
        this(lockKey, UUID.randomUUID().toString());
    }

    public RedisLock(String lockKey, String lockValue) {
        init();
        this.lockKey = lockKey;
        this.lockValue = lockValue;
    }


    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        boolean lock = doTryLock();
        if (!lock) {
            unit.sleep(time);
        }
        return lock;
    }

    public boolean tryLock() {
        return doTryLock();
    }


    public void unlock() {
        String result = redisTemplate.execute(unLockScript, Arrays.asList(lockKey), lockValue);
        if (FAILED.equals(result)) {
            log.warn("释放锁失败,lock_value 不匹配 my_lock_value:[{}]", lockValue);
        }
    }

    private boolean doTryLock() {
        String result = redisTemplate.execute(lockScript, Arrays.asList(lockKey), lockValue, String.valueOf(LOCK_MAX_EXPIRE_SECONDS));
        if (SUCCESSS.equals(result)) {
            return true;
        }
        return false;
    }

    private static StringRedisTemplate redisTemplate;
    private static RedisScript<String> lockScript;
    private static RedisScript<String> unLockScript;
    private static String SUCCESSS = "1", FAILED = "0";
    /**
     * 最大锁定5分钟
     */
    private static Long LOCK_MAX_EXPIRE_SECONDS = 300L;

    private void init() {
        if (Objects.isNull(lockScript)) {
            DefaultRedisScript script = new DefaultRedisScript();
            script.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/redis_lock.lua")));
            script.setResultType(String.class);
            lockScript = script;
        }
        if (Objects.isNull(unLockScript)) {
            DefaultRedisScript script = new DefaultRedisScript();
            script.setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/redis_unlock.lua")));
            script.setResultType(String.class);
            unLockScript = script;
        }
        if (Objects.isNull(redisTemplate)) {
            redisTemplate = SpringContextUtil.getBean(StringRedisTemplate.class);
        }
    }
}
