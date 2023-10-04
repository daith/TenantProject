package com.example.cruddata.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(FlywayProperties.class)
@ConfigurationProperties(prefix = "spring.flyway.hikari")
public class FlywayConfig {
    private static final Logger log = LoggerFactory.getLogger(FlywayConfig.class);
}
