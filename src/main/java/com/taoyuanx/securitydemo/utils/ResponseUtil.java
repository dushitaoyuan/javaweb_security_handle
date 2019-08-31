package com.taoyuanx.securitydemo.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dushitaoyuan
 * @desc 自定义结果返回
 * @date 2019/8/30
 */
public class ResponseUtil {
    public static void responseJson(HttpServletResponse response, Object result, HttpStatus httpStatus) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(httpStatus.value());
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static void responseJson(HttpServletResponse response, Object result, Integer httpStatus) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(httpStatus);
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
