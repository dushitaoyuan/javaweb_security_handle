package com.taoyuanx.securitydemo.exception;

/**
 * @author dushitaoyuan
 * @desc 限制访问异常 http状态码 429
 * @date 2019/8/26
 */
public class LimitException extends  RuntimeException {
    public LimitException(String message) {
        super(message);
    }
}
