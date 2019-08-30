package com.taoyuanx.securitydemo.security;

import java.lang.annotation.*;

/**
 * 限流复合注解
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Rate {
    RateLimit[] rate() default {};
}