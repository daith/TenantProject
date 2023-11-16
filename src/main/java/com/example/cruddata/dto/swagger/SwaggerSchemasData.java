package com.example.cruddata.dto.swagger;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SwaggerSchemasData {
    String type;
    Map<String, Map<String,String>> properties;
}
