package com.example.cruddata.dto.swagger;

import lombok.Data;

@Data
public class SwaggerSecurityDefinitionData {

    String bearer;
    String type;
    String name;
    String in;
    String description;

}
