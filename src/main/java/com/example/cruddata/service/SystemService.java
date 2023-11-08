package com.example.cruddata.service;

import com.example.cruddata.dto.web.CreateEntityData;
import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.DataSourceConfig;
import com.example.cruddata.entity.system.TableConfig;

import java.sql.SQLException;
import java.util.List;

public interface SystemService {



    public void createTable(CreateEntityData createEntity, Long tenantId) throws SQLException;

    public List<TableConfig> getTableConfigs(Long dataSourceId, String tableName, Long tenantId);

    public void deleteColumnConfig(Long dataSourceId,List<ColumnConfig> recordList) throws SQLException;


}
