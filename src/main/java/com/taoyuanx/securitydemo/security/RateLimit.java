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
     * 限流并发 和限流的key,类型
     */
    double limit() default 100;

    String key() default "" ;

    RateLimitType type() default RateLimitType.METHOD;
}