package com.taoyuanx.securitydemo.exception;

/**
 * 权限异常 http状态码401
 */
public class UnAuthException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public UnAuthException(String message) {
        super(message);
    }


}
