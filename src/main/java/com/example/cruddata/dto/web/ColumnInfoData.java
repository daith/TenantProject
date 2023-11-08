package com.example.cruddata.dto.web;

import lombok.Data;

@Data
public class ColumnInfoData {
    public String dataType;
    public String name;
    public String order;
    public String defaultValue;
    public Boolean nullable;
    public Boolean autoIncrement;
    public String caption;
    public String length;
    public String pkType;
    public String fkTableName;
    public String fkColumn;


}
