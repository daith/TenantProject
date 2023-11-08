package com.example.cruddata.dto.swagger;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SwaggerDocData {
    String openapi;
    SwaggerInfoData info;
    List<SwaggerServerData> servers;
    List<SwaggerTagData>  tags;
    Map<String, Map<String,SwaggerUrlEntityData>> paths;

    Map<String, Map<String,SwaggerUrlEntityData>> components;
}
