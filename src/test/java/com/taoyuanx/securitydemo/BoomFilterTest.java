package com.taoyuanx.securitydemo;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dushitaoyuan
 * @desc 布隆过滤测试
 * @date 2019/8/30
 */
public class BoomFilterTest {

    @Test
    public void failedCountTest() {
        int batch = 100000;
        BloomFilter bloomFilter = BloomFilter.create(Funnels.integerFunnel(), batch, 0.0001);

        for (int i = 0; i < batch; i++) {
            bloomFilter.put(i);
        }
        int count = 0, max = batch * 10;
        for (int i = 0; i < max; i++) {
            if (i < batch && !bloomFilter.mightContain(i)) {
                System.out.println("误判 \t" + i);
                count++;
            }
            if (i > batch && bloomFilter.mightContain(i)) {
                System.out.println("误判 \t" + i);
                count++;
            }
        }
        System.out.println("失败次数:" + count + "错误率:" + PercentUtil.percent(Double.valueOf(count), Double.valueOf(max), 4));

    }


    @Test
    public void testEl() {
        Map<String, String> args3 = new HashMap<>();
        args3.put("args1", "args3_1");
        args3.put("args2", "args3_2");
        args3.put("args3", "args3_3");
        Object[] args = new Object[]{"args1", 2, args3};
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("args", args);
        Expression expression = parser.parseExpression("#args[0]");
        System.out.println(expression.getValue(context, String.class));


        expression = parser.parseExpression("#args[1]");
        System.out.println(expression.getValue(context, String.class));

        expression = parser.parseExpression("#args[2].get('args1')");
        System.out.println(expression.getValue(context, String.class));

        expression = parser.parseExpression("#args[2].get('args1')+#args[2].get('args2')");
        System.out.println(expression.getValue(context, String.class));
    }
}
