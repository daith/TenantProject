package com.example.cruddata.filter;

import com.example.cruddata.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.Enumeration;

@Component
public class AuthHandlerInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthHandlerInterceptor.class);


    @Autowired
    public AccountService accountService;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        Enumeration<String> headerNames = request.getHeaderNames();
        String strRequestURI = request.getRequestURI();

        if(!(strRequestURI.contains("swagger") ||
        strRequestURI.contains("/api-docs")||
                strRequestURI.contains("/favicon.ico"))){
            String token ="";

            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String value =request.getHeader(name);
                    System.out.println("Header: " + name + " value:" + value);

                    if(value.toLowerCase().contains("bearer ")){
                        token = value.split(" ")[1];
                    }
                }
            }

            if(accountService.tokenValidationNotExist(token)){


                // 設定Response編碼為UTF-8
                response.setCharacterEncoding("UTF-8");
                // 設定Response的ContentType
                response.setContentType("application/json; charset=utf-8");
                // 驗證失敗，設定Response的狀態為401
                response.setStatus(401);

                // 將Object轉換成JSON字串
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonRspBody = objectMapper.writeValueAsString( ResponseEntity.ok("account token not correct!!"));

                // 宣告PrintWriter來回傳內容
                PrintWriter printWriter = response.getWriter();
                printWriter.append(jsonRspBody);
                printWriter.close();
                log.info("Request: {} {} , Msg: {}", "401", strRequestURI, "Auth Fail.");
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
