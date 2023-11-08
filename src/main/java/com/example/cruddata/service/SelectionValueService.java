package com.example.cruddata.service;

import com.example.cruddata.entity.system.SelectionValue;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface SelectionValueService  {

    public void resetDataSourceRedisData() throws JsonProcessingException;

    public  Object mappingSystemSelectionValue(Long tenantId , String tableName , String column , Object clientValue) ;

    public void saveEntity(SelectionValue selectionValue);

}
