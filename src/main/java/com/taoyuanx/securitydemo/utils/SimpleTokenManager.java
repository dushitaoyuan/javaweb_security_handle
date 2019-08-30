package com.taoyuanx.securitydemo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taoyuanx.securitydemo.exception.UnAuthException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.HmacUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 都市桃源
 * time:2018 下午4:25:55
 * usefor: hmac token算法 单向 简单实现
 */
public class SimpleTokenManager {
    private static final String END_KEY = "end";

    public static String createToken(String hmacKey, Map<String, Object> data, Long expire, TimeUnit timeUnit) {
        Long end = System.currentTimeMillis() + timeUnit.toMillis(expire);
        if (data == null || data.isEmpty()) {
            data = new HashMap<>(1);
        }
        data.put(END_KEY, end);
        byte[] signData = JSON.toJSONBytes(data);
        String token = Base64.encodeBase64URLSafeString(HmacUtils.getHmacMd5(hmacKey.getBytes()).doFinal(signData));
        token += "." + Base64.encodeBase64URLSafeString(signData);
        return token;
    }

    public static Map<String, Object> vafy(String hmacKey, String token) throws SecurityException {
        try {
            if (null == token || "".equals(token)) {
                return null;
            }
            String[] split = token.split("\\.");
            if (split.length > 2) {
                throw new UnAuthException("token格式非法");
            }
            byte[] signData = Base64.decodeBase64(split[1].getBytes());
            JSONObject signObj = JSON.parseObject(StringUtils.newString(signData, "UTF-8"));
            Long end = signObj.getLong(END_KEY);
            if (end < System.currentTimeMillis()) {
                throw new UnAuthException("token过期");
            }
            String tokenC = Base64.encodeBase64URLSafeString(HmacUtils.getHmacMd5(hmacKey.getBytes()).doFinal(signData));
            if (tokenC.equals(split[0])) {
                return signObj;
            }
            throw new UnAuthException("token校验失败");
        } catch (Exception e) {
            if (e instanceof UnAuthException) {
                throw e;
            } else {
                throw new UnAuthException("token校验失败");
            }
        }
    }


}
