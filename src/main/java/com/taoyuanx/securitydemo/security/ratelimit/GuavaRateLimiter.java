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
    private BloomFilter<CharSequence> TOTAL_LIMIT_ZERO_FLAG = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), MAX_HOLDER_SIZE * 20);

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

    @Override
    public boolean tryCount(int count, String key, Long totalCount) {
        //标记后,直接返回false
        if (TOTAL_LIMIT_ZERO_FLAG.mightContain(key)) {
            return false;
        }
        //超过固定阈值,清空,重构 防止内存溢出
        if (countHolder.size() > MAX_HOLDER_SIZE) {
            countHolder.clear();
        }
        LongAdder longAdder = null;
        if (countHolder.containsKey(key)) {
            longAdder = countHolder.get(key);
            longAdder.add(-count);
            //资源总数用完后,标记
            if (longAdder.longValue() <= 0) {
                TOTAL_LIMIT_ZERO_FLAG.put(key);
                countHolder.remove(key);
                return true;
            }
            return false;
        }
        if (count > totalCount) {
            return false;
        }
        longAdder = new LongAdder();
        countHolder.putIfAbsent(key, longAdder);
        countHolder.get(key).add(count);
        return true;
    }


}
