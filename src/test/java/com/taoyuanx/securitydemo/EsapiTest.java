package com.taoyuanx.securitydemo;

import org.junit.Test;
import org.owasp.esapi.Authenticator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.AuthenticationException;
import org.owasp.esapi.errors.ValidationException;

import java.util.Set;

/**
 * @author dushitaoyuan
 * @desc esapi 测试
 * @date 2019/9/29
 * <p>
 * esapi开源地址:https://github.com/ESAPI/esapi-java-legacy 可惜文档较少,不过代码不难理解
 * <p>
 * api较多,可以参考example使用
 */
public class EsapiTest {
    @Test
    public void userTest() throws AuthenticationException {
        Authenticator instance = ESAPI.authenticator();
        String password = instance.generateStrongPassword();
        String u = "uuu1";
        User user = null;
        if (!instance.exists(u)) {
            user = instance.createUser(u, password, password);
        }
        user = instance.getUser(u);
        user.addRole("role1");
        Set<String> roles = user.getRoles();
    }

    @Test
    public void scriptTest() throws AuthenticationException {
        /**
         * 转义javascript
         */
        String safe = ESAPI.encoder().encodeForJavaScript("<script>alert(1111)</script>");
        System.out.println(safe);

        safe = ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.STANDARD), "'1' or 1");
        System.out.println(safe);
        /**
         * 转义sql
         */
        safe = ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.STANDARD), "'1' or 1");
        System.out.println(safe);

    }

    /**
     * 参数校验
     * @throws AuthenticationException
     * @throws ValidationException
     */
    @Test
    public void validationTest() throws AuthenticationException, ValidationException {
        System.out.println(ESAPI.validator().isValidInput(
                "email", "12345", "Email",
                200, false));
        System.out.println(ESAPI.validator().isValidInput(
                "email", "192.168.10.1", "IPAddress",
                200, false));


    }
}
