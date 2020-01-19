package com.taoyuanx.securitydemo;

import com.taoyuanx.securitydemo.utils.HashUtil;
import com.taoyuanx.securitydemo.utils.HelperUtil;
import com.taoyuanx.securitydemo.utils.RSAUtil;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author dushitaoyuan
 * @date 2020/1/15
 */
public class CertTest {
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
}
