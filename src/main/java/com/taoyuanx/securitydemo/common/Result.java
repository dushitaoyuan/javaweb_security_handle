package com.taoyuanx.securitydemo.common;


import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 * <p>
 * errorCode 错误码
 * msg 错误消息
 * success 请求成功标识 1成功,0失败
 * data 结果
 * ext 扩展信息
 */
@Data
public class Result implements Serializable {
    private Integer errorCode;
    private String msg;
    private Integer success;
    private Object data;
    private Object ext;

    public static Result build() {
        return new Result();
    }

    public Result buildCode(Integer code) {
        this.setErrorCode(code);
        return this;
    }

    public Result buildMsg(String msg) {
        this.setMsg(msg);
        return this;
    }

    public Result buildSuccess(Integer success) {
        this.setSuccess(success);
        return this;
    }

    public Result buildData(Object data) {
        this.data = data;
        return this;

    }

    public Result buildExt(Object ext) {
        this.ext = ext;
        return this;

    }
}
