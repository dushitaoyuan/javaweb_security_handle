package com.taoyuanx.securitydemo.utils;

/**
 * @author 都市桃源
 * 数据摘要工具类
 */

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

public class HashUtil {
    //hash字符类型 base64 和hex
    public static final String HEX = "hex";
    public static final String BASE64 = "base64";

    //hash算法类型 md5,sha256,sha1
    public static final String MD5 = "md5";
    public static final String SHA256 = "sha256";
    public static final String SHA1 = "sha1";

    public static byte[] hash(String content, String hashType) throws Exception {
        if (null == content || "".equals(content)) {
            throw new Exception("hash 内容为空");
        }
        return doHash(content.getBytes(), hashType);
    }


    public static byte[] hash(byte[] content, String hashType) throws Exception {
        if (null == content || "".equals(content)) {
            throw new Exception("hash 内容为空");
        }
        return doHash(content, hashType);
    }


    /**
     * @param content    hash内容
     * @param hashType   hash类型
     * @param stringType 编码类型 base64 和 hex
     * @return
     * @throws Exception
     */
    public static String hash(String content, String hashType, String stringType) throws Exception {
        if (null == content || "".equals(content)) {
            throw new Exception("hash 内容为空");
        }
        return bytes2Str(doHash(content.getBytes(), hashType), stringType);
    }

    public static String hash(byte[] content, String hashType, String stringType) throws Exception {
        if (null == content || content.length == 0) {
            throw new Exception("hash 内容为空");
        }

        return bytes2Str(doHash(content, hashType), stringType);
    }


    public static String hash(InputStream content, String hashType, String stringType) throws Exception {
        if (null == content) {
            throw new Exception("hash 内容为空");
        }

        return bytes2Str(doHash(content, hashType), stringType);
    }

    private static String bytes2Str(byte[] bytes, String stringType) {
        switch (stringType) {
            case HEX:
                return Hex.encodeHexString(bytes);
            case BASE64:
                return Base64.encodeBase64String(bytes);
            default:
                return Base64.encodeBase64String(bytes);
        }
    }

    public static String bytesToAscllString(byte[] bytes) {
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append((char) b);
        }
        return buf.toString();
    }

    private static byte[] doHash(byte[] content, String hashType) {
        switch (hashType) {
            case MD5:
                return DigestUtils.getMd5Digest().digest(content);
            case SHA256:
                return DigestUtils.getSha256Digest().digest(content);
            case SHA1:
                return DigestUtils.getSha1Digest().digest(content);
            default:
                return DigestUtils.getMd5Digest().digest(content);
        }
    }

    private static byte[] doHash(InputStream content, String hashType) throws IOException {
        byte[] buffer = new byte[1024 * 1024 * 4];
        MessageDigest digest = null;
        switch (hashType) {
            case MD5:
                digest = DigestUtils.getMd5Digest();
                break;
            case SHA256:
                digest = DigestUtils.getSha256Digest();
                break;
            case SHA1:
                digest = DigestUtils.getSha1Digest();
                break;
            default:
                digest = DigestUtils.getMd5Digest();
                break;
        }
        int len = -1;
        while ((len = content.read(buffer, 0, 1024)) != -1) {
            digest.update(buffer, 0, len);
        }
        content.close();
        return digest.digest();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(hash("1", MD5, HEX));
    }


}
