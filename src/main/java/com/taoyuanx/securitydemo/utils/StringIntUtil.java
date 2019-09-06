package com.taoyuanx.securitydemo.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author dushitaoyuan
 * @desc 长字符转long
 * @date 2019/9/6
 */
public class StringIntUtil {

    public static int toInt(String str) {
        return Math.abs(digest(str).hashCode());
    }

    private static String digest(String str) {
        return Hex.encodeHexString(DigestUtils.getMd5Digest().digest(str.getBytes()));
    }


}
