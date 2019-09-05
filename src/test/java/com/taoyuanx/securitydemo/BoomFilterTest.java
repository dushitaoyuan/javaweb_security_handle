package com.taoyuanx.securitydemo;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
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
    @Test
    public  void testEl(){
        String el="${m}";
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("m","1234");
        Expression expression = parser.parseExpression(el);


        System.out.println(expression.getValue(context, String.class));

        // 定义变量
      /*  String name = "Tom";
        EvaluationContext context = new StandardEvaluationContext();  // 表达式的上下文,
        context.setVariable("myName", name);                        // 为了让表达式可以访问该对象, 先把对象放到上下文中
        ExpressionParser parser = new SpelExpressionParser();
        System.out.println( parser.parseExpression("#myName").getValue(context, String.class));;   // Tom , 使用变量

*/
    }
}
