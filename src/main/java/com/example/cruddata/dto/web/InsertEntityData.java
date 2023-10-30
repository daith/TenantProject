package com.example.cruddata.dto.web;

import com.example.cruddata.entity.system.ColumnConfig;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InsertEntityData {
    String tableName;
    List<ColumnConfig> columnNameList;
    List<Map<String,Object>> recordList;
}
