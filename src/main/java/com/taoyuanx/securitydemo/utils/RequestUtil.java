package com.taoyuanx.securitydemo.utils;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dushitaoyuan
 * @desc 请求工具类
 * @date 2019/8/27
 */
public class RequestUtil {
    /**
     * 获取请求ip
     *
     * @return
     */
    public static String getRemoteIp() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.
                        getRequestAttributes()).getRequest();
        return request.getRemoteHost();
    }


    /**
     * 判断是否为本站请求
     *
     * @param referer
     * @param systemDomain
     * @return
     */
    public static boolean isLoalSiteRequest(String referer, String systemDomain) {
        if (StringUtils.isEmpty(systemDomain)) {
            return true;
        }
        if (StringUtils.isEmpty(referer)) {
            return false;
        }
        return referer.contains(systemDomain);
    }


}
