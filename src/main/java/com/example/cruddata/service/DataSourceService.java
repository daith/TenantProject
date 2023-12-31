package com.example.cruddata.service;

import com.example.cruddata.entity.system.DataSourceConfig;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface DataSourceService {


    public void createDataSourceConfig(DataSourceConfig recordList);

    public List<DataSourceConfig> getAllDataSourceByCondition(Boolean idDelete,String Status,Long tenantId);

    public DataSourceConfig getDataSourceById(Long dataSourceId);

    public void resetDataSourceRedisData() throws JsonProcessingException;



}
