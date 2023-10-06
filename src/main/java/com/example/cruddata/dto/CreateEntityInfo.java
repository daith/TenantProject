package com.example.cruddata.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateEntityInfo {

    public String tableName;

    public String caption;
    public List<ColumnCreated> columnEntityList;
}
