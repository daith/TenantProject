package com.example.cruddata.dto.swagger;

import lombok.Data;

import java.util.Map;
@Data
public class SwaggerUrlContentSchemaData {

    String description;
    Map<String, Map<String,SwaggerUrlResponseSchemaData>> content;

//       '*/*':
            //    schema:
                //      type: array
                    //      items: object
                        //  $ref: '#/components/schemas/SystemConfigEntityData'
}
