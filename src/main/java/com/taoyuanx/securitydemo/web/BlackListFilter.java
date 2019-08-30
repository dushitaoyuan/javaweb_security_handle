package com.taoyuanx.securitydemo.web;

import com.taoyuanx.securitydemo.exception.UnAuthException;
import com.taoyuanx.securitydemo.security.blacklist.BlackListIpCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 黑名单过滤器
 */
/*@WebFilter(urlPatterns = "/api/*")
@Order(Ordered.HIGHEST_PRECEDENCE)*/
public class BlackListFilter implements Filter {

    @Autowired
    BlackListIpCheck blackListIpCheck;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, IOException {
        String clientIp = servletRequest.getRemoteAddr();
        if (blackListIpCheck.ipInBlackList(clientIp)) {
            throw new UnAuthException("ip已在黑名单,不可访问");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
