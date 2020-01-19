package com.taoyuanx.securitydemo.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;

public final class RSAUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RSAUtil.class);
    public static final String KEYSTORE_TYPE_P12 = "PKCS12";
    public static final String KEYSTORE_TYPE_JKS = "JKS";
    public static final String ENCRYPT_TYPE_RSA = "RSA";
    public static final String DEFAILT_SIGN_ALGORITHM = "MD5withRSA";
    public static final String CERT_TYPE_X509 = "X.509";

    public static KeyStore getKeyStore(String filePath, String keyPassword) throws Exception {
        return getKeyStore(HelperUtil.getInputStream(filePath), keyPassword, guessKeyStoreType(filePath));
    }

    public static KeyStore getKeyStore(InputStream inputStream, String keyPassword, String keyStoreType) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(inputStream, keyPassword.toCharArray());
        return keyStore;
    }

    public static String guessKeyStoreType(String filePath) {
        String ext = HelperUtil.getExtension(filePath);
        if (ext.equals("p12") || ext.equals("pfx")) {
            return KEYSTORE_TYPE_P12;
        }
        if (ext.equals("jks")) {
            return KEYSTORE_TYPE_JKS;
        }
        return null;
    }

    public static RSAPublicKey getPublicKey(KeyStore keyStore) throws Exception {
        String key_aliases = null;
        Enumeration<String> enumeration = keyStore.aliases();
        if (enumeration.hasMoreElements()) {
            key_aliases = enumeration.nextElement();
            RSAPublicKey publicKey = (RSAPublicKey) keyStore.getCertificate(key_aliases).getPublicKey();
            return publicKey;
        }
        return null;
    }

    public static Certificate getCertificate(KeyStore keyStore) throws Exception {
        String key_aliases = null;
        Enumeration<String> enumeration = keyStore.aliases();
        key_aliases = enumeration.nextElement();
        if (keyStore.isKeyEntry(key_aliases)) {
            Certificate certificate = keyStore.getCertificate(key_aliases);
            return certificate;
        }
        return null;
    }

    public static RSAPrivateKey getPrivateKey(KeyStore keyStore, String keyPassword) throws Exception {
        String key_aliases = null;
        Enumeration<String> enumeration = keyStore.aliases();
        key_aliases = enumeration.nextElement();
        if (keyStore.isKeyEntry(key_aliases)) {
            RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey(key_aliases, keyPassword.toCharArray());
            return privateKey;
        }
        return null;
    }


    public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
        return Base64.encodeBase64URLSafeString(encryptByPublicKey(Base64.decodeBase64(data), publicKey));
    }

    public static byte[] encryptByPublicKey(byte[] data, RSAPublicKey publicKey) throws Exception {
        if (null == publicKey) {// 如果公钥为空，采用系统公钥
            throw new Exception("rsa publicKey is null");
        }
        Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 模长
        int key_len = publicKey.getModulus().bitLength() / 8;
        // 加密数据长度 <= 模长-11,如果明文长度大于模长-11则要分组加密
        key_len -= 11;
        byte[] dataReturn = null;
        for (int i = 0; i < data.length; i += key_len) {
            byte[] doFinal = cipher.doFinal(HelperUtil.subarray(data, i, i + key_len));
            dataReturn = HelperUtil.addAll(dataReturn, doFinal);
        }
        return dataReturn;
    }


    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
        return new String(decryptByPrivateKey(Base64.decodeBase64(data), privateKey));
    }


    public static byte[] decryptByPrivateKey(byte[] data, RSAPrivateKey privateKey) throws Exception {
        if (null == privateKey) {
            throw new Exception("rsa privateKey is null");
        }
        Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE_RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // 模长
        int key_len = privateKey.getModulus().bitLength() / 8;
        // 分组解密
        ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
        // 如果密文长度大于模长则要分组解密
        for (int i = 0; i < data.length; i += key_len) {
            byte[] doFinal = cipher.doFinal(HelperUtil.subarray(data, i, i + key_len));
            out.write(doFinal, 0, doFinal.length);
        }
        return out.toByteArray();
    }


    public static void encryptByPublicKey(InputStream data, RSAPublicKey publicKey, OutputStream encodeStream) throws Exception {
        if (null == publicKey) {
            throw new Exception("rsa publicKey is null");
        }
        Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        CipherInputStream cipherInputStream = new CipherInputStream(data, cipher);
        int buffSize = 4 << 20;
        byte[] buf = new byte[buffSize];
        int len = 0;
        while ((len = cipherInputStream.read(buf)) != -1) {
            encodeStream.write(buf, 0, len);
        }
        cipherInputStream.close();

    }

    public static void decryptByPrivateKey(InputStream data, RSAPrivateKey privateKey, OutputStream decodeStream) throws Exception {
        if (null == privateKey) {
            throw new Exception("rsa privateKey is null");
        }
        Cipher cipher = Cipher.getInstance(ENCRYPT_TYPE_RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        CipherOutputStream cipherOutputStream = new CipherOutputStream(decodeStream, cipher);
        int buffSize = 4 << 20;
        byte[] buf = new byte[buffSize];
        int len = 0;
        while ((len = data.read(buf)) != -1) {
            cipherOutputStream.write(buf, 0, len);
        }
        cipherOutputStream.close();

    }


    /**
     * 签名
     *
     * @param data          签名内容
     * @param signAlgorithm 签名
     * @param privateKey    签名私钥
     * @return 签名值
     */
    public static byte[] sign(byte[] data, String signAlgorithm, RSAPrivateKey privateKey) throws Exception {
        if (null == privateKey) {
            throw new Exception("rsa privateKey is null");
        }
        if (HelperUtil.isEmpty(signAlgorithm)) {
            signAlgorithm = DEFAILT_SIGN_ALGORITHM;
        }
        Signature signture = Signature.getInstance(signAlgorithm);
        signture.initSign(privateKey);
        signture.update(data);
        return signture.sign();
    }

    /**
     * @param data          签名原文内容
     * @param sign          签名值
     * @param publicKey     验签公钥
     * @param signAlgorithm 签名算法  MD5withRSA,SHA1withRSA,SHA256withRSA
     * @return
     */
    public static boolean vefySign(byte[] data, byte[] sign, String signAlgorithm, PublicKey publicKey) {
        try {
            if (null == sign || sign.length == 0 || null == data || data.length == 0) {
                return false;
            }
            if (null == publicKey) {
                throw new Exception("rsa publicKey  is null");
            }
            if (HelperUtil.isEmpty(signAlgorithm)) {
                signAlgorithm = DEFAILT_SIGN_ALGORITHM;
            }
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            LOG.error("验签异常{}", e);
            return false;
        }
    }


    public static String sign(String data, String signAlgorithm, RSAPrivateKey privateKey) throws Exception {
        return Base64.encodeBase64URLSafeString(sign(data.getBytes(), signAlgorithm, privateKey));
    }

    public static boolean vefySign(String data, String signvalue, X509Certificate cert) {
        try {
            if (HelperUtil.isEmpty(data) || HelperUtil.isEmpty(signvalue)) {
                return false;
            }
            if (null == cert) {
                throw new Exception("rsa X509Certificate  is null");
            }
            return vefySign(data.getBytes(), Base64.decodeBase64(signvalue), cert.getSigAlgName(), cert.getPublicKey());
        } catch (Exception e) {
            LOG.error("验签异常{}", e);
            return false;
        }
    }


    public static X509Certificate readPublicKeyCer(InputStream publicInput) throws Exception {
        CertificateFactory certificatefactory = CertificateFactory.getInstance(CERT_TYPE_X509);
        X509Certificate cert = (X509Certificate) certificatefactory.generateCertificate(publicInput);
        return cert;
    }

    public static PublicKey readPublicKey(File fileInputStream) {
        try {
            return RSAUtil.readPublicKeyCer(new FileInputStream(fileInputStream)).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("读取公钥失败", e);
        }

    }

    /**
     * 读取pem格式公钥
     *
     * @param certPemString pem字符串
     * @return
     */
    public static PublicKey readPublicKeyPEM(String certPemString) {
        try {
            return RSAUtil.readPublicKeyCer(new ByteArrayInputStream(certPemString.getBytes("UTF-8"))).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("读取公钥失败", e);
        }

    }


}
