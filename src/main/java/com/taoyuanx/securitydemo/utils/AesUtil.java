package com.taoyuanx.securitydemo.utils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author dushitaoyuan
 * @desc aes 加密
 * @date 2020/1/20
 */
public class AesUtil {
    private static final byte[] iv = "0000000000000000".getBytes();
    private static final String AES_MODE = "AES/CBC/PKCS5Padding";
    private static Integer cacheSize = 1024 * 1024;

    public static byte[] encrypt(byte[] data, String password) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] result = cipher.doFinal(data);
        return result;
    }


    public static byte[] decrypt(byte[] data, String password) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] result = cipher.doFinal(data);
        return result;
    }


    public static void encrypt(InputStream dataStream, String password, OutputStream encodeStream) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        CipherInputStream cipherInputStream = new CipherInputStream(dataStream, cipher);
        byte[] buf = new byte[cacheSize];
        int len = 0;
        while ((len = cipherInputStream.read(buf)) != -1) {
            encodeStream.write(buf, 0, len);
            encodeStream.flush();
        }
        encodeStream.close();
        cipherInputStream.close();
    }


    public static void decrypt(InputStream encodeStream, String password, OutputStream decodeStream) throws Exception {
        SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        CipherOutputStream cipherOutputStream = new CipherOutputStream(decodeStream, cipher);
        byte[] buf = new byte[cacheSize];
        int len = 0;
        while ((len = encodeStream.read(buf)) != -1) {
            cipherOutputStream.write(buf, 0, len);
            cipherOutputStream.flush();
        }
        cipherOutputStream.close();
        decodeStream.close();
        encodeStream.close();
    }
}
