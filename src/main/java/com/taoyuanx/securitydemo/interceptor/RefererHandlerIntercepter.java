package com.taoyuanx.securitydemo.interceptor;

import com.taoyuanx.securitydemo.exception.UnAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * referer校验拦截器
 */

public class RefererHandlerIntercepter implements HandlerInterceptor {
    Logger LOG = LoggerFactory.getLogger(RefererHandlerIntercepter.class);
    private List<String> refererCheckUrl;
    private List<String> allowDomains;

    public RefererHandlerIntercepter(List<String> refererCheckUrl, List<String> allowDomains) {
        this.refererCheckUrl = refererCheckUrl;
        this.allowDomains = allowDomains;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (refererCheckUrl == null) {
            return true;
        }
        String requestUrl = request.getRequestURI();
        for (String checkUrl : refererCheckUrl) {
            if (requestUrl.contains(checkUrl)) {
                String referer = request.getHeader("Referer");
                if (referer == null) {
                    LOG.debug("请求url[{}],未携带有效header:[Referer]", requestUrl);
                    throw new UnAuthException("操作非法");
                }
                if (!checkDomain(referer)) {
                    LOG.debug("请求url[{}],Referer->[{}]不在允许范围内", requestUrl, referer);
                    throw new UnAuthException("操作非法");
                }

                return false;
            }
        }
        return true;

    }

    private boolean checkDomain(String referer) {
        for (String domain : allowDomains) {
            if (referer.contains(domain)) {
                return true;
            }
        }
        return false;
    }


}
