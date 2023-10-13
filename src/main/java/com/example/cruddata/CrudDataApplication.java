package com.example.cruddata;

import com.example.cruddata.config.MultiTenantManager;
import com.example.cruddata.controller.BusinessController;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

import static java.lang.String.format;

@ComponentScan({"com.example", "com.example.cruddata"})
@ServletComponentScan(basePackages = {"com.example"})
@EnableJpaRepositories({"com.example.cruddata.repository"})
@EnableAsync
@SpringBootApplication
public class CrudDataApplication {

    private final MultiTenantManager tenantManager;

    private static final Logger log = LoggerFactory.getLogger(CrudDataApplication.class);


    public CrudDataApplication(MultiTenantManager tenantManager) {
        this.tenantManager = tenantManager;
        this.tenantManager.setTenantResolver(CrudDataApplication::tenantResolver);
    }

    /**
     * Load tenant datasource properties from the folder 'tenants/onStartUp`
     * when the app has started.
     */
    @SneakyThrows(IOException.class)
    @EventListener
    public void onReady(ApplicationReadyEvent event) {

        File[] files = Paths.get("tenants/onStartUp").toFile().listFiles();

        if (files == null) {
            log.warn("[!] Tenant property files not found at ./tenants/onStartUp folder!");
            return;
        }

        for (File propertyFile : files) {
            Properties tenantProperties = new Properties();
            tenantProperties.load(new FileInputStream(propertyFile));

            String tenantId = tenantProperties.getProperty("id");
            String url = tenantProperties.getProperty("url");
            String username = tenantProperties.getProperty("username");
            String password = tenantProperties.getProperty("password");
            String drive = tenantProperties.getProperty("drive");

            try {
                tenantManager.addTenant(tenantId, url, username, password,drive);
                log.info("[i] Loaded DataSource for tenant '{}'.", tenantId);
            } catch (SQLException e) {
                log.error(format("[!] Could not load DataSource for tenant '%s'!", tenantId), e);
            }
        }
    }

    /**
     * Example of the tenant resolver - load the given tenant datasource properties
     * from the folder 'tenants/atRuntime'
     *
     * @param tenantId tenant id
     * @return tenant DataSource
     */
    private static DataSourceProperties tenantResolver(String tenantId) {

        File[] files = Paths.get("tenants/atRuntime").toFile().listFiles();

        if (files == null) {
            String msg = "[!] Tenant property files not found at ./tenants/atRuntime folder!";
            log.error(msg);
            throw new RuntimeException(msg);
        }

        for (File propertyFile : files) {
            Properties tenantProperties = new Properties();
            try {
                tenantProperties.load(new FileInputStream(propertyFile));
            } catch (IOException e) {
                String msg = "[!] Could not read tenant property file at ./tenants/atRuntime folder!";
                log.error(msg);
                throw new RuntimeException(msg, e);
            }

            String id = tenantProperties.getProperty("id");
            if (tenantId.equals(id)) {
                DataSourceProperties properties = new DataSourceProperties();
                properties.setUrl(tenantProperties.getProperty("url"));
                properties.setUsername(tenantProperties.getProperty("username"));
                properties.setPassword(tenantProperties.getProperty("password"));
                return properties;
            }
        }
        String msg = "[!] Any tenant property files not found at ./tenants/atRuntime folder!";
        log.error(msg);
        throw new RuntimeException(msg);
    }

    public static void main(String[] args) {
        SpringApplication.run(CrudDataApplication.class, args);
    }

}
