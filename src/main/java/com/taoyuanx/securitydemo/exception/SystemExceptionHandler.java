package com.taoyuanx.securitydemo.exception;

import com.alibaba.fastjson.JSONException;
import com.taoyuanx.securitydemo.common.Result;
import com.taoyuanx.securitydemo.common.ResultBuilder;
import com.taoyuanx.securitydemo.common.ResultCode;
import com.taoyuanx.securitydemo.utils.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 */
public class SystemExceptionHandler implements HandlerExceptionResolver {
    public static final Logger LOG = LoggerFactory.getLogger(SystemExceptionHandler.class);

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception e) {

        if (isJson(request)) {
            doHandleException(request, response, handler, e);
            return new ModelAndView();
        } else {
            //todo跳转到页面
            return new ModelAndView();

        }
    }


    public void doHandleException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                  Throwable e) {
        Result result = null;
        HttpStatus httpStatus = HttpStatus.OK;
        if (e instanceof ValidatorException) {//参数异常
            result = ResultBuilder.failed(ResultCode.PARAM_ERROR.code, e.getMessage());
        } else if (e instanceof ServiceException) {
            result = ResultBuilder.failed(ResultCode.BUSSINESS_ERROR.code, e.getMessage());
        } else if (e instanceof UnAuthException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            result = ResultBuilder.failed(ResultCode.UNAUTHORIZED.code, e.getMessage());
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            HttpMediaTypeNotSupportedException mediaEx = (HttpMediaTypeNotSupportedException) e;
            result = ResultBuilder.failed(ResultCode.UN_SUPPORT_MEDIATYPE.code, "不支持该媒体类型:" + mediaEx.getContentType());
        } else if (e instanceof RadioLimitException) {
            httpStatus = HttpStatus.TOO_MANY_REQUESTS;
            result = ResultBuilder.failed(ResultCode.TOO_MANY_REQUESTS.code, e.getMessage());
        } else if (e instanceof JSONException) {
            result = ResultBuilder.failed(ResultCode.PARAM_ERROR.code, "参数异常,json格式非法:" + e.getMessage());
        } else if (e instanceof ServletException) {
            result = ResultBuilder.failed(e.getMessage());
        } else if (e instanceof NoHandlerFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
            result = ResultBuilder.failed(ResultCode.NOT_FOUND.code, "接口 [" + ((NoHandlerFoundException) e).getRequestURL() + "] 不存在");
        } else {
            result = ResultBuilder.failed(ResultCode.INTERNAL_SERVER_ERROR.code, "接口 [" + request.getRequestURI() + "] 内部错误，请联系管理员");
            if (handler != null && handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                LOG.error("接口 [{}] 出现异常，方法：{}.{}，异常摘要：{}", request.getRequestURI(),
                        handlerMethod.getBean().getClass().getName(),
                        handlerMethod.getMethod().getName(),
                        e.getMessage());
            }
            LOG.error("系统未知异常,异常信息:", e);
        }
        ResponseUtil.responseJson(response, result, httpStatus);
    }


    public static boolean isJson(HttpServletRequest request) {
        String header = request.getHeader("Content-Type");
        String accept = request.getHeader("Accept");
        if ((header != null && header.contains("application/json")) || (accept != null && accept.contains("application/json"))) {
            return true;
        } else {
            return false;
        }


    }
}
