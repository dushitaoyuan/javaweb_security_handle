package com.taoyuanx.securitydemo.controller;

import cn.hutool.core.io.FileTypeUtil;
import com.taoyuanx.securitydemo.config.GlobalConfig;
import com.taoyuanx.securitydemo.constant.SystemConstants;
import com.taoyuanx.securitydemo.dto.AccountDTO;
import com.taoyuanx.securitydemo.exception.ServiceException;
import com.taoyuanx.securitydemo.helper.ToeknHelper;
import com.taoyuanx.securitydemo.security.*;
import com.taoyuanx.securitydemo.utils.FileTypeCheckUtil;
import com.taoyuanx.securitydemo.utils.PasswordUtil;
import com.taoyuanx.securitydemo.utils.SimpleTokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dushitaoyuan
 * @desc 业务api
 * @date 2019/8/29
 */
@Controller
@RequestMapping(value = "api")
public class BussinessController {
    @Autowired
    ToeknHelper toeknHelper;
    @Value("${server.servlet.session.timeout}")
    Integer sessionTimeOut;
    /**
     * 管理员访问
     * @return
     */
    @GetMapping("admin")
    @ResponseBody
    @RequireRole(role = {Role.ADMIN})
    public String admin() {
        return "hello admin!";
    }
    /**
     * 普通用户访问
     * @return
     */
    @GetMapping("commonUser")
    @ResponseBody
    @RequireRole(role = {Role.COMMONUSER})
    public String commonUser() {
        return "hello commonUser!";
    }

    /**
     * 公开访问
     * @return
     */
    @GetMapping("public")
    @ResponseBody
    public String publicM() {
        return "hello anybody!";
    }

    /**
     * 必须携带Rerferer头部
     * @return
     */
    @GetMapping("refererCheck")
    @ResponseBody
    public String refererCheck() {
        return "hello refererCheck!";
    }

    /**
     * 限流: 单个ip每秒一次,
     * 自定义key 每秒10次
     * @return
     */
    @GetMapping("rateLimit")
    @ResponseBody
    @Rate(rate = {@RateLimit(type = RateLimitType.IP,limit = 1),
            @RateLimit(type = RateLimitType.SERVICE_KEY,limit = 10,key = "api/rateLimit")})
    public String rateLimit() {
        return "hello rateLimit!";
    }

    /**
     * 限流: 每秒10次,
     * @return
     */
    @GetMapping("rateLimit_key")
    @ResponseBody
    @RateLimit(type = RateLimitType.SERVICE_KEY,limit = 2,key = "api/rateLimit_key")
    public String rateLimitKey() {
        return "hello rateLimit!";
    }


    /**
     * 黑名单测试
     * @return
     */
    @GetMapping("blackList")
    @ResponseBody
    public String blackList() {
        return "hello blackList!";
    }



    /**
     * 登录安全控制
     * @return
     */
    @PostMapping("login")
    @ResponseBody
    public String login(String userName, String password, HttpServletResponse response) throws Exception {
        /**
         * select from db
         */
        if(userName.equals("dushitaoyuan")&& PasswordUtil.isPasswordEqual(password,PasswordUtil.passwordHanlePlain("123456"))){
            AccountDTO accountDTO=new AccountDTO();
            accountDTO.setAccountId(1L);
            accountDTO.setAccountStatus(Role.ADMIN.getAccountStatus());
            String token = toeknHelper.create(accountDTO);
            Cookie cookie=new Cookie(SystemConstants.TOKEN_COOKIE_KEY,token);
            cookie.setPath("/");
            cookie.setMaxAge(sessionTimeOut*2);
            response.addCookie(cookie);
        }
        return "hello upload!";
    }

    /**
     * 黑名单测试
     * @return
     */
    @GetMapping("upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile multipartFile,@CookieValue(name=SystemConstants.TOKEN_COOKIE_KEY) String token) throws Exception {
        toeknHelper.vafy(token);
        String ext=FileTypeCheckUtil.getType(multipartFile.getOriginalFilename());
        if(!FileTypeCheckUtil.allow(ext)){
            throw new ServiceException("文件上传失败,类型不支持");
        }
        ext=FileTypeCheckUtil.getRealType(multipartFile.getInputStream());
        if(!FileTypeCheckUtil.allow(ext)){
            throw new ServiceException("文件上传失败,类型不支持");
        }
        return "hello upload!";
    }


}
