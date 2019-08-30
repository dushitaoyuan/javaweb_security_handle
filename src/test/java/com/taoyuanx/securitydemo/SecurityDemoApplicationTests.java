package com.taoyuanx.securitydemo;

import com.taoyuanx.securitydemo.controller.BussinessController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecurityDemoApplicationTests {
    @Autowired
    BussinessController bussinessController;
    @Test
    public void contextLoads() {
        bussinessController.admin();
    }

}
