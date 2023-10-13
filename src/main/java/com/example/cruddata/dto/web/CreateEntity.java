package com.example.cruddata.dto.web;

import lombok.Data;

import java.util.List;

@Data
public class CreateEntity {

    public String tableName;

    public Long datasourceId;

    public String caption;
    public List<ColumnInfo> columnEntityList;
}
