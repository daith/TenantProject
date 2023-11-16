
package com.example.cruddata.filter;

import com.example.cruddata.service.AccountService;
import com.example.cruddata.service.imp.AccountServiceImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;


public class BasicAuthHeaderFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(BasicAuthHeaderFilter.class);


    public AccountService accountService;

    private ServletContext context;

    public AccountService getAccountService(){

        if(null == accountService){
            WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(context);

            accountService =  ((AccountService)   wac.getBean(AccountServiceImp.class));
        }
        return accountService;
    }



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        this.context = filterConfig.getServletContext();
        this.context.log("AuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse= (HttpServletResponse) servletResponse;
        String strRequestURI = httpRequest.getRequestURI();

        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        String token ="";

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value =httpRequest.getHeader(name);
                System.out.println("Header: " + name + " value:" + value);

                if(value.toLowerCase().contains("bearer ")){
                    token = value.split(" ")[1];
                }
            }
        }

        if(getAccountService().tokenValidationNotExist(token)){
            // 設定Response編碼為UTF-8
            httpResponse.setCharacterEncoding("UTF-8");
            // 設定Response的ContentType
            httpResponse.setContentType("application/json; charset=utf-8");
            // 驗證失敗，設定Response的狀態為401
            httpResponse.setStatus(401);

            // 將Object轉換成JSON字串
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRspBody = objectMapper.writeValueAsString( ResponseEntity.ok("account token not correct!!"));

            // 宣告PrintWriter來回傳內容
            PrintWriter printWriter = servletResponse.getWriter();
            printWriter.append(jsonRspBody);
            printWriter.close();
            log.info("Request: {} {} , Msg: {}", "401", strRequestURI, "Auth Fail.");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }


}