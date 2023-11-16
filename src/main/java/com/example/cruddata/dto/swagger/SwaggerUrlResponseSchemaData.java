package com.example.cruddata.dto.swagger;

import lombok.Data;

import java.util.Map;
@Data
public class SwaggerUrlResponseSchemaData {

    String type;
    Map<String,String> items;
}
