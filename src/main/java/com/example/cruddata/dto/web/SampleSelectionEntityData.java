package com.example.cruddata.dto.web;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SampleSelectionEntityData {

    public String tableName;

    public List<String> columnSelectedList;

    public Map<String,String> conditionForEq;


}
