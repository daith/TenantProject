package com.example.cruddata.service;

import com.example.cruddata.dto.web.CreateEntity;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.entity.system.TableConfig;

import java.sql.SQLException;
import java.util.List;

public interface SystemService {


    public void deleteColumnConfig(List<ColumnConfig> recordList) throws SQLException;

    public void deleteDataSourceConfig(DataSourceConfig recordList);

    public void deleteTableConfig(TableConfig recordList);

    public void updateColumnConfig(List<ColumnConfig> recordList);

    public void updateDataSourceConfig(DataSourceConfig recordList);

    public void updateTableConfig(TableConfig recordList);

    public void createTable(CreateEntity createEntity, Long tenantId) throws SQLException;

    public List<DataSourceConfig> getDataSourceConfigs(DataSourceConfig dataSourceConfig);

    public List<TableConfig> getTableConfigs(TableConfig recordList);

    public List<ColumnConfig> getColumnConfigs(TableConfig recordList);


}
