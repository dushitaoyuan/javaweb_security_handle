package com.taoyuanx.securitydemo.security;

import java.lang.annotation.*;

/**
 * 限流注解
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    /**
     * limit 每秒并发
     * limitKey 限流的key 只有 type=RateLimitType.SERVICE_KEY 才支持el表达式
     * type 限流类型 参见:com.taoyuanx.securitydemo.security.RateLimitType
     * totalCount 次数限流
     */
    double limit() default 100;

    String limitKey() default "";

    RateLimitType type() default RateLimitType.METHOD;

    long totalCount() default 0;
}