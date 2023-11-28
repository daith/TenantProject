package com.example.cruddata.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

public class LogProcessTimeFilter  extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        httpServletRequest = new ContentCachingRequestWrapper(httpServletRequest, 1024); // 最大長度取1024，依需要自行調整數字

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}
