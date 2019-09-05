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
     * key 限流的key
     * type 限流类型 参见:com.taoyuanx.securitydemo.security.RateLimitType
     */
    double limit() default 100;

    String key() default "" ;

    RateLimitType type() default RateLimitType.METHOD;
}