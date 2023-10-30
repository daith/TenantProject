package com.example.cruddata.service;

import com.example.cruddata.dto.web.CreateEntityData;
import com.example.cruddata.dto.web.InsertEntityData;
import com.example.cruddata.entity.system.ColumnConfig;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DataService {
    List<Map<String, Object>> list();

    void createTable(Long dataSource , String dbName  , CreateEntityData createEntity) throws SQLException;

    void insertData(Long dataSourceId , String dbName  , InsertEntityData insertDataEntity ) throws SQLException;

    void dropColumns(Map<String,List<String>> columnConfigsByTableName)  throws SQLException;
}
