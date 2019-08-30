package com.taoyuanx.securitydemo.interceptor;

import com.taoyuanx.securitydemo.constant.SystemConstants;
import com.taoyuanx.securitydemo.dto.AccountDTO;
import com.taoyuanx.securitydemo.exception.UnAuthException;
import com.taoyuanx.securitydemo.security.RequireRole;
import com.taoyuanx.securitydemo.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 简单权限校验
 */
public class SimpleAuthHandlerIntercepter implements HandlerInterceptor {
    Logger LOG = LoggerFactory.getLogger(SimpleAuthHandlerIntercepter.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccountDTO account = getAccount(request);
            RequireRole requireRole = getAuthority(handlerMethod, RequireRole.class);
            Integer accountStatus = account == null ? null : account.getAccountStatus();
            if (Role.hasRole(requireRole, accountStatus)) {
                return true;
            }
            LOG.debug("请求url[{}],权限不足,已拦截", request.getRequestURI());
            throw new UnAuthException("操作");
        }
        return true;
    }

    private AccountDTO getAccount(HttpServletRequest request) {
        Object account = request.getSession().getAttribute(SystemConstants.ACCOUNT_SESSION_KEY);
        if (account == null) {
            return null;
        }
        return (AccountDTO) account;
    }

    private <T> T getAuthority(HandlerMethod handlerMethod, Class annotationType) {
        if (handlerMethod == null) {
            return null;
        }
        T methodAnno = (T) AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotationType);
        if (methodAnno == null) {
            return (T) AnnotationUtils.findAnnotation(handlerMethod.getBean().getClass(), annotationType);
        } else {
            return methodAnno;
        }
    }


}
