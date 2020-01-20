package com.taoyuanx.securitydemo;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.taoyuanx.securitydemo.utils.AesUtil;
import com.taoyuanx.securitydemo.utils.HashUtil;
import com.taoyuanx.securitydemo.utils.RSAUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author dushitaoyuan
 * @date 2020/1/15
 */
public class EncodeTest {
    @Test
    public void rsaTest() throws Exception {


        KeyStore keyStore = RSAUtil.getKeyStore("d://client.p12", "123456");
        RSAPublicKey publicKey = RSAUtil.getPublicKey(keyStore);
        RSAUtil.getPrivateKey(keyStore, "123456");


    }

    @Test
    public void rsaDecodeFileTest() throws Exception {

        KeyStore keyStore = RSAUtil.getKeyStore("d://client.p12", "123456");
        RSAPublicKey publicKey = RSAUtil.getPublicKey(keyStore);
        RSAPrivateKey privateKey = RSAUtil.getPrivateKey(keyStore, "123456");
        String srcFile = "d://demo.pdf", encodeFile = "d://demo_encode.pdf", decodeFile = "d://demo_decode.pdf";
        String fileHash = HashUtil.hash(new FileInputStream(srcFile), HashUtil.MD5, HashUtil.HEX);
        RSAUtil.encryptByPublicKey(new FileInputStream(srcFile), publicKey, new FileOutputStream(encodeFile));
        RSAUtil.decryptByPrivateKey(new FileInputStream(encodeFile), privateKey, new FileOutputStream(decodeFile));

        String fileEncodeHash = HashUtil.hash(new FileInputStream(decodeFile), HashUtil.MD5, HashUtil.HEX);
        System.out.println(fileHash.equals(fileEncodeHash));

    }

    @Test
    public void rsaFileTest() throws Exception {

        KeyStore keyStore = RSAUtil.getKeyStore("d://client.p12", "123456");
        RSAPublicKey publicKey = RSAUtil.getPublicKey(keyStore);
        RSAPrivateKey privateKey = RSAUtil.getPrivateKey(keyStore, "123456");
        String srcFile = "d://demo.pdf", encodeFile = "d://demo_encode.pdf", decodeFile = "d://demo_decode.pdf";
        String fileHash = HashUtil.hash(new FileInputStream(srcFile), HashUtil.MD5, HashUtil.HEX);
        byte[] bytes = RSAUtil.encryptByPublicKey(FileUtils.readFileToByteArray(new File(srcFile)), publicKey);
        FileUtils.writeByteArrayToFile(new File(encodeFile), bytes);
        bytes = RSAUtil.decryptByPrivateKey(bytes, privateKey);
        FileUtils.writeByteArrayToFile(new File(decodeFile), bytes);
        String fileEncodeHash = HashUtil.hash(new FileInputStream(decodeFile), HashUtil.MD5, HashUtil.HEX);
        System.out.println(fileHash.equals(fileEncodeHash));

    }

    @Test
    public void aesFileTest() throws Exception {
        String srcFile = "d://demo.pdf", encodeFile = "d://demo_encode.pdf", decodeFile = "d://demo_decode.pdf";
        String password="1234567812345678";
        String fileHash = HashUtil.hash(new FileInputStream(srcFile), HashUtil.MD5, HashUtil.HEX);
        AesUtil.encrypt(new FileInputStream(srcFile), password, new FileOutputStream(encodeFile));
        AesUtil.decrypt(new FileInputStream(encodeFile), password, new FileOutputStream(decodeFile));

        String fileEncodeHash = HashUtil.hash(new FileInputStream(decodeFile), HashUtil.MD5, HashUtil.HEX);
        System.out.println(fileHash.equals(fileEncodeHash));

    }
}
