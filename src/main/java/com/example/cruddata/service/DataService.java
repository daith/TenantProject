package com.example.cruddata.service;

import com.example.cruddata.dto.web.CreateEntityData;
import com.example.cruddata.dto.web.DeleteEntityData;
import com.example.cruddata.dto.web.InsertEntityData;
import com.example.cruddata.dto.web.SampleSelectionEntityData;
import com.example.cruddata.entity.system.ColumnConfig;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DataService {


    void createTable(Long dataSource , String dbName  , CreateEntityData createEntity) throws SQLException;

    void dropTable(Long dataSource , String dbName, String tableName ) throws SQLException;

    void insertData(Long dataSourceId , String dbName  , InsertEntityData insertDataEntity ) throws SQLException;

    void dropColumns(Long dataSourceId,Map<String,List<String>> columnConfigsByTableName)  throws SQLException;

    List<Map<String,Object>> queryDataBySampleCondition(Long dataSourceId , String dbName  , SampleSelectionEntityData entityData) throws SQLException;

    void deleteData(Long dataSourceId , String dbName  , DeleteEntityData records) throws SQLException;

}
