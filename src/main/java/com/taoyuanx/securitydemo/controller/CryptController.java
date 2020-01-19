package com.taoyuanx.securitydemo.controller;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.taoyuanx.securitydemo.common.Result;
import com.taoyuanx.securitydemo.common.ResultBuilder;
import com.taoyuanx.securitydemo.config.GlobalConfig;
import com.taoyuanx.securitydemo.exception.ServiceException;

import com.taoyuanx.securitydemo.utils.*;
import com.taoyuanx.securitydemo.vo.EncryptForm;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPrivateKey;
import java.util.Objects;

/**
 * @author dushitaoyuan
 * @desc 加密控制器
 * @date 2020/1/15
 */
@Controller
@RequestMapping("crypt")
public class CryptController {
    @Autowired
    GlobalConfig globalConfig;

    /**
     * 服务端公钥获取
     */
    @GetMapping("serverPub")
    @ResponseBody
    public String serverPub() {
        return globalConfig.getPublickKeyBase64();
    }

    /**
     * 服务端公钥获取
     */
    @GetMapping("cryptPage")
    public String cryptPage(Model model, HttpServletRequest request) {
        model.addAttribute("serverPub", globalConfig.getPublickKeyBase64());
        String serverRandom = RandomCodeUtil.getRandCode(16);
        request.getSession().setAttribute("serverRandom", serverRandom);
        model.addAttribute("serverRandom", serverRandom);
        String iv=RandomCodeUtil.getRandCode(16);
        model.addAttribute("iv", iv);
        request.getSession().setAttribute("iv", iv);
        return "crypt";
    }

    /**
     * 接受加密请求
     */
    @PostMapping("reciveEncrypt")
    @ResponseBody
    public Result reciveEncrypt(EncryptForm encryptForm, HttpServletRequest request) throws Exception {
        Object serverRandom = request.getSession().getAttribute("serverRandom");
        if (!(Objects.nonNull(encryptForm.getServerRandom()) && encryptForm.getServerRandom().equals(serverRandom))) {
            throw new ServiceException("非法请求");
        }
        String dataHash = encryptForm.getDataHash();
        //解密aes对称密码
        String clientAesEncodePassword = RSAUtil.decryptByPrivateKey(encryptForm.getClientAesEncodePassword(), (RSAPrivateKey) globalConfig.getPrivateKey());
        byte[] clientAesIv = request.getSession().getAttribute("iv").toString().getBytes("UTF-8");
        String aesMode = "AES/CBC/PKCS5Padding";
        //解密数据
        String data = null;
        try {
            AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, clientAesEncodePassword.getBytes("UTF-8"), clientAesIv);
            byte[] bytes = Hex.decodeHex(encryptForm.getEncodeData().toCharArray());
            data = new String(aes.decrypt(bytes), "UTF-8");
        } catch (Exception e) {
            throw new ServiceException("解密失败");
        }
        String hash = HashUtil.hash(data, HashUtil.SHA256, HashUtil.HEX);
        if (!hash.equals(encryptForm.getDataHash())) {
            throw new ServiceException("数据hash不匹配");
        }
        return ResultBuilder.success("ok");
    }
}
