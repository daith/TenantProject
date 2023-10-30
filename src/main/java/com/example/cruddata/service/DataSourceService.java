package com.example.cruddata.service;

import com.example.cruddata.entity.system.DataSourceConfig;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;
import java.util.Map;

public interface DataSourceService {
    List<Map<String, Object>> list();

    public void deleteDataSourceConfig(DataSourceConfig recordList);

    public void updateDataSourceConfig(DataSourceConfig recordList);

    public void createDataSourceConfig(DataSourceConfig recordList);

    public List<DataSourceConfig> getAllDataSourceByCondition(Boolean idDelete,String Status,Long tenantId);

    public void resetDataSourceRedisData() throws JsonProcessingException;



}
