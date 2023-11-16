package com.example.cruddata.service;

import com.example.cruddata.entity.authroty.Function;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DynamicService {

    public List<?> getAllDataFromTable(Function function) throws SQLException;

    public List<?> getDataImport(Function function,List<Map<String,Object>> dataImport) throws SQLException ;
}
