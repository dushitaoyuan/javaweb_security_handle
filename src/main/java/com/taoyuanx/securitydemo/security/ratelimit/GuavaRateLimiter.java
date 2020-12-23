package com.taoyuanx.securitydemo.security.ratelimit;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.common.util.concurrent.RateLimiter;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author dushitaoyuan
 * @desc guava限流实现
 * @date 2019/9/5
 */
public class GuavaRateLimiter extends AbstractRateLimiter {
    private static final int MAX_HOLDER_SIZE = 50000;
    private Map<String, RateLimiter> rateHolder = new ConcurrentHashMap<>(MAX_HOLDER_SIZE);

    private Map<String, LongAdder> countHolder = new ConcurrentHashMap();
    /**
     * 总数限流到0后,标记,会有些许误判,不在乎内存的话,可用hashset存
     */
    private BloomFilter<CharSequence> TOTAL_LIMIT_ZERO_FLAG = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), MAX_HOLDER_SIZE * 20, 0.001);

    @Override
    public boolean doTryAcquire(int permits, String key, Double limit) {
        //超过固定阈值,清空,重构
        if (rateHolder.size() > MAX_HOLDER_SIZE) {
            rateHolder.clear();
        }
        RateLimiter rateLimiter = null;
        if (!rateHolder.containsKey(key)) {
            rateHolder.putIfAbsent(key, RateLimiter.create(limit));
        }
        rateLimiter = rateHolder.get(key);
        return rateLimiter.tryAcquire(permits);
    }

    @Override
    public boolean tryCount(int count, String key, Long totalCount) {
        if (count > totalCount) {
            return false;
        }
        //标记后,直接返回false
        if (TOTAL_LIMIT_ZERO_FLAG.mightContain(key)) {
            return false;
        }
        //超过固定阈值,清空,重构 防止内存溢出
        if (countHolder.size() > MAX_HOLDER_SIZE) {
            countHolder.clear();
        }
        LongAdder longAdder = null;
        if (!countHolder.containsKey(key)) {
            countHolder.putIfAbsent(key, new LongAdder());
        }
        longAdder = countHolder.get(key);
        if (longAdder.longValue() >= totalCount) {
            TOTAL_LIMIT_ZERO_FLAG.put(key);
            countHolder.remove(key);
            return false;
        }
        longAdder.add(count);
        return true;
    }


}
