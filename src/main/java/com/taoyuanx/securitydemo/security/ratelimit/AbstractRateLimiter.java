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
    public boolean tryAcquire(String key, Double limit) {
        return doTryAcquire(1, key, limit);
    }

    /**
     * @param permits 申请令牌数量
     * @param key     限流标识
     * @param limit   速率
     * @return
     */
    public abstract boolean doTryAcquire(int permits, String key, Double limit);

    /**
     *  增加资源访问次数 用户可自行持久化记录
     * @param count      (申请资源数量)
     * @param key        (资源key)
     * @param totalCount (资源总量)
     * @return
     */
    public abstract boolean tryCount(int count, String key, Long totalCount);



}
