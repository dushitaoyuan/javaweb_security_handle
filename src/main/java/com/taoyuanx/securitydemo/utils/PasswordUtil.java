package com.taoyuanx.securitydemo.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author dushitaoyuan
 * @desc 密码工具类
 * @date 2019/8/30
 */
public class PasswordUtil {
    public static String passwordEncode(String passwordHash) {
        /**
         * 处理密码hash
         */
        return passwordHash;
    }

    /**
     * 明文处理
     *
     * @param passwordPlain
     * @return
     */
    public static String passwordHanlePlain(String passwordPlain) {
        /**
         * 前后端约定好hash算法,涉及密码相关,hash传送
         */
        return Base64.encodeBase64String(DigestUtils.getSha256Digest().digest(passwordPlain.getBytes()));
    }

    public static boolean isPasswordEqual(String passwordHash, String dbPasswordHash) {
        if (passwordHash != null) {
            return false;
        }
        return passwordHash.equals(dbPasswordHash);
    }


}
