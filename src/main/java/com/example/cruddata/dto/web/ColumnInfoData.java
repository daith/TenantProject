package com.example.cruddata.dto.web;

import lombok.Data;

@Data
public class ColumnInfoData {
    public String dataType;
    public String name;
    public String defaultValue;
    public Boolean nullable;
    public Boolean autoIncrement;
    public String indexType;
    public String caption;
    public Integer length;


}
