package com.taoyuanx.securitydemo.controller;

import cn.hutool.core.date.DateUtil;
import com.taoyuanx.securitydemo.common.Result;
import com.taoyuanx.securitydemo.common.ResultBuilder;
import com.taoyuanx.securitydemo.config.GlobalConfig;
import com.taoyuanx.securitydemo.constant.SystemConstants;
import com.taoyuanx.securitydemo.dto.AccountDTO;
import com.taoyuanx.securitydemo.exception.ServiceException;
import com.taoyuanx.securitydemo.helper.ToeknHelper;
import com.taoyuanx.securitydemo.security.*;
import com.taoyuanx.securitydemo.utils.CookieUtil;
import com.taoyuanx.securitydemo.utils.FileHandler;
import com.taoyuanx.securitydemo.utils.FileTypeCheckUtil;
import com.taoyuanx.securitydemo.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    GlobalConfig globalConfig;
    @Autowired
    FileHandler fileHandler;

    /**
     * 管理员访问
     *
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
     *
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
     *
     * @return
     */
    @GetMapping("public")
    @ResponseBody
    public String publicM() {
        return "hello anybody!";
    }

    /**
     * 必须携带Rerferer头部
     *
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
     *
     * @return
     */
    @GetMapping("rateLimit")
    @ResponseBody
    @Rate(rate = {@RateLimit(type = RateLimitType.IP, limit = 1),
            @RateLimit(type = RateLimitType.SERVICE_KEY, limit = 10, key = "api/rateLimit")})
    public String rateLimit() {
        return "hello rateLimit!";
    }

    /**
     * 限流: 每秒10次,
     *
     * @return
     */
    @GetMapping("rateLimit_key")
    @ResponseBody
    @RateLimit(type = RateLimitType.SERVICE_KEY, limit = 100, key = "api/rateLimit_key")
    public String rateLimitKey() {
        return "hello rateLimit!";
    }


    /**
     * 黑名单测试
     *
     * @return
     */
    @GetMapping("blackList")
    @ResponseBody
    public String blackList() {
        return "hello blackList!";
    }


    /**
     * 登录安全控制
     *
     * @return
     */
    @PostMapping("login")
    @ResponseBody
    public Result login(String userName, String password, HttpServletResponse response) throws Exception {
        /**
         * select from db
         */
        if (userName.equals("dushitaoyuan") && PasswordUtil.isPasswordEqual(password, PasswordUtil.passwordHanlePlain("123456"))) {
            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setAccountId(1L);
            accountDTO.setAccountStatus(Role.ADMIN.getAccountStatus());
            String token = toeknHelper.create(accountDTO);
            Cookie cookie = new Cookie(SystemConstants.TOKEN_COOKIE_KEY, token);
            cookie.setPath("/");
            cookie.setMaxAge(sessionTimeOut * 2);
            response.addCookie(cookie);
            return ResultBuilder.success();
        }
        throw new ServiceException("登陆异常");

    }



    @PostMapping("loginOut")
    @ResponseBody
    public void loginOut(HttpServletResponse response, HttpServletRequest request) throws Exception {
        CookieUtil.removeCookie(response, "/", SystemConstants.TOKEN_COOKIE_KEY);
        request.getSession().invalidate();
    }

    /**
     * 文件上传
     *
     * @return
     */
    @PostMapping("upload")
    @ResponseBody
    public Map<String, String> upload(@RequestParam("file") MultipartFile multipartFile, @CookieValue(name = SystemConstants.TOKEN_COOKIE_KEY) String token) throws Exception {
        toeknHelper.vafy(token);
        String ext = FileTypeCheckUtil.getType(multipartFile.getOriginalFilename());
        if (!FileTypeCheckUtil.allow(ext)) {
            throw new ServiceException("文件上传失败,类型不支持");
        }
        ext = FileTypeCheckUtil.getRealType(multipartFile.getInputStream());
        if (!FileTypeCheckUtil.allow(ext)) {
            throw new ServiceException("文件上传失败,类型不支持");
        }
        /**
         * 文件签名后返回前端,5分钟过期
         */
        String fileId = DateUtil.format(new Date(), "yyyy-mm-dd") + "/" + multipartFile.getOriginalFilename();
        File file = new File(globalConfig.getFileStorageDir(), fileId);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        multipartFile.transferTo(file);
        Map<String, String> result = new HashMap<>();
        result.put("path", fileId);
        result.put("url", fileHandler.signFileUrl(fileId, FileHandler.LOOK, 5L, TimeUnit.MINUTES));
        return result;
    }

    @GetMapping("file")
    public void upload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        fileHandler.handleFile(response, request);
    }
    @RateLimit(type = RateLimitType.TOTAL_COUNT,key = "rate_count",totalCount = 10)
    @GetMapping("rate_count")
    @ResponseBody
    public  Result rateCount() throws Exception {
        System.out.println("rate_count\t");
        return ResultBuilder.success("ok");
    }
}
