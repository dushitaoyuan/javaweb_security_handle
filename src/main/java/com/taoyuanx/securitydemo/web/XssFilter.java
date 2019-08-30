package com.taoyuanx.securitydemo.web;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * xss过滤器
 */
@WebFilter(urlPatterns = "/api/*")
public class XssFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, IOException {
        //使用包装器
        XssRequestWrapper xssRequestWrapper = new XssRequestWrapper((HttpServletRequest) servletRequest);
        filterChain.doFilter(xssRequestWrapper, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
