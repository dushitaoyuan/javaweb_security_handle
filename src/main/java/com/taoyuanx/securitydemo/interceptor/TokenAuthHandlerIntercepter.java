package com.taoyuanx.securitydemo.interceptor;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.taoyuanx.securitydemo.constant.SystemConstants;
import com.taoyuanx.securitydemo.exception.UnAuthException;
import com.taoyuanx.securitydemo.helper.ToeknHelper;
import com.taoyuanx.securitydemo.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class TokenAuthHandlerIntercepter implements HandlerInterceptor {
    @Autowired
    ToeknHelper toeknHelper;

    private List<String> publicUrl;

    public TokenAuthHandlerIntercepter(String publicUrl) {
        this.publicUrl = Lists.newArrayList();
        Splitter.on(",").split(publicUrl).forEach(url -> {
            this.publicUrl.add(url.trim());
        });
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            Long accountId = getAccountId(request);
            if (isPermit(accountId, request)) {
                return true;
            } else {
                throw new UnAuthException("未授权操作");
            }
        }
        return true;
    }

    public Long getAccountId(HttpServletRequest request) {
        Object accountIdObj = request.getSession().getAttribute(SystemConstants.ACCOUNT_SESSION_KEY);
        if (accountIdObj == null) {
            return null;
        }
        Long accountId = (Long) accountIdObj;
        return accountId;
    }

    public boolean isPermit(Long accountId, HttpServletRequest request) {
        /**
         * 校验逻辑:
         * 1.登录时,校验操作token中accountId是否等于session中id
         * 2.未登录,校验url是否在公开url中
         */
        boolean login = accountId != null;
        String url = request.getRequestURI();
        if (login) {
            if (null == accountId) {
                return false;
            }
            String token = CookieUtil.getCookie(request.getCookies(), SystemConstants.TOKEN_COOKIE_KEY);
            if (null == token || token.length() == 0) {
                return false;
            }
            Map<String, Object> tokenData = toeknHelper.vafy(token);
            Long tokenAccountId = toeknHelper.getAccountId(tokenData);
            if (!tokenAccountId.equals(accountId) ) {
                return false;
            }

            return true;
        }
        if (null != publicUrl && publicUrl.size() > 0) {
            for (String pUrl : publicUrl) {
                if (url.contains(pUrl)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }


}
