package com.taoyuanx.securitydemo.vo;

import lombok.Data;

/**
 * @author dushitaoyuan
 * @desc 加密请求表单
 * @date 2020/1/15
 */
@Data
public class EncryptForm {
    //服务端随机数
    private String serverRandom;
    //服务端公钥加密后的aes对称密钥
    private String clientAesEncodePassword;
    //加密后的数据
    private String encodeData;
    //数据hash
    private String dataHash;

}
