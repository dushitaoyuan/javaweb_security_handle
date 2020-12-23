package com.taoyuanx.securitydemo.security;


import com.taoyuanx.securitydemo.exception.LimitException;
import com.taoyuanx.securitydemo.security.ratelimit.AbstractRateLimiter;
import com.taoyuanx.securitydemo.utils.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * aop限流
 */
@Aspect
@Component
public class RateLimitAspect {
    private static final Logger LOG = LoggerFactory.getLogger(RateLimitAspect.class);

    @Autowired
    AbstractRateLimiter rateLimiter;

    @Pointcut("execution(* com.taoyuanx.securitydemo.controller..*.*(..))&& (@annotation(RateLimit)||@annotation(Rate))")
    public void ratePointCut() {
    }

    @Around("ratePointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method currentMethod = methodSignature.getMethod();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = className + "." + methodSignature.getName();
        RateLimit rateLimit = AnnotationUtils.findAnnotation(currentMethod, RateLimit.class);
        if (rateLimit != null) {
            handleRateLimit(rateLimit, methodName);
        } else {
            Rate rate = AnnotationUtils.findAnnotation(currentMethod, Rate.class);
            if (rate != null && rate.rate() != null) {
                for (RateLimit limit : rate.rate()) {
                    handleRateLimit(limit, methodName);
                }

            }

        }
        return joinPoint.proceed();
    }


    private void handleRateLimit(RateLimit rateLimit, String methodName) throws Throwable {
        RateLimitType type = rateLimit.type();
        String key = rateLimit.key();
        if (type == null) {
            type = RateLimitType.METHOD;
        }
        switch (type) {
            case IP:
                if (key == null || key.isEmpty()) {
                    key = RequestUtil.getRemoteIp() + "_" + methodName;
                } else {
                    key = RequestUtil.getRemoteIp() + "_" + key;
                }
                break;
            case METHOD:
                key = methodName;
                break;
            case SERVICE_KEY:
                break;
            case GLOBAL:
                key = "global";
                break;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("采用[{}]限流策略,限流key:{}", type, key);
        }
        if (type.equals(RateLimitType.TOTAL_COUNT)) {
            if (!rateLimiter.tryCount(1, key, rateLimit.totalCount())) {
                throw new LimitException("访问次数已达最大限制:" + rateLimit.totalCount() + ",请稍后再试");
            }
        } else {
            if (!rateLimiter.tryAcquire(key, rateLimit.limit())) {
                throw new LimitException("请求过于频繁,请稍后再试");
            }
        }


    }


}
