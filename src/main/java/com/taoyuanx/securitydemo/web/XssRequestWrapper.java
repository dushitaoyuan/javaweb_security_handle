package com.taoyuanx.securitydemo.web;

import com.taoyuanx.securitydemo.utils.RequestParamFilterUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * xss request包装类
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {
    public XssRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 对数组参数进行特殊字符过滤
     */
    @Override
    public String[] getParameterValues(String name) {
        return RequestParamFilterUtil.xssFilter(super.getParameterValues(name));
    }

    @Override
    public String getParameter(String name) {
        return RequestParamFilterUtil.xssFilter(super.getParameter(name));
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String[]> newParameterMap = new HashMap<>(parameterMap.size());
        for (String key : parameterMap.keySet()) {
            newParameterMap.put(key, RequestParamFilterUtil.xssFilter(parameterMap.get(key)));
        }
        return newParameterMap;
    }


}
