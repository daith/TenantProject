package com.example.cruddata.config;

import com.example.cruddata.util.DataSourceEnum;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataSourceContextHolder {

    private static ThreadLocal<DataSourceEnum> threadLocal;

    private final ThreadLocal<String> currentTenant = new ThreadLocal<>();
    private final Map<Object, Object> tenantDataSources = new ConcurrentHashMap<>();
    private  DataSourceProperties properties;

    private AbstractRoutingDataSource multiTenantDataSource;

    public DataSourceContextHolder(DataSourceProperties properties) {
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

    public void addTenant(String tenantId, String url, String username, String password) throws SQLException {

        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName(properties.getDriverClassName())
                .url(url)
                .username(username)
                .password(password)
                .build();

        // Check that new connection is 'live'. If not - throw exception
        try(Connection c = dataSource.getConnection()) {
            tenantDataSources.put(tenantId, dataSource);
            multiTenantDataSource.afterPropertiesSet();
        }
    }

    public void setCurrentTenant(String tenantId) {
        currentTenant.set(tenantId);
    }

    private DriverManagerDataSource defaultDataSource() {
        DriverManagerDataSource defaultDataSource = new DriverManagerDataSource();
        defaultDataSource.setDriverClassName("org.h2.Driver");
        defaultDataSource.setUrl("jdbc:h2:mem:default");
        defaultDataSource.setUsername("default");
        defaultDataSource.setPassword("default");
        return defaultDataSource;
    }

    public DataSourceContextHolder() {
        threadLocal = new ThreadLocal<>();
    }

    public void setBranchContext(DataSourceEnum dataSourceEnum) {
        threadLocal.set(dataSourceEnum);
    }

    public DataSourceEnum getBranchContext() {
        return threadLocal.get();
    }

    public static void clearBranchContext() {
        threadLocal.remove();
    }
}
