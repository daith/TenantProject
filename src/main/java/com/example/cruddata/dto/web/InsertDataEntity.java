package com.example.cruddata.dto.web;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class InsertDataEntity {
    List<ColumnInfo> columnNameList;
    List<Map<String,Object>> recordList;
}
