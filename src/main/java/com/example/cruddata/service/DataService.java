package com.example.cruddata.service;

import com.example.cruddata.dto.web.CreateEntity;
import com.example.cruddata.entity.system.ColumnConfig;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DataService {
    List<Map<String, Object>> list();

    void createTable(String dbName  , CreateEntity createEntity) throws SQLException;

    void insertData(String dbName  , List<Map<String,Object>> recordList) throws SQLException;

    void dropColumns(Map<String,List<String>> columnConfigsByTableName)  throws SQLException;
}
