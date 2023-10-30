package com.example.cruddata.service;

import com.example.cruddata.dto.web.CreateEntityData;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.entity.system.TableConfig;

import java.sql.SQLException;
import java.util.List;

public interface SystemService {


    public void deleteColumnConfig(List<ColumnConfig> recordList) throws SQLException;

    public void createTable(CreateEntityData createEntity, Long tenantId) throws SQLException;


    public void deleteTableConfig(TableConfig recordList);

    public void updateColumnConfig(List<ColumnConfig> recordList);

    public void updateTableConfig(TableConfig recordList);


    public List<DataSourceConfig> getDataSourceConfigs(DataSourceConfig dataSourceConfig);


    public List<ColumnConfig> getColumnConfigs(TableConfig recordList);

    public List<TableConfig> getTableConfigs(Long dataSourceId, String tableName, Long tenantId);


}
