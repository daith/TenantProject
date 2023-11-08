package com.example.cruddata.service;

import com.example.cruddata.entity.system.ColumnConfig;
import com.example.cruddata.entity.system.TableConfig;

import java.sql.SQLException;
import java.util.List;

public interface TableColumnService {

    List<ColumnConfig> getActiveColumnByTenantIdAndTableId(Long tenantId , Long tableId);

    List<TableConfig> getActiveTablesByTenantIdAndDataSourceId(Long tenantId , Long dataSourceId);

    void saveTableConfig(TableConfig tableConfig);

    void saveColumnConfig(ColumnConfig columnConfig);

    void deleteTableConfig(Long tenantId , Long dataSourceId , String tableName) throws SQLException;

    void deleteAllTableByDataSource(Long tenantId , Long dataSourceId);
}
