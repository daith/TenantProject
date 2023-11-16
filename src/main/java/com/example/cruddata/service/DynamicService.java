package com.example.cruddata.service;

import com.example.cruddata.entity.authroty.Function;

import java.sql.SQLException;
import java.util.List;

public interface DynamicService {

    public List<?> getAllDataFromTable(Function function) throws SQLException;

}
