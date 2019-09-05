package com.taoyuanx.securitydemo.security.ratelimit;

/**
 * @author dushitaoyuan
 * @desc 抽象限流
 * @date 2019/9/5
 */
public abstract class AbstractRateLimiter {


    /**
     * 尝试获取令牌
     *
     * @param key   限流标识
     * @param limit 限流速率
     * @return
     */
    public boolean tryAcquire(String key, Double limit){
        return doTryAcquire(1,key,limit);
    }

    /**
     * 尝试获取令牌
     *
     * @param permits 获取令牌数量
     * @param key     限流标识
     * @param limit   限流速率
     * @return
     */
    public boolean tryAcquire(int permits, String key, Double limit){
        return doTryAcquire(permits,key,limit);
    }

    protected abstract boolean doTryAcquire(int permits, String key, Double limit);
}
