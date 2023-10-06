package com.example.cruddata.service;

import com.example.cruddata.dto.ColumnCreated;
import com.example.cruddata.dto.CreateEntityInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface TableService {
    List<Map<String, Object>> list();

    void createTable(String dbName  , CreateEntityInfo createEntityInfo) throws SQLException;
}
