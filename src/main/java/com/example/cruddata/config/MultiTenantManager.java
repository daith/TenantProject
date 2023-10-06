package com.example.cruddata.config;


import com.example.cruddata.exception.TenantNotFoundException;
import com.example.cruddata.exception.TenantResolvingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.lang.String.format;


@Slf4j
@Configuration
public class MultiTenantManager {

    private final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private final Map<Object, Object> tenantDataSources = new ConcurrentHashMap<>();
    private final DataSourceProperties properties;

    private Function<String, DataSourceProperties> tenantResolver;

    private AbstractRoutingDataSource multiTenantDataSource;

    public MultiTenantManager(DataSourceProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DataSource dataSource() {

        multiTenantDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return currentTenant.get();
            }
        };
        multiTenantDataSource.setTargetDataSources(tenantDataSources);
        multiTenantDataSource.setDefaultTargetDataSource(defaultDataSource());
        multiTenantDataSource.afterPropertiesSet();
        return multiTenantDataSource;
    }

    public void setTenantResolver(Function<String, DataSourceProperties> tenantResolver) {
        this.tenantResolver = tenantResolver;
    }

    public void setCurrentTenant(String tenantId) throws SQLException, TenantNotFoundException, TenantResolvingException {
        if (tenantIsAbsent(tenantId)) {
            if (tenantResolver != null) {
                DataSourceProperties properties;
                try {
                    properties = tenantResolver.apply(tenantId);
                    log.debug("[d] Datasource properties resolved for tenant ID '{}'", tenantId);
                } catch (Exception e) {
                    throw new TenantResolvingException(e, "Could not resolve the tenant!");
                }

                String url = properties.getUrl();
                String username = properties.getUsername();
                String password = properties.getPassword();
                String driverClassName = properties.getDriverClassName();

                addTenant(tenantId, url, username,password, driverClassName);
            } else {
                throw new TenantNotFoundException(format("Tenant %s not found!", tenantId));
            }
        }
        currentTenant.set(tenantId);
        log.debug("[d] Tenant '{}' set as current.", tenantId);
    }

    public void addTenant(String tenantId, String url, String username, String password,String driver) throws SQLException {

        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName(driver)
                .url(url)
                .username(username)
                .password(password)
                .build();

        // Check that new connection is 'live'. If not - throw exception
        try(Connection c = dataSource.getConnection()) {
            tenantDataSources.put(tenantId, dataSource);
            multiTenantDataSource.afterPropertiesSet();
            log.debug("[d] Tenant '{}' added.", tenantId);
        }
    }

    public DataSource removeTenant(String tenantId) {
        Object removedDataSource = tenantDataSources.remove(tenantId);
        multiTenantDataSource.afterPropertiesSet();
        return (DataSource) removedDataSource;
    }

    public boolean tenantIsAbsent(String tenantId) {
        return !tenantDataSources.containsKey(tenantId);
    }

    public Collection<Object> getTenantList() {
        return tenantDataSources.keySet();
    }

    private DriverManagerDataSource defaultDataSource() {
        DriverManagerDataSource defaultDataSource = new DriverManagerDataSource();
        defaultDataSource.setDriverClassName("org.h2.Driver");
        defaultDataSource.setUrl("jdbc:h2:mem:default");
        defaultDataSource.setUsername("default");
        defaultDataSource.setPassword("default");
        return defaultDataSource;
    }


}