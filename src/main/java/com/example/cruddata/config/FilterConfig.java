package com.example.cruddata.config;

import com.example.cruddata.filter.BasicAuthHeaderFilter;
import com.example.cruddata.filter.LogProcessTimeFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean logProcessTimeFilter() {
        FilterRegistrationBean<LogProcessTimeFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LogProcessTimeFilter());
        bean.addUrlPatterns("/api/*");
        bean.setName("logProcessTimeFilter");

        return bean;
    }

    @Bean
    public FilterRegistrationBean customerHeaderFilter() {
        FilterRegistrationBean<BasicAuthHeaderFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new BasicAuthHeaderFilter());
        bean.addUrlPatterns("/api/*");
        bean.setName("customHeaderFilter");

        return bean;
    }
}
