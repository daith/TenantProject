package com.example.cruddata.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="datasourceone.datasource")
@Data
public class DataSourceOneConfig {

    private String url;
    private String password;
    private String username;
}
