package com.taoyuanx.securitydemo.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.taoyuanx.securitydemo.common.Result;
import com.taoyuanx.securitydemo.common.ResultBuilder;
import com.taoyuanx.securitydemo.exception.SystemExceptionHandler;
import com.taoyuanx.securitydemo.interceptor.RefererHandlerIntercepter;
import com.taoyuanx.securitydemo.interceptor.SimpleAuthHandlerIntercepter;
import com.taoyuanx.securitydemo.security.blacklist.BlackListIpCheck;
import com.taoyuanx.securitydemo.security.blacklist.DefaultBlackListIpCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dushitaoyuan
 * @desc mvc配置
 * @date 2019/8/29
 */
@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    GlobalConfig globalConfig;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(globalConfig.getSystemDomain());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/public/**").addResourceLocations("classpath:/public/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/");
    }

    /**
     * 拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * 匹配路径按需设定
         */
        registry.addInterceptor(refererHandlerIntercepter()).addPathPatterns("/**")
                .excludePathPatterns("/static/**").excludePathPatterns("/public/**");
        registry.addInterceptor(simpleAuthHandlerIntercepter()).addPathPatterns("/**")
                .excludePathPatterns("/static/**").excludePathPatterns("/public/**");
    }

    /**
     * 异常处理
     *
     * @param resolvers
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(systemExceptionHandler());
    }

    /**
     * 消息转换
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON);
        fastJsonConfig.setCharset(Charset.forName("UTF-8"));
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastJsonHttpMessageConverter);
    }


    /**
     * 统一结果处理
     */
    @RestControllerAdvice(basePackages = "com.taoyuanx.securitydemo.controller")
    public static class ResponseHandler implements ResponseBodyAdvice<Object> {

        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            return true;
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
            if (body instanceof Result) {
                return body;
            }
            if (body instanceof ResponseEntity) {
                return body;
            }
            return ResultBuilder.success(body);
        }

    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SystemExceptionHandler systemExceptionHandler() {
        return new SystemExceptionHandler();
    }

    @Bean
    public RefererHandlerIntercepter refererHandlerIntercepter() {
        RefererHandlerIntercepter refererHandlerIntercepter = new RefererHandlerIntercepter(globalConfig.getRefererCheckUrl(), globalConfig.getRefererCheckAllowDomains());
        return refererHandlerIntercepter;
    }

    @Bean
    public SimpleAuthHandlerIntercepter simpleAuthHandlerIntercepter() {
        SimpleAuthHandlerIntercepter refererHandlerIntercepter = new SimpleAuthHandlerIntercepter();
        return refererHandlerIntercepter;
    }

    @Bean
    public BlackListIpCheck blackListIpCheck() {
        BlackListIpCheck blackListIpCheck = new DefaultBlackListIpCheck(globalConfig.getBlackListIp());
        return blackListIpCheck;
    }

}
