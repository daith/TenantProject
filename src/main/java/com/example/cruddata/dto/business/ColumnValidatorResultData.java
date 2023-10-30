package com.example.cruddata.dto.business;

import com.example.cruddata.entity.system.ColumnConfig;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ColumnValidatorResultData {

    Map<Long, List<String>> notExitTableByTenant ;
    Map<Long,Map<String,Map<String,List<String>>>> totalColumnsBySituationTableTenant;

    Map<String,List<ColumnConfig>> existColumnsByTableName;
    boolean isValidateCorrect;
}
