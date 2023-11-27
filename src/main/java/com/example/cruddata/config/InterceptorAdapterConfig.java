package com.example.cruddata.config;

import com.example.cruddata.filter.AuthHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorAdapterConfig implements WebMvcConfigurer {

    @Bean
    public AuthHandlerInterceptor setauthHandlerInterceptor(){
        return new  AuthHandlerInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //注册自己的拦截器并设置拦截的请求路径
        registry.addInterceptor(setauthHandlerInterceptor()).addPathPatterns("/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }

}
