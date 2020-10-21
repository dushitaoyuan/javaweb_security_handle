package com.taoyuanx.securitydemo;

import com.taoyuanx.securitydemo.controller.BussinessController;
import com.taoyuanx.securitydemo.security.lock.RedisLock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisLockTest {
    @Test
    public void lockTestOne() throws InterruptedException {
        RedisLock redisLock = new RedisLock("lock:xxx","11");
        if (redisLock.tryLock()) {
            System.out.println("获取到锁了");
            redisLock.unlock();

        }
    }
    @Test
    public void lockTest() throws InterruptedException {
        RedisLock redisLock = new RedisLock("lock:xxx");
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    if (redisLock.tryLock(3,TimeUnit.SECONDS)) {
                        count++;
                        System.out.println("执行");
                        redisLock.unlock();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("未执行");
            });
        }
        System.out.println(count);
        executorService.shutdown();
        while (!executorService.awaitTermination(3, TimeUnit.SECONDS)){
            System.out.println("休眠3秒");
        }
        System.out.println("结束");
    }

    private ExecutorService executorService;
    private Integer count = 0;

    @Before
    public void before() throws Exception {
        executorService = Executors.newFixedThreadPool(10);
    }


}
