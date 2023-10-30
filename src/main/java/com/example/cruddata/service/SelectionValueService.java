package com.example.cruddata.service;

import com.example.cruddata.entity.BasicEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


public interface SelectionValueService  {

    public void resetDataSourceRedisData() throws JsonProcessingException;

    public  Object mappingSystemSelectionValue(Long tenantId , String tableName , String column , Object clientValue) ;


}
