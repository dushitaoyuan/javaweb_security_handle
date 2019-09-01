package com.taoyuanx.securitydemo.security;


import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.taoyuanx.securitydemo.exception.LimitException;
import com.taoyuanx.securitydemo.utils.RequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * aop限流
 */
@Aspect
@Component
public class RateLimitAspect {
    private static final Logger LOG = LoggerFactory.getLogger(RateLimitAspect.class);

    private Map<String, RateLimiter> rateHolder = Maps.newConcurrentMap();
    private static final int MAX_HOLDER_SIZE = 50000;

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
            if (rate != null) {
                RateLimit[] rateLimitArray = rate.rate();
                if (rateLimitArray != null) {
                    for (RateLimit limit : rateLimitArray) {
                        handleRateLimit(limit, methodName);
                    }
                }
            }

        }
        return joinPoint.proceed();
    }


    private void handleRateLimit(RateLimit rateLimit, String methodName) throws Throwable {
        RateLimiter rateLimiter = doGetRateLimiter(rateLimit, methodName);
        if (!rateLimiter.tryAcquire()) {
            throw new LimitException("请求过于频繁,请稍后再试");
        }
    }

    private RateLimiter doGetRateLimiter(RateLimit rateLimit, String methodName) {
        RateLimitType type = rateLimit.type();
        String key = rateLimit.key();
        if (type == null) {
            key = methodName;
            if (LOG.isDebugEnabled()) {
                LOG.debug("限流策略未定义,采用[]限流策略", methodName, RateLimitType.METHOD);
            }
        } else {
            switch (type) {
                case IP:
                    String serviceKey = rateLimit.key();
                    if (serviceKey == null || key.isEmpty()) {
                        key = RequestUtil.getRemoteIp() + "_" + methodName;
                    } else {
                        key = RequestUtil.getRemoteIp() + "_" + serviceKey;
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("采用[{}]限流策略,限流key:{}", RateLimitType.IP, key);
                    }
                    break;
                case METHOD:
                    key = methodName;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("采用[{}]限流策略,限流key:{}", RateLimitType.METHOD, key);
                    }
                    break;
                case SERVICE_KEY:
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("采用[{}]限流策略,限流key:{}", RateLimitType.SERVICE_KEY, key);
                    }
                    break;
                case GLOBAL:
                    key = "global";
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("采用[{}]限流策略,限流key:{}", RateLimitType.GLOBAL, key);
                    }
                    break;
            }
        }
        //超过固定阈值,清空,重构
        if (rateHolder.size() > MAX_HOLDER_SIZE) {
            rateHolder.clear();
        }
        if (rateHolder.containsKey(key)) {
            return rateHolder.get(key);
        }
        RateLimiter rateLimiter = RateLimiter.create(rateLimit.limit());
        rateHolder.putIfAbsent(key, rateLimiter);
        return rateHolder.get(key);

    }


}