package com.taoyuanx.securitydemo.security.ratelimit;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;

import java.util.Map;

/**
 * @author dushitaoyuan
 * @desc guava限流实现
 * @date 2019/9/5
 */
public class GuavaRateLimiter extends AbstractRateLimiter {
    private Map<String, RateLimiter> rateHolder = Maps.newConcurrentMap();
    private static final int MAX_HOLDER_SIZE = 50000;
 /*   @Override
    public boolean tryAcquire(String key, Double limit) {
        return doTryAcquire(1, key, limit);
    }

    @Override
    public boolean tryAcquire(int permits, String key, Double limit) {
        return doTryAcquire(permits, key, limit);
    }
*/
    protected boolean doTryAcquire(int permits, String key, Double limit) {
        //超过固定阈值,清空,重构
        if (rateHolder.size() > MAX_HOLDER_SIZE) {
            rateHolder.clear();
        }
        RateLimiter rateLimiter = null;
        if (rateHolder.containsKey(key)) {
            rateLimiter = rateHolder.get(key);
        }
        rateLimiter = RateLimiter.create(limit);
        rateHolder.putIfAbsent(key, rateLimiter);
        return rateLimiter.tryAcquire(permits);
    }


}
