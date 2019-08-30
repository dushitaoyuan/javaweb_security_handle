package com.taoyuanx.securitydemo.controller;

import com.taoyuanx.securitydemo.exception.SystemExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author dushitaoyuan
 * @desc 用途描述
 * @date 2019/8/30
 **/

@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class SystemErrorController implements ErrorController {
    private static final String PATH = "/error";

    @Autowired
    ErrorAttributes errorAttributes;
    @Autowired
    SystemExceptionHandler systemExceptionHandler;

    @Override
    public String getErrorPath() {
        return PATH;
    }


    @RequestMapping
    public void doHandleError(HttpServletRequest request, HttpServletResponse response) {
        WebRequest webRequest = new ServletWebRequest(request, response);
        Throwable e = errorAttributes.getError(webRequest);
        systemExceptionHandler.doHandleException(request, response, null, e);
    }


}

