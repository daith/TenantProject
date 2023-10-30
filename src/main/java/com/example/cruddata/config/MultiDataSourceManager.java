package com.example.cruddata.config;


import com.example.cruddata.constant.RedisKeyConsts;
import com.example.cruddata.entity.account.Tenant;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.exception.TenantNotFoundException;
import com.example.cruddata.exception.TenantResolvingException;
import com.example.cruddata.repository.system.DataSourceConfigRepository;
import com.example.cruddata.util.RedisUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.lang.String.format;


@Slf4j
@Configuration
public class MultiDataSourceManager {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    DataSourceConfigRepository dataSourceConfigRepository;
    private final ThreadLocal<String> currentDataSource = new ThreadLocal<>();

    private final Map<Object,Object> dataSources = new ConcurrentHashMap<>();
    private final DataSourceProperties properties;

    private Function<String, DataSourceProperties> dataSourceResolver;

    private AbstractRoutingDataSource multiTenantDataSource;

    public MultiDataSourceManager(DataSourceProperties properties) {
        this.properties = properties;
    }

    @Bean
    public DataSource dataSource() {

        multiTenantDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
                return currentDataSource.get();
            }
        };
        multiTenantDataSource.setTargetDataSources(dataSources);
        multiTenantDataSource.afterPropertiesSet();
        return multiTenantDataSource;
    }

    public void setDataSourceResolver(Function<String, DataSourceProperties> dataSourceResolver) {
        this.dataSourceResolver = dataSourceResolver;
    }

    public void setCurrentDataSource(String dataSourceId) throws SQLException, TenantNotFoundException, TenantResolvingException {
        if (checkDataSourceIsAbsent(dataSourceId)) {

                DataSourceConfig  dataSourceConfig  = null;
                if(redisUtil.isConnected() && null != redisUtil.getHashEntries(RedisKeyConsts.DATA_SOURCE)&&  redisUtil.getHashEntries(RedisKeyConsts.DATA_SOURCE).size() >0){

                    dataSourceConfig = (DataSourceConfig) redisUtil.getHashEntries(RedisKeyConsts.DATA_SOURCE).get(dataSourceId);

                }else {
                    Optional<DataSourceConfig> dataSourceConfigOptional =  dataSourceConfigRepository.findById(Long.valueOf(dataSourceId));
                    dataSourceConfig = dataSourceConfigOptional.get();
                }

                extractedDataSource( dataSourceConfig);

        }
        currentDataSource.set(dataSourceId);
        log.debug("[d] Tenant '{}' set as current.", dataSourceId);
    }

    public void addDataSource(String dataSourceId, String url, String username, String password, String driver) throws SQLException {

        DataSource dataSource = DataSourceBuilder.create()
                .driverClassName(driver)
                .url(url)
                .username(username)
                .password(password)
                .build();

        // Check that new connection is 'live'. If not - throw exception
        try(Connection c = dataSource.getConnection()) {
            dataSources.put(dataSourceId, dataSource);
            multiTenantDataSource.afterPropertiesSet();
            log.debug("[d] Tenant '{}' added.", dataSourceId);
        }
    }

    public DataSource removeTenant(String dataSourceId) {
        Object removedDataSource = dataSources.remove(dataSourceId);
        multiTenantDataSource.afterPropertiesSet();
        return (DataSource) removedDataSource;
    }

    public boolean checkDataSourceIsAbsent(String tenantId) {
        return !dataSources.containsKey(tenantId);
    }

    public Collection<Object> getTenantList() {
        return dataSources.keySet();
    }

    public void initDataSource(){
        if(redisUtil.isConnected() && null != redisUtil.getHashEntries(RedisKeyConsts.DATA_SOURCE)){

           Map<Object, Object> result =   redisUtil.getHashEntries(RedisKeyConsts.DATA_SOURCE);
            result.forEach((key,object)->{
                DataSourceConfig dataSourceConfig = (DataSourceConfig) object;

                extractedDataSource( dataSourceConfig);
            });


        }else {
            List<DataSourceConfig> dataSourceConfigList =  dataSourceConfigRepository.findAll();
            dataSourceConfigList.forEach(item->{
                extractedDataSource( item);
            });
        }



    }

    private void extractedDataSource( DataSourceConfig dataSourceConfig) {
        String url = dataSourceConfig.getUrl();
        String username = dataSourceConfig.getUsername();
        String password = dataSourceConfig.getPassword();
        String driverClassName = dataSourceConfig.getDriverClassName();

        try {
            addDataSource(dataSourceConfig.getId().toString(), url, username,password, driverClassName);
        } catch (SQLException e) {
            e.printStackTrace();
//                    throw new RuntimeException(e);
        }
    }

}
