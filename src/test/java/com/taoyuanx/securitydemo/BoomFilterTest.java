package com.taoyuanx.securitydemo;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;

/**
 * @author dushitaoyuan
 * @desc 布隆过滤测试
 * @date 2019/8/30
 */
public class BoomFilterTest {

    @Test
    public void failedCountTest() {
        int batch = 10000000;
        BloomFilter bloomFilter = BloomFilter.create(Funnels.integerFunnel(), batch);

        for (int i = 0; i < batch; i++) {
            bloomFilter.put(i);
        }
        int count = 0;
        for (int i = 0; i < batch; i++) {
            if (!bloomFilter.mightContain(i)) {
                System.out.println("failed\t" + i);
                count++;
            }
        }
        System.out.println("失败次数:" + count);

    }
}
