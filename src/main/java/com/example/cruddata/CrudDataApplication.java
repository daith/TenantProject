package com.example.cruddata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@ComponentScan({"com.example", "com.example.cruddata"})
@ServletComponentScan(basePackages = {"com.example"})
@EnableAsync
@SpringBootApplication
public class CrudDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudDataApplication.class, args);
    }

}
