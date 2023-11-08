package com.example.cruddata.dto.web;

import lombok.Data;

import java.util.List;

@Data
public class CreateEntityData {

    public String tableName;

    public String moduleName;

    public Long datasourceId;

    public String caption;
    public String category;

    public List<String> fkTables;
    public List<ColumnInfoData> columnEntityList;
}
