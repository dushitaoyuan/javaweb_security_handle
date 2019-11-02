package com.taoyuanx.securitydemo.utils;

import com.google.common.base.Splitter;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author dushitaoyuan
 * @desc 请求参数过滤
 * @date 2019/8/29
 */
public class RequestParamFilterUtil {
    /**
     * 处理xss
     *
     * @param values
     * @return
     */
    public static String[] xssFilter(String[] values) {
        if(values==null){
            return null;
        }
        String[] newValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = xssFilter(values[i]);
        }
        return newValues;
    }

    /**
     * 处理xss
     *
     * @param value
     * @return
     */
    public static String xssFilter(String value) {
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(value);
    }

    /**
     * sql注入风险检测
     */
    private static List<String> SQL_KEY_WORDS = Splitter.on(",").splitToList("'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|; |or|-|+|,");
    public static boolean isSqlInject(String... params) {
        if (null == params) {
            return false;
        }
        for (String param : params) {
            for (int i = 0, len = SQL_KEY_WORDS.size(); i < len; i++) {
                if (param.toLowerCase().contains(" " + SQL_KEY_WORDS.get(i) + " ")) {
                    return true;
                }
            }
        }

        return false;
    }

}
