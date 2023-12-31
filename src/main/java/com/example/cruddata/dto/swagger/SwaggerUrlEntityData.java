package com.example.cruddata.dto.swagger;

import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
public class SwaggerUrlEntityData {

    List<String> tags;
    String summary;
    String operationId;

    String description;
    List<Map<String,Object> > parameters;
    Map<String,Map<String,Map<String,Map<String,String>>>>  requestBody;
    Map<String, SwaggerUrlContentSchemaData> responses;

}
