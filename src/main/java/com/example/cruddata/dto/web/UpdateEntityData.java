package com.example.cruddata.dto.web;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UpdateEntityData {
    public Integer totalRecordCount;
    public List<Map> recordList;
}
