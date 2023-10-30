package com.example.cruddata.dto.web;

import lombok.Data;

import java.util.List;

@Data
public class CreateEntityData {

    public String tableName;

    public Long datasourceId;

    public String caption;
    public List<ColumnInfoData> columnEntityList;
}
