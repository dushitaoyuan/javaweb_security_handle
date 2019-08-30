package com.taoyuanx.securitydemo.web;

import com.taoyuanx.securitydemo.exception.UnAuthException;
import com.taoyuanx.securitydemo.utils.RequestParamFilterUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Map;

/**
 * xss过滤器,
 * 注意测试,不要拦截所有请求,有风险再拦截,容易误杀正常请求
 */
@WebFilter(urlPatterns = "/api/*")
public class SqlFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, IOException {
        Map<String, String[]> parameterMap = servletRequest.getParameterMap();
        if (parameterMap != null) {
            for (String key : parameterMap.keySet()) {
                if (RequestParamFilterUtil.isSqlInject(parameterMap.get(key))) {
                    throw new UnAuthException("权限异常");
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
